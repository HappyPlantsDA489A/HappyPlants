package com.happyplants.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyplants.dto.ApiResponse;
import com.happyplants.dto.PerenualPlantDTO;
import com.happyplants.dto.PerenualSearchPlantDTO;
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

            return mapper.readValue(response.body(), PerenualPlantDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch plant details", e);
        }
    }

    public List<PerenualSearchPlantDTO> getPlantResults(HttpResponse<String> response) {
        try {
            ApiResponse apiResponse = mapper.readValue(response.body(), ApiResponse.class);
            return apiResponse.data().stream()
                    .map(p -> {
                        String imageUrl = null;
                        if (p.defaultImage() != null) {
                            imageUrl = p.defaultImage().originalUrl();
                            if (imageUrl == null || imageUrl.isBlank()) imageUrl = p.defaultImage().regularUrl();
                            if (imageUrl == null || imageUrl.isBlank()) imageUrl = p.defaultImage().mediumUrl();
                            if (imageUrl == null || imageUrl.isBlank()) imageUrl = p.defaultImage().smallUrl();
                            if (imageUrl == null || imageUrl.isBlank()) imageUrl = p.defaultImage().thumbnail();
                            if (imageUrl != null && imageUrl.isBlank()) imageUrl = null;
                        }
                        return new PerenualSearchPlantDTO(
                                p.perenualId(),
                                p.commonName(),
                                p.scientificName(),
                                p.familyName(),
                                p.cultivar(),
                                p.speciesEpithet(),
                                p.genus(),
                                imageUrl
                        );
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse plant results", e);
        }
    }

    public PerenualPlantDTO getPartialPlantById(int id) {

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
                null,
                null
        );
    }

    public String getWateringDescription(int id) {
        try {
            String url = String.format(
                    "https://perenual.com/api/species-care-guide-list?species_id=%d&key=%s",
                    id, plantApiKey
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());

            JsonNode sections = root.path("data").get(0).path("section");

            for(JsonNode section : sections) {
                if (section.path("type").asText().equals("watering")) {
                    return section.path("description").asText();
                }
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch watering description", e);
        }
    }
}
