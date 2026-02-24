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

import static org.mockito.ArgumentMatchers.anyString;
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
                .andExpect(jsonPath("$[0].common_name").value("Rose"));
    }


    @Test
    @DisplayName("HPF-SEARCH-01: Search returns empty list when no plants match")
    void searchEndpoint_NoMatch_ShouldReturnEmptyList() throws Exception {
        // Simulating that the API returns an empty list when no plants match
        when(perenualApiService.search("NonExistentPlant")).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/plants/search")
                        .param("name", "NonExistentPlant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("HPF-SEARCH-01: Search with missing name param returns 400")
    void searchEndpoint_MissingParam_ShouldReturn400() throws Exception {
        // Testing to call the search endpoint without the 'name' parameter
        mockMvc.perform(get("/api/plants/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("HPF-SEARCH-01: Search with empty name returns 400 or empty list")
    void searchEndpoint_EmptyName_ShouldHandleGracefully() throws Exception {
        // If the search is empty, the API should return an empty list
        mockMvc.perform(get("/api/plants/search").param("name", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("HPF-SEARCH-01: Service failure returns 500")
    void searchEndpoint_ServiceError_ShouldReturn500() throws Exception {
        // Simulating that the service throws an exception
        when(perenualApiService.search(anyString())).thenThrow(new RuntimeException("API Down"));

        mockMvc.perform(get("/api/plants/search").param("name", "Rose"))
                .andExpect(status().isInternalServerError());
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
            .andExpect(jsonPath("$.sunlight[0]").value("Partial Shade"))
            .andExpect(jsonPath("$.scientific_name[0]").value("Epipremnum aureum"))
            .andExpect(jsonPath("$.family").value("Araceae"))
            .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("Verify that the test endpoint returns connection success message")
    void testEndpoint_ShouldReturnSuccessMessage() throws Exception{
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Backend replying: Connection successful"));

    }


    @Test
    @DisplayName("HPF-PLANT-02: Plant not found returns 404")
    void getPlantById_NotFound_ShouldReturn404() throws Exception {
        // Simulate that service returns null when a plant is not found
        when(perenualApiService.getPlantById(999)).thenReturn(null);

        mockMvc.perform(get("/api/plants/999"))
                .andExpect(status().isNotFound()); //
    }

    @Test
    @DisplayName("HPF-PLANT-02: Invalid plant ID (e.g. 'abc') returns 400")
    void getPlantById_InvalidId_ShouldReturn400() throws Exception {
        // Spring Boot throws an error automatically if a string is passed to an Integer parameter
        mockMvc.perform(get("/api/plants/abc"))
                .andExpect(status().isBadRequest()); //
    }

    @Test
    @DisplayName("HPF-PLANT-02: Extremely large ID returns 400")
    void getPlantById_TooLargeId_ShouldReturn400() throws Exception {
        // A number larger than Integer.MAX_VALUE
        mockMvc.perform(get("/api/plants/999999999999999"))
                .andExpect(status().isBadRequest());
    }
}