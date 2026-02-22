package com.happyplants.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyplants.exception.WikipediaException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class WikipediaService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "https://en.wikipedia.org/";



    public String getPlantImageUrl(String plantName) {
        try {
            String articleTitle = getFirstArticleTitle(plantName);

            if (articleTitle == null) {
                return null;
            }

            String articleTitleWithUnderscores = articleTitle.replace(' ', '_');
            String encodedTitle = URLEncoder.encode(articleTitleWithUnderscores, StandardCharsets.UTF_8);

            String url = baseUrl + "api/rest_v1/page/summary/" + encodedTitle;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .setHeader("User-Agent", "HappyPlants/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode originalImage = root.path("originalimage");

                if (!originalImage.isMissingNode()) {
                    JsonNode source = originalImage.path("source");
                    if (!source.isMissingNode() && !source.isNull()) {
                        return source.asText();
                    }
                }
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Wikipedia image for plant: " + plantName, e);
        }
    }

    private String getFirstArticleTitle(String plantName) {
        try {
            String trimmedName = plantName;

            int dashPosition = trimmedName.indexOf('-');
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