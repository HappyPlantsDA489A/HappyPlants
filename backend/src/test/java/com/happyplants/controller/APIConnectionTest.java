package com.happyplants.controller;

import com.happyplants.dto.response.PlantResponse;
import com.happyplants.dto.response.PerenualSearchPlantResponse;
import com.happyplants.model.Plant;
import com.happyplants.service.PerenualApiService;
import com.happyplants.service.PerenualCacheService;
import com.happyplants.service.PlantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
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

    @MockitoBean
    private PlantService plantService;

    @MockitoBean
    private PerenualCacheService perenualCacheService;

    @Test
    @DisplayName("HPF-SEARCH-01: Verify that search returns matching plants")
    void searchEndpoint_ShouldReturnMatchingPlants() throws Exception {

        PerenualSearchPlantResponse mockPlant = new PerenualSearchPlantResponse(
                1, "Rose", List.of("Rosa"), "Rosaceae", "Cultivar", "Epithet", "Genus", null
        );

        when(perenualApiService.search("Rose")).thenReturn(List.of(mockPlant));
        when(plantService.enrichWithImages(anyList())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(get("/api/plants/search")
                        .param("name", "Rose"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].common_name").value("Rose"))
                .andExpect(jsonPath("$[0].scientific_name[0]").value("Rosa"))
                .andExpect(jsonPath("$[0].family").value("Rosaceae"));
    }


    @Test
    @DisplayName("HPF-SEARCH-01: Search returns empty list when no plants match")
    void searchEndpoint_NoMatch_ShouldReturnEmptyList() throws Exception {
        when(perenualApiService.search("NonExistentPlant")).thenReturn(java.util.Collections.emptyList());
        when(plantService.enrichWithImages(anyList())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(get("/api/plants/search")
                        .param("name", "NonExistentPlant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("HPF-SEARCH-01: Search with missing name param returns 400")
    void searchEndpoint_MissingParam_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/plants/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("HPF-SEARCH-01: Search with empty name returns 400 or empty list")
    void searchEndpoint_EmptyName_ShouldHandleGracefully() throws Exception {
        mockMvc.perform(get("/api/plants/search").param("name", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("HPF-SEARCH-01: Service failure returns 500")
    void searchEndpoint_ServiceError_ShouldReturn500() throws Exception {
        when(perenualApiService.search(anyString())).thenThrow(new RuntimeException("API Down"));
        when(plantService.enrichWithImages(anyList())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(get("/api/plants/search").param("name", "Rose"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("HPF-PLANT-02.1: Plant details include image URL from Wikipedia")
    void getPlantById_ShouldReturnPlantDtoWithImageUrl() throws Exception {
        UUID plantId = UUID.randomUUID();
        PlantResponse mockDto = new PlantResponse(
                plantId,
                1,
                "Golden Pothos",
                "Epipremnum aureum",
                "Araceae",
                null,
                null,
                "Epipremnum",
                "A resilient climbing plant",
                "Frequent",
                "Partial Shade",
                "https://upload.wikimedia.org/wikipedia/commons/some/image.jpg"
        );

        Plant mockPlant = new Plant();
        mockPlant.setPerenualId(1);
        mockPlant.setCommonName("Golden Pothos");
        mockPlant.setScientificName("Epipremnum aureum");

        when(plantService.getOrCreatePlant(1)).thenReturn(mockPlant);
        when(plantService.convertToDto(mockPlant)).thenReturn(mockDto);

        mockMvc.perform(get("/api/plants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commonName").value("Golden Pothos"))
                .andExpect(jsonPath("$.scientificName").value("Epipremnum aureum"))
                .andExpect(jsonPath("$.wateringDescription").value("Frequent"))
                .andExpect(jsonPath("$.sunDescription").value("Partial Shade"))
                .andExpect(jsonPath("$.imageUrl").value("https://upload.wikimedia.org/wikipedia/commons/some/image.jpg"))
                .andExpect(jsonPath("$.plantDescription").exists());
    }

    @Test
    @DisplayName("Verify that the test endpoint returns connection success message")
    void testEndpoint_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Backend replying: Connection successful"));
    }

    @Test
    @DisplayName("HPF-PLANT-02: Plant not found returns 404")
    void getPlantById_NotFound_ShouldReturn404() throws Exception {
        when(plantService.getOrCreatePlant(999)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/plants/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("HPF-PLANT-02: Invalid plant ID (e.g. 'abc') returns 400")
    void getPlantById_InvalidId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/plants/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("HPF-PLANT-02: Extremely large ID returns 400")
    void getPlantById_TooLargeId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/plants/999999999999999"))
                .andExpect(status().isBadRequest());
    }
}
