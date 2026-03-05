package com.happyplants;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Application {

    public static void main(String[] args) {
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.configure()
                    .directory("./backend")
                    .load();
        } catch (Exception e) {
            dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();
        }

        if (dotenv == null) {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            System.out.println("⚠ No .env file found, trying using system environment variables");
        }

        if (dotenv != null) {
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
        }

        SpringApplication.run(Application.class, args);
    }
}