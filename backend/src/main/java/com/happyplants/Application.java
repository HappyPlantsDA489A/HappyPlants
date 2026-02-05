package com.happyplants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    @Value("${plant.api.token}")

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}