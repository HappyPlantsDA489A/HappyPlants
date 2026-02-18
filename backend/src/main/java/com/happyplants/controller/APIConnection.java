package com.happyplants.controller;

import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PlantDTO;
import com.happyplants.service.PerenualApiService;
import org.springframework.web.bind.annotation.*;
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
        return "Backend svarar svar: Koppling fungerar!";
    }

    @GetMapping("plants/search")
    public List<PlantDTO> search(@RequestParam String name) {
        return perenualApiService.search(name);
    }

    @GetMapping("plants/{id}")
    public PerenualPlantDTO getPlantById(@PathVariable int id) {
        return perenualApiService.getPlantById(id);
    }
}

