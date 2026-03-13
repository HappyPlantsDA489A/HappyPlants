package com.happyplants.service;

import com.happyplants.dto.response.PlantResponse;
import com.happyplants.dto.response.UserPlantResponse;
import com.happyplants.dto.response.WateredPlantResponse;
import com.happyplants.exception.InvalidWateringFrequencyException;
import com.happyplants.exception.UnauthorizedUserPlantAccessException;
import com.happyplants.exception.UserPlantNotFoundException;
import com.happyplants.model.*;
import com.happyplants.repository.UsersPlantRepository;
import com.happyplants.repository.WateredPlantRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserPlantService {
    private final UsersPlantRepository usersPlantRepository;
    private final PlantService plantService;
    private final PerenualApiService perenualApiService;
    private final PerenualCacheService perenualCacheService;
    private final WateredPlantRepository wateredPlantRepository;

    public UserPlantService(UsersPlantRepository usersPlantRepository,
                            PlantService plantService,
                            PerenualApiService perenualApiService,
                            PerenualCacheService perenualCacheService,
                            WateredPlantRepository wateredPlantRepository) {
        this.usersPlantRepository = usersPlantRepository;
        this.plantService = plantService;
        this.perenualApiService = perenualApiService;
        this.perenualCacheService = perenualCacheService;
        this.wateredPlantRepository = wateredPlantRepository;
    }

    public UserPlantResponse convertToDto(UsersPlant usersPlant, OffsetDateTime lastWateredAt, int timesWatered) {
        Plant plant = usersPlant.getPlant();

        // Perenual fresh image (cache, TTL 6h, Wikipedia fallback) takes priority over stored Wikipedia URL
        String perenualImageUrl = perenualCacheService.getFreshImageUrl(
                plant.getPerenualId(),
                plant.getScientificName(),
                plant.getCommonName()
        );
        String resolvedPlantImageUrl = perenualImageUrl != null
                ? perenualImageUrl
                : plant.getWikipediaImageUrl();

        PlantResponse plantDto = new PlantResponse(
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
                plant.getSunDescription(),
                resolvedPlantImageUrl
        );

        return new UserPlantResponse(
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

        if (plant == null) {
            return null;
        }

        UsersPlant usersPlant = new UsersPlant();
        usersPlant.setUser(user);
        usersPlant.setPlant(plant);
        usersPlant.setImageUrl(null);
        return usersPlantRepository.save(usersPlant);
    }

    public List<UserPlantResponse> getPlantsForUser(UUID userId) {
        List<Object[]> results = usersPlantRepository.findAllWithLastWateredByUserId(userId);

        return results.stream().map(result -> {
            UsersPlant up = (UsersPlant) result[0];
            OffsetDateTime lastWatered = (OffsetDateTime) result[1];
            Long count = (Long) result[2];
            int timesWatered = (count != null) ? count.intValue() : 0;
            return convertToDto(up, lastWatered, timesWatered);
        }).toList();
    }

    public UserPlantResponse getPlantForUser(UUID plantId, UUID userId) {
        return usersPlantRepository.findWithLastWateredByPlantId(plantId, userId)
                .stream()
                .findFirst()
                .map(result -> {
                    UsersPlant up = (UsersPlant) result[0];
                    OffsetDateTime lastWatered = (OffsetDateTime) result[1];
                    Long count = (Long) result[2];
                    int timesWatered = (count != null) ? count.intValue() : 0;

                    return convertToDto(up, lastWatered, timesWatered);
                })
                .orElseThrow(UserPlantNotFoundException::new);
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

    public void updateWateringFrequency(UUID userId, UUID userPlantId, Integer wateringFrequencyDays) {
        UsersPlant plant = usersPlantRepository.findById(userPlantId)
                .orElseThrow(UserPlantNotFoundException::new);

        if (!plant.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserPlantAccessException();
        }

        if (wateringFrequencyDays == null || wateringFrequencyDays == 0) {
            plant.setWateringFrequencyDays(null);
        }
        else if (wateringFrequencyDays < 0) {
            throw new InvalidWateringFrequencyException();
        }
        else plant.setWateringFrequencyDays(wateringFrequencyDays);

        usersPlantRepository.save(plant);
    }

    public WateredPlantResponse waterPlant(UUID userId, UUID userPlantId) {
        UsersPlant plant = usersPlantRepository.findById(userPlantId)
                .orElseThrow(UserPlantNotFoundException::new);

        if (!plant.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserPlantAccessException();
        }

        OffsetDateTime now = OffsetDateTime.now();

        WateredPlantId id = new WateredPlantId();
        id.setUsersPlantsId(userPlantId);
        id.setOccuredAt(now);

        WateredPlant watered = new WateredPlant();
        watered.setId(id);
        watered.setUsersPlants(plant);

        wateredPlantRepository.save(watered);

        return new WateredPlantResponse(now);
    }

    public List<WateredPlantResponse> getWateringHistory(UUID userId, UUID userPlantId) {
        UsersPlant plant = usersPlantRepository.findById(userPlantId)
                .orElseThrow(UserPlantNotFoundException::new);

        if (!plant.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserPlantAccessException();
        }

        return wateredPlantRepository.findHistory(userPlantId)
                .stream()
                .map(w -> new WateredPlantResponse(w.getId().getOccuredAt()))
                        .toList();
    }

    public void updateNickname(UUID userId, UUID userPlantId, String nickname) {
        UsersPlant plant = usersPlantRepository.findById(userPlantId)
                .orElseThrow(UserPlantNotFoundException::new);

        if (!plant.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserPlantAccessException();
        }

        if (nickname != null && nickname.isBlank()) {
            nickname = null;
        }

        plant.setNickname(nickname);
        usersPlantRepository.save(plant);
    }

    public void updateImageUrl(UUID userId,  UUID userPlantId, String imageUrl) {
        UsersPlant plant = usersPlantRepository.findById(userPlantId)
                .orElseThrow(UserPlantNotFoundException::new);

        if (!plant.getUser().getId().equals(userId)) {
            throw new UnauthorizedUserPlantAccessException();
        }

        if (imageUrl != null && imageUrl.isBlank()) {
            imageUrl = null;
        }

        plant.setImageUrl(imageUrl);
        usersPlantRepository.save(plant);
    }


}
