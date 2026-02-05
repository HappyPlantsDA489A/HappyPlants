package com.happyplants.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api")
public class APIConnection {

    @Value("${plant.api.token}")
    private String plantApiKey;

    @GetMapping("test")
    public String test() {
        return "Backend svarar svar: Koppling fungerar!";
    }

    @GetMapping("search")
    public String search(@RequestParam String plantName) throws IOException, InterruptedException {
        String url = String.format("https://perenual.com/api/v2/species-list?q=%s&page=1&hardiness=4-8&key=%s", plantName, plantApiKey);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        System.out.println(response.statusCode());
        System.out.println(plantApiKey);

        return response.body();
    }
}
