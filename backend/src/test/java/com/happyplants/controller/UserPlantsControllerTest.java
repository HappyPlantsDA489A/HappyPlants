package com.happyplants.controller;


import com.happyplants.model.User;
import com.happyplants.service.UserService;
import com.happyplants.service.UsersPlantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UsersPlantService usersPlantService;

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
