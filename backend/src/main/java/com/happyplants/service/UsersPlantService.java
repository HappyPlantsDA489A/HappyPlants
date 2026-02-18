package com.happyplants.service;

import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PlantDTO;
import com.happyplants.model.dto.UsersPlantDto;
import com.happyplants.repository.UsersPlantRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UsersPlantService {
    private final UsersPlantRepository usersPlantRepository;
    private final PlantService plantService;
    private final PerenualApiService perenualApiService;

    public UsersPlantService(UsersPlantRepository usersPlantRepository, PlantService plantService, PerenualApiService perenualApiService) {
        this.usersPlantRepository = usersPlantRepository;
        this.plantService = plantService;
        this.perenualApiService = perenualApiService;
    }

    public UsersPlantDto convertToDto(UsersPlant usersPlant) {
        Plant plant = usersPlant.getPlant();

        PlantDTO plantDto = new PlantDTO(
                plant.getId(),
                plant.getPerenualId(),
                plant.getCommonName(),
                plant.getScientificName(),
                plant.getFamilyName(),
                plant.getCultivar(),
                plant.getSpeciesEpithet(),
                plant.getGenus(),
                plant.getWateringDescription(),
                plant.getSunDescription()
        );

        return new UsersPlantDto(
                usersPlant.getId(),
                usersPlant.getNickname(),
                usersPlant.getImageUrl(),
                usersPlant.getWateringFrequencyDays(),
                usersPlant.getCreatedAt(),
                usersPlant.getDiedAt(),
                plantDto
        );
    }

    public UsersPlant addPlantToUser(User user, int perenualId) {

        Plant plant = plantService.getOrCreatePlant(perenualId);
        PerenualPlantDTO perenualPlantDTO = perenualApiService.getPlantById(perenualId);

        UsersPlant usersPlant = new UsersPlant();
        usersPlant.setUser(user);
        usersPlant.setPlant(plant);
        usersPlant.setImageUrl("placeholder");
        return usersPlantRepository.save(usersPlant);
    }

    public List<UsersPlant> getPlantsForUser(UUID userId) {
        return usersPlantRepository.findAllByUserId(userId);
    }
}
