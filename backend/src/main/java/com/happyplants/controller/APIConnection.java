package com.happyplants.controller;

import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PerenualSearchPlantDTO;
import com.happyplants.service.PerenualApiService;
import io.swagger.v3.oas.annotations.Operation;
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
        return "Backend replying: Connection successful";
    }

    @GetMapping("plants/search")
    @Operation(summary = "Search for plants")
    public List<PerenualSearchPlantDTO> search(@RequestParam String name) {
        return perenualApiService.search(name);
    }

    @GetMapping("plants/{id}")
    @Operation(summary = "Get plant details by id")
    public PerenualPlantDTO getPlantById(@PathVariable int id) {
        return perenualApiService.getPlantById(id);
    }
}

