package com.happyplants.controller;

import com.happyplants.dto.PlantDTO;
import com.happyplants.dto.PerenualSearchPlantDTO;
import com.happyplants.model.Plant;
import com.happyplants.service.PerenualApiService;
import com.happyplants.service.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class APIConnection {

    private final PerenualApiService perenualApiService;
    private final PlantService plantService;

    public APIConnection(PerenualApiService perenualApiService, PlantService plantService) {
        this.perenualApiService = perenualApiService;
        this.plantService = plantService;
    }

    @GetMapping("test")
    public String test() {
        return "Backend replying: Connection successful";
    }

    @GetMapping("plants/search")
    @Operation(summary = "Search for plants")
    public ResponseEntity<List<PerenualSearchPlantDTO>> search(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<PerenualSearchPlantDTO> results = perenualApiService.search(name);
            results = plantService.enrichWithImages(results);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("plants/{id}")
    @Operation(summary = "Get plant details by id")
    public ResponseEntity<PlantDTO> getPlantById(@PathVariable int id) {
        try {
            Plant plant = plantService.getOrCreatePlant(id);
            if (plant == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(plantService.convertToDto(plant));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
