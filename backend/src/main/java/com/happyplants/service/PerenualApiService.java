package com.happyplants.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyplants.model.dto.ApiResponse;
import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PlantDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class PerenualApiService {

    @Value("${plant.api.token}")
    private String plantApiKey;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<PlantDTO> search(String name) {
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            String url = String.format(
                    "https://perenual.com/api/v2/species-list?q=%s&page=1&hardiness=4-8&key=%s",
                    encodedName, plantApiKey
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return getPlantResults(response);

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

    public List<PlantDTO> getPlantResults(HttpResponse<String> response) {
        try {
            ApiResponse apiResponse = mapper.readValue(response.body(), ApiResponse.class);
            return apiResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse plant results", e);
        }
    }
}
