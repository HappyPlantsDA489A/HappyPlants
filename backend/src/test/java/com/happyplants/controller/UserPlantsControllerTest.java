package com.happyplants.controller;

import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.dto.PlantDTO;
import com.happyplants.model.dto.UserPlantDTO;
import com.happyplants.service.UserService;
import com.happyplants.service.UsersPlantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPlantsController.class)
class UserPlantsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Korrekt annotation för Spring Boot 3.4+
    private UserService userService;

    @MockitoBean
    private UsersPlantService usersPlantService;

    private User mockUser;
    private Plant mockPlant;
    private UsersPlant mockUsersPlant;
    private UserPlantDTO mockUserPlantDTO;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setDisplayName("Test User");

        mockPlant = new Plant();
        mockPlant.setPerenualId(1);
        mockPlant.setCommonName("Snake Plant");

        mockUsersPlant = new UsersPlant();
        mockUsersPlant.setUser(mockUser);
        mockUsersPlant.setPlant(mockPlant);

        PlantDTO plantDto = new PlantDTO(
                UUID.randomUUID(), 1, "Snake Plant", "Sansevieria",
                null, null, null, null, null, null, null
        );

        mockUserPlantDTO = new UserPlantDTO(
                UUID.randomUUID(), null, null, null, null,
                null, null, 0, plantDto
        );
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Verify that a plant can be added to the personal collection")
    void addPlantToLibrary_ShouldReturnCreated() throws Exception {
        when(userService.getCurrentUser(any())).thenReturn(mockUser);
        when(usersPlantService.addPlantToUser(eq(mockUser), eq(1))).thenReturn(mockUsersPlant);
        when(usersPlantService.convertToDto(any(), any(), anyInt())).thenReturn(mockUserPlantDTO);

        mockMvc.perform(post("/api/user/plants/1")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()) // Krävs ofta vid POST-test
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plant.commonName").value("Snake Plant"));
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Verify that added plants appear in the personal collection")
    void getUserListOfPlants_ShouldReturnListOfUserPlants() throws Exception {
        List<UserPlantDTO> userPlantsList = List.of(mockUserPlantDTO);
        when(userService.getCurrentUser(any())).thenReturn(mockUser);
        when(usersPlantService.getPlantsForUser(mockUser.getId())).thenReturn(userPlantsList);

        mockMvc.perform(get("/api/user/plants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].plant.commonName").value("Snake Plant"));
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Verify that a specific plant can be retrieved by ID")
    void getUserPlant_ShouldReturnSpecificPlant() throws Exception {
        // Arrange
        UUID plantId = mockUserPlantDTO.id();
        when(userService.getCurrentUser(any())).thenReturn(mockUser);
        when(usersPlantService.getPlantForUser(eq(plantId), eq(mockUser.getId()))).thenReturn(mockUserPlantDTO);

        // Act & Assert
        mockMvc.perform(get("/api/user/plants/" + plantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(plantId.toString()))
                .andExpect(jsonPath("$.plant.commonName").value("Snake Plant"));
    }
}