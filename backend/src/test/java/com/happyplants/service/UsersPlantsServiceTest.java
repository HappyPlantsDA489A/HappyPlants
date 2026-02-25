package com.happyplants.service;

import com.happyplants.exception.UnauthorizedUserPlantAccessException;
import com.happyplants.exception.UserPlantNotFoundException;
import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.dto.UserPlantDTO;
import com.happyplants.repository.UsersPlantRepository;
import com.happyplants.repository.WateredPlantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersPlantsServiceTest {

    @Mock
    private UsersPlantRepository usersPlantRepository;

    @Mock
    private WateredPlantRepository wateredPlantRepository;

    @Mock
    private PlantService plantService;

    @Mock
    private PerenualApiService perenualApiService;

    @InjectMocks
    private UsersPlantService usersPlantService;

    @Test
    @DisplayName("HPF-USERS-01: Verify that plant is removed when owner requests it")
    void removeUserPlant_ShouldDeleteWhenOwnerIsCorrect(){
        // Create mock-data for user and connection
        UUID userId = UUID.randomUUID();
        UUID userPlantId = UUID.randomUUID();

        User mockUser = new User();
        mockUser.setId(userId);

        UsersPlant plant = new UsersPlant();
        plant.setUser(mockUser);

        when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

        // Run method for removal
        usersPlantService.removeUserPlant(userId, userPlantId);

        // Verify that delete was called
        verify(usersPlantRepository, times(1)).delete(plant);

    }

    @Test
    @DisplayName("HPF-COLL-01: Verify that plant is correctly associated with user and persisted")
    void addPlantToUser_ShouldAssociateAndSave() {

        User user = new User();
        user.setId(UUID.randomUUID());

        Plant plant = new Plant();
        plant.setPerenualId(1);
        plant.setCommonName("Snake Plant");

        when(plantService.getOrCreatePlant(1)).thenReturn(plant);
        when(usersPlantRepository.save(any(UsersPlant.class))).thenAnswer(i -> i.getArguments()[0]);


        UsersPlant result = usersPlantService.addPlantToUser(user, 1);


        assertNotNull(result);
        assertEquals(user, result.getUser(), "Plant must be associated with the correct user");
        assertEquals(plant, result.getPlant(), "Correct plant must be associated");

        // Verifies that the repository's save method was called (Persistence!)
        verify(usersPlantRepository, times(1)).save(any(UsersPlant.class));
    }

    @Test
    @DisplayName("HPF-COLL-01: Adding plant fails if plant data cannot be fetched")
    void addPlantToUser_PlantServiceFails_ShouldReturnNull() {
        User user = new User();
        // Simulating that the plant service cannot find the plant (returns null)
        when(plantService.getOrCreatePlant(999)).thenReturn(null);

        UsersPlant result = usersPlantService.addPlantToUser(user, 999);

        // Verifying that no save operation took place if the plant was not found
        assertNull(result);
        verify(usersPlantRepository, never()).save(any());
    }

    @Test
    @DisplayName("HPF-COLL-01: Service throws exception if database save fails")
    void addPlantToUser_DatabaseError_ShouldThrowException() {
        when(plantService.getOrCreatePlant(1)).thenReturn(new Plant());
        User mockUser = new User();
        // Simulating a database error
        when(usersPlantRepository.save(any())).thenThrow(new RuntimeException("Database connection lost"));

        assertThrows(RuntimeException.class, () -> {
            usersPlantService.addPlantToUser(mockUser, 1);
        });
    }

    @Test
    @DisplayName("HPF-COLL-01: Should return empty list when user has no plants")
    void getPlantsForUser_NoPlants_ShouldReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        // Vi simulerar att databasen inte hittar några kopplingar
        lenient().when(usersPlantRepository.findAllByUserId(userId))
                .thenReturn(java.util.Collections.emptyList());

        List<UserPlantDTO> result = usersPlantService.getPlantsForUser(userId);

        assertTrue(result.isEmpty(), "Result should be empty but not null");
    }

    @Test
    @DisplayName("HPF-COLL-01: Should return null if external plant ID does not exist")
    void addPlantToUser_InvalidPerenualId_ShouldReturnNull() {
        User user = new User();
        // plantService kan inte hitta växten och returnerar null
        when(plantService.getOrCreatePlant(99999)).thenReturn(null);

        UsersPlant result = usersPlantService.addPlantToUser(user, 99999);

        assertNull(result);
        verify(usersPlantRepository, never()).save(any());
    }

    @Test
    @DisplayName("HPF-COLL-03: Verify unauthorized users cannot remove others plants")
    void removeUserPlant_ShouldThrowExceptionWhenOwnerIsIncorrect(){
        UUID ownerId = UUID.randomUUID();
        UUID strangerId = UUID.randomUUID();
        UUID userPlantId = UUID.randomUUID();

        User owner = new User();
        owner.setId(ownerId);

        UsersPlant plant = new UsersPlant();
        plant.setUser(owner);

        when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

        // Verify that exception is thrown
        assertThrows(UnauthorizedUserPlantAccessException.class, () ->
                usersPlantService.removeUserPlant(strangerId, userPlantId)
        );

        // Verify that delete was not called
        verify(usersPlantRepository, never()).delete(plant);
    }

    @Test
    @DisplayName("HPF-COLL-04: Verify that exception is thrown when plant does not exist")
    void removeUserPlant_ShouldThrowExceptionWhenPlantNotFound(){
        UUID userId = UUID.randomUUID();
        UUID nonExistentId = UUID.randomUUID();

        when(usersPlantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserPlantNotFoundException.class, () ->
                usersPlantService.removeUserPlant(userId, nonExistentId));
    }

}
