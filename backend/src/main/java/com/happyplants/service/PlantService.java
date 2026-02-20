package com.happyplants.service;

import com.happyplants.controller.APIConnection;
import com.happyplants.model.Plant;
import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PlantDTO;
import com.happyplants.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final PerenualApiService perenualApiService;

    public PlantService(PlantRepository plantRepository, PerenualApiService perenualApiService) {
        this.plantRepository = plantRepository;
        this.perenualApiService = perenualApiService;
    }

    public Plant getOrCreatePlant(int perenualId) {
        return plantRepository.findByPerenualId(perenualId)
                .orElseGet(() -> {
                   PerenualPlantDTO plantDTO = perenualApiService.getPlantById(perenualId);
                   Plant plant = convertDtoToPlant(plantDTO);
                   return plantRepository.save(plant);
                });
    }

    private Plant convertDtoToPlant(PerenualPlantDTO plantDto) {
        Plant plant = new Plant();
        plant.setPerenualId(plantDto.perenualId());
        plant.setCommonName(plantDto.commonName());
        plant.setScientificName(plantDto.scientificName().isEmpty() ? null : plantDto.scientificName().get(0));
        plant.setFamilyName(plantDto.familyName());
        plant.setCultivar(plantDto.cultivar());
        plant.setSpeciesEpithet(plantDto.speciesEpithet());
        plant.setGenus(plantDto.genus());
        plant.setPlantDescription(plantDto.plantDescription());
        plant.setWateringDescription(plantDto.wateringDescription());
        plant.setSunDescription(
                plantDto.sunDescription() != null && !plantDto.sunDescription().isEmpty()
                        ? plantDto.sunDescription().get(0)
                        : null
        );
        return plant;
    }

}
