package com.happyplants.controller;

import com.happyplants.service.WikipediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// This controller is only intended to be used for testing

@RestController
@RequestMapping("/api/wikipedia")
@Tag(name = "Wikipedia")
public class WikipediaController {
    private final WikipediaService wikipediaService;

    public WikipediaController(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
    }

    @GetMapping("/image-url")
    @Operation(summary = "Get image url by plant name")
    public String getPlantImageUrl(@RequestParam(name = "plantName") String plantName) {
        String url = wikipediaService.getPlantImageUrl(plantName);
        return (url != null) ? url : "No image found for: " + plantName;
    }
}
