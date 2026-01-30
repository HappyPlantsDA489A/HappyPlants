package com.happyplants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Value("${plant.api.token}")
    private String plantApiKey;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner printKey() {
        return args -> {
            System.out.println("Min API-nyckel är: " + plantApiKey);
        };
    }
}