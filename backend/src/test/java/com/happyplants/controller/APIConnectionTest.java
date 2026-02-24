package com.happyplants.controller;

import com.happyplants.model.dto.PerenualPlantDTO;
import com.happyplants.model.dto.PerenualSearchPlantDTO;
import com.happyplants.service.PerenualApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(controllers = APIConnection.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class APIConnectionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PerenualApiService perenualApiService;

    @Test
    @DisplayName("HPF-SEARCH-01: Verify that search returns matching plants")
    void searchEndpoint_ShouldReturnMatchingPlants() throws Exception {

        PerenualSearchPlantDTO mockPlant = new PerenualSearchPlantDTO(
                1, "Rose", List.of("Rosa"), "Rosaceae", "Cultivar", "Epithet", "Genus", null
        );

        when(perenualApiService.search("Rose")).thenReturn(List.of(mockPlant));

        mockMvc.perform(get("/api/plants/search")
                        .param("name", "Rose"))
                .andExpect(status().isOk())
                // ÄNDRING: Vi använder common_name med understreck här för att matcha JSON-outputen
                .andExpect(jsonPath("$[0].common_name").value("Rose"));
    }

    @Test
    @DisplayName("HPF-Plant-02: Verify that plant details are returned correctly with care data")
    void getPlantById_ShouldReturnCorrectDetails() throws Exception {
        // Creating a mock DTO with care data
        PerenualPlantDTO mockPlant = new PerenualPlantDTO(
                1,
                "Golden Pothos",
                List.of("Epipremnum aureum"),
                "Araceae",
                null,
                null,
                "Epipremnum",
                "A resilient climbing plant",
                "Frequent",
                List.of("Partial Shade")
        );

        // Mocking the service to return the mock DTO
    when(perenualApiService.getPlantById(1)).thenReturn(mockPlant);

    mockMvc.perform(get("/api/plants/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.common_name").value("Golden Pothos"))
            .andExpect(jsonPath("$.watering").value("Frequent"))
            .andExpect(jsonPath("$.sunlight[0]").value("Partial Shade"));
    }

    @Test
    @DisplayName("Verify that the test endpoint returns connection success message")
    void testEndpoint_ShouldReturnSuccessMessage() throws Exception{
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Backend replying: Connection successful"));

    }
}