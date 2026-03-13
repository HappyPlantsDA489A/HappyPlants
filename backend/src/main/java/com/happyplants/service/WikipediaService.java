package com.happyplants.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyplants.exception.WikipediaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Configuration
public class WikipediaService {
    private final HttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "https://en.wikipedia.org/";

    public WikipediaService(HttpClient client) {
        this.client = client;
    }
    /**
     * Tries to find a Wikipedia image for a plant.
     * Strategy: scientific name first, common name as fallback.
     * Returns null if no image can be found.
     */
    public String getPlantImageUrl(String scientificName, String commonName) {
        String imageUrl = fetchImageUrl(scientificName);
        if (imageUrl != null) return imageUrl;
        return fetchImageUrl(commonName);
    }

    /** Kept for backwards-compatibility (e.g. WikipediaController testing endpoint). */
    public String getPlantImageUrl(String plantName) {
        return fetchImageUrl(plantName);
    }

    public String fetchImageUrl(String plantName) {
        if (plantName == null || plantName.isBlank()) return null;
        try {
            String articleTitle = getFirstArticleTitle(plantName);

            if (articleTitle == null) {
                return null;
            }

            String encodedTitle = URLEncoder.encode(articleTitle.replace(' ', '_'), StandardCharsets.UTF_8);
            String url = baseUrl + "api/rest_v1/page/summary/" + encodedTitle;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .setHeader("User-Agent", "HappyPlants/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());

                // Prefer full-resolution original image
                JsonNode originalImage = root.path("originalimage");
                if (!originalImage.isMissingNode()) {
                    JsonNode source = originalImage.path("source");
                    if (!source.isMissingNode() && !source.isNull()) {
                        return source.asText();
                    }
                }

                // Fall back to thumbnail if originalimage is absent
                JsonNode thumbnail = root.path("thumbnail");
                if (!thumbnail.isMissingNode()) {
                    JsonNode source = thumbnail.path("source");
                    if (!source.isMissingNode() && !source.isNull()) {
                        return source.asText();
                    }
                }
            }

            return null;

        } catch (Exception e) {
            // Return null rather than crashing plant creation if Wikipedia is unavailable
            return null;
        }
    }

    public String getFirstArticleTitle(String plantName) {
        try {
            String trimmedName = plantName;

            // Only strip on " - " (space-dash-space) to avoid breaking scientific names like "Rosa canina"
            int dashPosition = trimmedName.indexOf(" - ");
            if (dashPosition >= 0) {
                trimmedName = trimmedName.substring(0, dashPosition);
            }

            int pipePosition = trimmedName.indexOf('|');
            if (pipePosition >= 0) {
                trimmedName = trimmedName.substring(0, pipePosition);
            }

            String encodedSearch = URLEncoder.encode(trimmedName.trim(), StandardCharsets.UTF_8);
            String url = baseUrl + "w/api.php?action=query&list=search&srsearch=" + encodedSearch + "&format=json";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .setHeader("User-Agent", "HappyPlants/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode searchArray = root.path("query").path("search");

                if (searchArray.isArray() && !searchArray.isEmpty()) {
                    JsonNode titleNode = searchArray.get(0).path("title");
                    if (!titleNode.isMissingNode() && !titleNode.isNull()) {
                        return titleNode.asText();
                    }
                }
            }

            return null;

        } catch (Exception e) {
            throw new WikipediaException(plantName);
        }
    }
}