package com.happyplants.service;

import com.happyplants.model.Plant;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPlantServiceTest {

    @Mock
    private UsersPlantRepository usersPlantRepository;

    @Mock
    private PlantService plantService;

    @Mock
    private WateredPlantRepository wateredPlantRepository;

    @InjectMocks
    private UsersPlantService usersPlantService;

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

        // Verifies that repository's save method was called (Persistence!)
        verify(usersPlantRepository, times(1)).save(any(UsersPlant.class));
    }
}