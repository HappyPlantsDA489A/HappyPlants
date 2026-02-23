package com.happyplants.service;

import com.happyplants.exception.UnauthorizedUserPlantAccessException;
import com.happyplants.exception.UserPlantNotFoundException;
import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PlantDTO;
import com.happyplants.model.dto.UserPlantDTO;
import com.happyplants.repository.UsersPlantRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsersPlantService {
    private final UsersPlantRepository usersPlantRepository;
    private final PlantService plantService;
    private final PerenualApiService perenualApiService;
    private final WikipediaService wikipediaService;

    public UsersPlantService(UsersPlantRepository usersPlantRepository, PlantService plantService, PerenualApiService perenualApiService, WikipediaService wikipediaService) {
        this.usersPlantRepository = usersPlantRepository;
        this.plantService = plantService;
        this.perenualApiService = perenualApiService;
        this.wikipediaService = wikipediaService;
    }

    public UserPlantDTO convertToDto(UsersPlant usersPlant, OffsetDateTime lastWateredAt, int timesWatered) {
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
                plant.getPlantDescription(), 
                plant.getWateringDescription(),
                plant.getSunDescription()
        );

        return new UserPlantDTO(
                usersPlant.getId(),
                usersPlant.getNickname(),
                usersPlant.getImageUrl(),
                usersPlant.getWateringFrequencyDays(),
                usersPlant.getCreatedAt(),
                usersPlant.getDiedAt(),
                lastWateredAt,
                timesWatered,
                plantDto
        );
    }

    public UsersPlant addPlantToUser(User user, int perenualId) {

        Plant plant = plantService.getOrCreatePlant(perenualId);

        UsersPlant usersPlant = new UsersPlant();
        usersPlant.setUser(user);
        usersPlant.setPlant(plant);
        usersPlant.setImageUrl(null);

        String wikipediaImageUrl = null;

        wikipediaImageUrl = wikipediaService.getPlantImageUrl(plant.getScientificName());
        if (wikipediaImageUrl == null) {
            wikipediaImageUrl = wikipediaService.getPlantImageUrl(plant.getCommonName());
        }

        usersPlant.setImageUrl(wikipediaImageUrl);

        return usersPlantRepository.save(usersPlant);
    }

    public List<UserPlantDTO> getPlantsForUser(UUID userId) {
        List<Object[]> results = usersPlantRepository.findAllWithLastWateredByUserId(userId);

        return results.stream().map(result -> {
            UsersPlant up = (UsersPlant) result[0];
            OffsetDateTime lastWatered = (OffsetDateTime) result[1];
            Long count = (Long) result[2];
            int timesWatered = (count != null) ? count.intValue() : 0;
            return convertToDto(up, lastWatered, timesWatered);
        }).toList();
    }

    public void removeUserPlant(UUID userId, UUID userPlantId) {
        UsersPlant plant = usersPlantRepository.findById(userPlantId)
                .orElseThrow(UserPlantNotFoundException::new);

        if (!plant.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserPlantAccessException();
        }
        usersPlantRepository.delete(plant);
    }

    public void markPlantAsDead(UUID userId, UUID userPlantId) {
        UsersPlant plant = usersPlantRepository.findById(userPlantId)
                .orElseThrow(UserPlantNotFoundException::new);

        if (!plant.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserPlantAccessException();
        }

        if (plant.getDiedAt() == null) {
            plant.setDiedAt(OffsetDateTime.now());
        }

        usersPlantRepository.save(plant);

    }
}
