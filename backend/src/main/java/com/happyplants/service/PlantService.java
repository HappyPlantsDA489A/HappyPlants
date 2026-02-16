package com.happyplants.service;

import com.happyplants.model.Plant;
import com.happyplants.model.dto.PlantDTO;
import com.happyplants.repository.PlantRepository;
import org.springframework.stereotype.Service;


@Service
public class PlantService {
    private final PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public Plant getOrCreatePlant(PlantDTO plantDTO) {
        return plantRepository.findByPerenualId(plantDTO.getId())
                .orElseGet(() -> plantRepository.save(convertDtoToPlant(plantDTO)));
    }

    private Plant convertDtoToPlant(PlantDTO plantDto) {
        Plant plant = new Plant();
        plant.setPerenualId(plantDto.getId());
        plant.setCommonName(plantDto.getCommonName());
        plant.setScientificName(plantDto.getScientificName() != null && plantDto.getScientificName().length > 0 ? plantDto.getScientificName()[0] : "Unknown");
        plant.setFamilyName(plantDto.getFamily());
        plant.setGenus(plantDto.getGenus());
        plant.setWateringDescription(plantDto.getWatering());
        plant.setSunDescription(plantDto.getSunlight());
        return plant;
    }


}
