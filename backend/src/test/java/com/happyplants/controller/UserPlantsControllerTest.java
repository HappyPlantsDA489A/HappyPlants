package com.happyplants.controller;

<<<<<<< UnitTest--HPFColl03-RemovePlant

import com.happyplants.model.User;
import com.happyplants.service.UserService;
import com.happyplants.service.UsersPlantService;
=======
import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.dto.PlantDTO;
import com.happyplants.model.dto.UserPlantDTO;
import com.happyplants.service.UserService;
import com.happyplants.service.UsersPlantService;
import org.junit.jupiter.api.BeforeEach;
>>>>>>> dev
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
<<<<<<< UnitTest--HPFColl03-RemovePlant
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserPlantsController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})

public class UserPlantsControllerTest {
=======
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
>>>>>>> dev

    @Autowired
    private MockMvc mockMvc;

<<<<<<< UnitTest--HPFColl03-RemovePlant
    @MockitoBean
=======
    @MockitoBean // Korrekt annotation för Spring Boot 3.4+
>>>>>>> dev
    private UserService userService;

    @MockitoBean
    private UsersPlantService usersPlantService;

<<<<<<< UnitTest--HPFColl03-RemovePlant
    @Test
    @DisplayName("HPF-COLL-03: Verify that delete returns 204 No Content")
    void removeUserPlant_ShouldReturnNoContent() throws Exception {
        // Create a mock-user and an id for the plant
        UUID userId = UUID.randomUUID();
        UUID userPlantId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(userId);

        // Simulate that the user is logged in
        when(userService.getCurrentUser(any())).thenReturn(mockUser);

        // Simulate that the service-method runs without faults
        doNothing().when(usersPlantService).removeUserPlant(userId, userPlantId);

        // Call the delete-endpoint and expect a 204 status
        mockMvc.perform(delete("/api/user/plants/" + userPlantId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("HPF-COLL-03: Should return 401 if user session is invalid")
    void removeUserPlant_UserNotFound_ShouldReturn401() throws Exception {
        when(userService.getCurrentUser(any())).thenReturn(null);

        mockMvc.perform(delete("/api/user/plants/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized()); // Testar din nya if-sats
    }

    @Test
    @DisplayName("HPF-COLL-03: Should return 404 if plant to remove does not exist")
    void removeUserPlant_PlantNotFound_ShouldReturn404() throws Exception {
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        UUID plantId = UUID.randomUUID();

        when(userService.getCurrentUser(any())).thenReturn(mockUser);

        doThrow(new com.happyplants.exception.UserPlantNotFoundException())
                .when(usersPlantService).removeUserPlant(mockUser.getId(), plantId);

        mockMvc.perform(delete("/api/user/plants/" + plantId))
                .andExpect(status().isNotFound());
    }
}
=======
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

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Get non-existent user plant returns 404")
    void getUserPlant_NotFound_ShouldReturn404() throws Exception {
        UUID plantId = UUID.randomUUID();
        // Telling Mockito to return mockuser
        when(userService.getCurrentUser(any())).thenReturn(mockUser);
        // Simulating that the service cannot find the plant in user's collection
        when(usersPlantService.getPlantForUser(eq(plantId), any())).thenReturn(null);

        mockMvc.perform(get("/api/user/plants/" + plantId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Add non-existent plant (external ID) returns 404")
    void addPlantToLibrary_PlantNotFound_ShouldReturn404() throws Exception {
        when(userService.getCurrentUser(any())).thenReturn(mockUser);
        // Simulating that the external plant data cannot be found
        when(usersPlantService.addPlantToUser(eq(mockUser), eq(999))).thenReturn(null);

        mockMvc.perform(post("/api/user/plants/999")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Empty collection returns 200 and empty list")
    void getUserListOfPlants_EmptyCollection_ShouldReturnEmptyArray() throws Exception {
        when(userService.getCurrentUser(any())).thenReturn(mockUser);
        // Verifies that the system handles an empty collection gracefully
        when(usersPlantService.getPlantsForUser(mockUser.getId())).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/user/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Unauthorized access to another user's plant returns 404")
    void getUserPlant_Unauthorized_ShouldReturn404() throws Exception {
        UUID otherUsersPlantId = UUID.randomUUID();
        // Simulating that the service denies access to the plant (returns null) for a different user
        when(userService.getCurrentUser(any())).thenReturn(mockUser);
        when(usersPlantService.getPlantForUser(eq(otherUsersPlantId), eq(mockUser.getId()))).thenReturn(null);

        mockMvc.perform(get("/api/user/plants/" + otherUsersPlantId))
                .andExpect(status().isNotFound()); // Förhindrar informationsläckage
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Invalid UUID format returns 400")
    void getUserPlant_InvalidUUID_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/user/plants/inte-ett-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Get all plants returns 404 if user not found")
    void getUserPlants_UserNotFound_ShouldReturn404() throws Exception {
        // Simulating that the user cannot be found in the database
        when(userService.getCurrentUser(any())).thenReturn(null);

        mockMvc.perform(get("/api/user/plants"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("HPF-COLL-01: Get specific plant returns 401 if user not found")
    void getUserPlant_UserNotFound_ShouldReturn401() throws Exception {
        // Simulating that the user cannot be found
        when(userService.getCurrentUser(any())).thenReturn(null);

        mockMvc.perform(get("/api/user/plants/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }


}
>>>>>>> dev
