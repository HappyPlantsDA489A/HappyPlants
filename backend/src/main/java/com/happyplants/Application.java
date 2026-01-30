package com.happyplants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class Application {

    @Value("${plant.api.token}")
    private String plantApiKey;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

    @Bean
    public CommandLineRunner printKey() throws IOException, InterruptedException {
        return args -> {
            System.out.println("Min API-nyckel är: " + plantApiKey);
        };

    }

   /* @Bean
    public HttpResponse<String> testAPI() throws IOException, InterruptedException {

        String common_name = "rose";
        String key = plantApiKey;

        //String url = String.format("https://trefle.io/api/v1/plants?token=%s&common_name%s", key, common_name);

        HttpClient client = HttpClient.newHttpClient();

        /*HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();



        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response);

        return response;

    }

    */

}