package com.happyplants.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyplants.model.dto.ApiResponse;
import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PerenualSearchPlantDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PerenualApiService {

    @Value("${plant.api.token}")
    private String plantApiKey;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<Integer, PerenualSearchPlantDTO> searchCache = new ConcurrentHashMap<>();

    public List<PerenualSearchPlantDTO> search(String name) {
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            String url = String.format(
                    "https://perenual.com/api/v2/species-list?q=%s&page=1&key=%s",
                    encodedName, plantApiKey
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<PerenualSearchPlantDTO> results = getPlantResults(response);

            searchCache.clear();
            for (PerenualSearchPlantDTO plant : results) {
                searchCache.put(plant.perenualId(), plant);
            }

            return results;

        } catch (Exception e) {
            throw new RuntimeException("Failed to search plants", e);
        }
    }

    public PerenualPlantDTO getPlantById(int id) {
        try {
            String url = String.format(
                    "https://perenual.com/api/v2/species/details/%d?key=%s",
                    id, plantApiKey
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            try {
                return mapper.readValue(response.body(), PerenualPlantDTO.class);
            } catch (Exception e) {
                return getPartialPlantById(id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch plant details", e);
        }
    }

    public List<PerenualSearchPlantDTO> getPlantResults(HttpResponse<String> response) {
        try {
            ApiResponse apiResponse = mapper.readValue(response.body(), ApiResponse.class);
            return apiResponse.data();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse plant results", e);
        }
    }

    private PerenualPlantDTO getPartialPlantById(int id) {

        PerenualSearchPlantDTO pspDTO = searchCache.get(id);
        if (pspDTO == null) { throw new RuntimeException("Plant not found"); }
        return new PerenualPlantDTO(
                pspDTO.perenualId(),
                pspDTO.commonName(),
                pspDTO.scientificName(),
                pspDTO.familyName(),
                pspDTO.cultivar(),
                pspDTO.speciesEpithet(),
                pspDTO.genus(),
                null,
                null,
                null
        );
    }
}
