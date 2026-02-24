package com.happyplants.service;

import com.happyplants.exception.UnauthorizedUserPlantAccessException;
import com.happyplants.exception.UserPlantNotFoundException;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.repository.UsersPlantRepository;
import com.happyplants.repository.WateredPlantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersPlantsRemoveServiceTest {

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
