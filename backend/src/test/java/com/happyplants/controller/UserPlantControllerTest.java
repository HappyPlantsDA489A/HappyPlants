package com.happyplants.controller;

import com.happyplants.exception.UnauthorizedUserPlantAccessException;
import com.happyplants.exception.UserPlantNotFoundException;
import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.dto.PlantDTO;
import com.happyplants.dto.UserPlantDTO;
import com.happyplants.dto.WateredPlantDTO;
import com.happyplants.service.UserPlantService;
import com.happyplants.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserPlantController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserPlantController Tests")
public class UserPlantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserPlantService usersPlantService;

    private User mockUser;
    private Plant mockPlant;
    private UsersPlant mockUsersPlant;
    private UserPlantDTO mockUserPlantDTO;
    private UsernamePasswordAuthenticationToken authToken;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setDisplayName("Test User");
        mockUser.setEmail("test@example.com");

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

        authToken = new UsernamePasswordAuthenticationToken(
                mockUser.getId().toString(), null, Collections.emptyList()
        );

        when(userService.getCurrentUser(any())).thenReturn(mockUser);
    }

    @Nested
    @DisplayName("Add Plant Tests")
    class AddPlantTests {

        @Test
        @DisplayName("HPF-COLL-01: Verify that plant is added to user's collection and returns correct response")
        public void shouldReturnCreatedWhenPlantIsAddedToCollection() throws Exception {
            when(usersPlantService.addPlantToUser(eq(mockUser), eq(1))).thenReturn(mockUsersPlant);
            when(usersPlantService.convertToDto(any(), any(), anyInt())).thenReturn(mockUserPlantDTO);

            mockMvc.perform(post("/api/user/plants/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.plant.commonName").value("Snake Plant"));
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that adding a plant with non-existent Perenual ID returns 404")
        public void shouldReturn404WhenPlantExternalIdDoesNotExist() throws Exception {
            when(usersPlantService.addPlantToUser(eq(mockUser), eq(999))).thenReturn(null);

            mockMvc.perform(post("/api/user/plants/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Get Plants Tests")
    class GetPlantsTests {

        @Test
        @DisplayName("HPF-COLL-01: Verify that added plants appear in the personal collection")
        public void shouldReturnListWhenUserHasPlants() throws Exception {
            List<UserPlantDTO> userPlantsList = List.of(mockUserPlantDTO);
            when(usersPlantService.getPlantsForUser(mockUser.getId())).thenReturn(userPlantsList);

            mockMvc.perform(get("/api/user/plants")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].plant.commonName").value("Snake Plant"));
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that an empty list is returned when the user's collection has no plants")
        public void shouldReturnEmptyListWhenCollectionIsEmpty() throws Exception {
            when(usersPlantService.getPlantsForUser(mockUser.getId())).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/user/plants"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that a 404 status is returned when the user is not found")
        public void shouldReturn404WhenUserIsNotFound() throws Exception {
            when(userService.getCurrentUser(any())).thenReturn(null);

            mockMvc.perform(get("/api/user/plants"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Get Plant Tests")
    class GetPlantTests {

        @Test
        @DisplayName("HPF-COLL-01: Verify that a specific plant can be retrieved by ID")
        public void shouldReturnPlantWhenFoundByIdForUser() throws Exception {
            UUID plantId = mockUserPlantDTO.id();
            when(usersPlantService.getPlantForUser(eq(plantId), eq(mockUser.getId()))).thenReturn(mockUserPlantDTO);

            mockMvc.perform(get("/api/user/plants/" + plantId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(plantId.toString()))
                    .andExpect(jsonPath("$.plant.commonName").value("Snake Plant"));
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that a 404 status is returned when the plant is not found for the user")
        public void shouldReturn404WhenPlantIsNotFound() throws Exception {
            UUID plantId = UUID.randomUUID();
            when(usersPlantService.getPlantForUser(eq(plantId), any())).thenReturn(null);

            mockMvc.perform(get("/api/user/plants/" + plantId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that a 404 status is returned when trying to access another user's plant")
        public void shouldReturn404WhenAccessingAnotherUsersPlant() throws Exception {
            UUID otherUsersPlantId = UUID.randomUUID();
            when(usersPlantService.getPlantForUser(eq(otherUsersPlantId), eq(mockUser.getId()))).thenReturn(null);

            mockMvc.perform(get("/api/user/plants/" + otherUsersPlantId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that a 400 status is returned when the plant ID format is invalid (not a UUID)")
        public void shouldReturn400WhenUUIDFormatIsInvalid() throws Exception {
            mockMvc.perform(get("/api/user/plants/not-a-valid-uuid"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that a 401 status is returned when the user session is invalid")
        public void shouldReturn401WhenUserIsNotFound() throws Exception {
            when(userService.getCurrentUser(any())).thenReturn(null);

            mockMvc.perform(get("/api/user/plants/" + UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Remove Plant Tests")
    class RemovePlantTests {

        @Test
        @DisplayName("HPF-COLL-03: Verify that delete returns 204 No Content")
        public void shouldReturn204WhenPlantIsRemovedSuccessfully() throws Exception {
            UUID userPlantId = UUID.randomUUID();
            doNothing().when(usersPlantService).removeUserPlant(mockUser.getId(), userPlantId);

            mockMvc.perform(delete("/api/user/plants/" + userPlantId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("HPF-COLL-03: Should return 401 if user session is invalid")
        public void shouldReturn401WhenUserSessionIsInvalid() throws Exception {
            when(userService.getCurrentUser(any())).thenReturn(null);

            mockMvc.perform(delete("/api/user/plants/" + UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("HPF-COLL-03: Should return 404 when trying to remove a plant that does not exist in the user's collection")
        public void shouldReturn404WhenPlantToRemoveDoesNotExist() throws Exception {
            UUID plantId = UUID.randomUUID();
            doThrow(new UserPlantNotFoundException())
                    .when(usersPlantService).removeUserPlant(mockUser.getId(), plantId);

            mockMvc.perform(delete("/api/user/plants/" + plantId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("HPF-COLL-03: Should return 403 when trying to remove a plant that belongs to another user")
        public void shouldReturn403WhenRemovingAnotherUsersPlant() throws Exception {
            UUID plantId = UUID.randomUUID();
            doThrow(new UnauthorizedUserPlantAccessException())
                    .when(usersPlantService).removeUserPlant(mockUser.getId(), plantId);

            mockMvc.perform(delete("/api/user/plants/" + plantId))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Water Plant Tests")
    class WaterPlantTests {

        @Test
        @DisplayName("HPF-CARE-03: Verify that watering a plant returns 201 Created with the correct response body")
        public void shouldReturn201WithWateredPlantDTOWhenWateringIsSuccessful() throws Exception {
            UUID userPlantId = UUID.randomUUID();
            WateredPlantDTO dto = new WateredPlantDTO(OffsetDateTime.now());

            when(usersPlantService.waterPlant(any(UUID.class), any(UUID.class))).thenReturn(dto);

            mockMvc.perform(post("/api/user/plants/{userPlantId}/water", userPlantId)
                            .with(authentication(authToken)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.occuredAt").isNotEmpty());
        }

        @Test
        @DisplayName("HPF-CARE-03: Verify that watering a non-existent plant returns 404 Not Found")
        public void shouldReturn404WhenPlantDoesNotExist() throws Exception {
            UUID userPlantId = UUID.randomUUID();

            doThrow(new UserPlantNotFoundException())
                    .when(usersPlantService).waterPlant(any(UUID.class), any(UUID.class));

            mockMvc.perform(post("/api/user/plants/{userPlantId}/water", userPlantId)
                            .with(authentication(authToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("HPF-CARE-03: Verify that watering a plant that belongs to another user returns 403 Forbidden")
        public void shouldReturn403WhenUserDoesNotOwnThePlant() throws Exception {
            UUID userPlantId = UUID.randomUUID();

            doThrow(new UnauthorizedUserPlantAccessException())
                    .when(usersPlantService).waterPlant(any(UUID.class), any(UUID.class));

            mockMvc.perform(post("/api/user/plants/{userPlantId}/water", userPlantId)
                            .with(authentication(authToken)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Watering History Tests")
    class WateringHistoryTests {

        @Test
        @DisplayName("HPF-CARE-03: Verify that retrieving watering history returns 200 OK with the correct response body")
        public void shouldReturn200WithWateringHistory() throws Exception {
            UUID userPlantId = UUID.randomUUID();
            List<WateredPlantDTO> history = List.of(
                    new WateredPlantDTO(OffsetDateTime.now().minusDays(1)),
                    new WateredPlantDTO(OffsetDateTime.now().minusDays(3))
            );

            when(usersPlantService.getWateringHistory(any(UUID.class), any(UUID.class))).thenReturn(history);

            mockMvc.perform(get("/api/user/plants/{userPlantId}/waterings", userPlantId)
                            .with(authentication(authToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].occuredAt").isNotEmpty())
                    .andExpect(jsonPath("$[1].occuredAt").isNotEmpty());
        }

        @Test
        @DisplayName("HPF-CARE-03: Verify that retrieving watering history for a plant that does not exist returns 404 Not Found")
        public void shouldReturn404WhenPlantDoesNotExist() throws Exception {
            UUID userPlantId = UUID.randomUUID();

            doThrow(new UserPlantNotFoundException())
                    .when(usersPlantService).getWateringHistory(any(UUID.class), any(UUID.class));

            mockMvc.perform(get("/api/user/plants/{userPlantId}/waterings", userPlantId)
                            .with(authentication(authToken)))
                    .andExpect(status().isNotFound());
        }
    }
}
