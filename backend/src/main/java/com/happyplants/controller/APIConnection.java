package com.happyplants.controller;

import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PerenualSearchPlantDTO;
import com.happyplants.service.PerenualApiService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class APIConnection {

    private final PerenualApiService perenualApiService;

    public APIConnection(PerenualApiService perenualApiService) {
        this.perenualApiService = perenualApiService;
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
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

    }


    @GetMapping("plants/{id}")
    @Operation(summary = "Get plant details by id")
    public ResponseEntity<PerenualPlantDTO> getPlantById(@PathVariable int id) {
        PerenualPlantDTO plant = perenualApiService.getPlantById(id);

        if (plant == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plant);
    }
}

