package com.happyplants.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyplants.dto.internal.PerenualPlantData;
import com.happyplants.dto.response.PerenualSearchPlantResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Perenual API Endpoint Tests")
public class PerenualApiServiceTest {

    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private HttpClient mockClient;

    private ObjectMapper mapper;

    private PerenualApiService service;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();   // viktig rad
        service = new PerenualApiService(mockClient, mapper);
    }

    @Nested
    @DisplayName("Search Tests")
    class SearchEndpointTests {
        @Test
        @DisplayName("TC-PLANT-01, TC-SEA-01: Verifies that exception can be thrown.")
        void shouldThrowExceptionSearch() {
            assertThrows(RuntimeException.class, () -> {
                service.search(null);
            });
        }

        @Test
        @DisplayName("TC-PLANT-01, TC-SEA-01, TC-SEA-02: Verifies that the correct data is mapped from API response to PerenualSearchPlantResponse")
        void shouldMapValidApiResponseToPerenualSearchPlantResponse() throws IOException, InterruptedException {
            String json = """
        {
          "data": [
            {
              "id": 3080,
              "common_name": "orchid",
              "scientific_name": ["Galearis spectabilis"],
              "family": "Orchidaceae",
              "cultivar": null,
              "species_epithet": "spectabilis",
              "genus": "Galearis",
              "default_image": {
                "original_url": "http://image.jpg"
              }
            }
          ]
        }
        """;

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(json);
            List<PerenualSearchPlantResponse> result = service.search("orchid");
            assertEquals(1, result.size());
            assertEquals("orchid", result.get(0).commonName());
            assertEquals("Galearis", result.get(0).genus());
        }
    }

    @Nested
    @DisplayName("Get plant by ID tests")
    class GetPlantByIdTests {
        @Test
        @DisplayName("Verifies that exception can be thrown.")
        void shouldThrowExceptionGetPlantById() {
            assertThrows(RuntimeException.class, () -> {
                service.getPlantById(-1);
            });
        }

        @Test
        @DisplayName("Verifies that the correct data is mapped from API response when it is found by plant id.")
        void shouldMapCorrectDataWhenPlantIsFoundById() throws IOException, InterruptedException {
            String json = """
            {
              "id": 67,
              "common_name": "Kagiri Nishiki Japanese Maple",
              "scientific_name": ["Acer palmatum 'Kagiri Nishiki'"],
              "family": null,
              "cultivar": "Kagiri Nishiki",
              "species_epithet": "palmatum",
              "genus": "Acer",
              "default_image": {
                "original_url": "http://image.jpg"
              }
            }
            """;

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(json);
            PerenualPlantData result = service.getPlantById(67);

            assertEquals(67, result.perenualId());
            assertEquals("Kagiri Nishiki Japanese Maple", result.commonName());
        }
    }

    @Nested
    @DisplayName("Get plant result tests")
    class GetPlantResultTests {
        @Test
        @DisplayName("Verifies that exception can be thrown.")
        void shouldThrowExceptionGetPlantResults() {
            assertThrows(RuntimeException.class, () -> {
                service.getPlantResults(null);
            });
        }

        @Test
        @DisplayName("TC-PLANT-01, TC-SEA-01, TC-SEA-02 : Verifies that the correct data is mapped from API response to PerenualSearchPlantResponse")
        void shouldFetchDataFromApiAndMapItCorrectly() {
            String json = """
        {
          "data": [
            {
              "id": 1,
              "common_name": "Rose",
              "scientific_name": ["Rosa"],
              "family": "Rosaceae",
              "cultivar": null,
              "species_epithet": "rubiginosa",
              "genus": "Rosa",
              "default_image": {
                "original_url": "http://image.jpg"
              }
            }
          ]
        }
        """;

            when(mockResponse.body()).thenReturn(json);

            List<PerenualSearchPlantResponse> result =
                    service.getPlantResults(mockResponse);

            assertEquals(1, result.size());

            PerenualSearchPlantResponse plant = result.get(0);

            assertEquals(1, plant.perenualId());
            assertEquals("Rose", plant.commonName());
            assertEquals("Rosaceae", plant.familyName());
            assertEquals("Rosa", plant.genus());
        }
    }

    @Nested
    @DisplayName("Get partial plant by ID")
    class GetPartialPlantByIdTests {
        @Test
        @DisplayName("Verifies that exception can be thrown.")
        void shouldThrowExceptionGetPartialPlantById() {
            assertThrows(RuntimeException.class, () -> {
                service.getPartialPlantById(-1);
            });
        }

        @Test
        @DisplayName("Verifies that a valid ID returns an instance of PerenualSearchPlantDto with corresponding data.")
        void shouldReturnCorrectSearchDtoWhenIdIsValid() {
            PerenualSearchPlantResponse searchDto =
                    new PerenualSearchPlantResponse(
                            1,
                            "Orchid",
                            List.of("Orchidaceae"),
                            "Orchidaceae",
                            "Cultivar",
                            "palmatum",
                            "Acer",
                            "http://image.jpg"
                    );

            service.getSearchCache().put(1, searchDto);

            PerenualPlantData result = service.getPartialPlantById(1);
            assertEquals(1, result.perenualId());
            assertEquals("Orchid", result.commonName());

            assertNull(result.plantDescription());
            assertNull(result.wateringDescription());
            assertNull(result.sunDescription());
        }
    }

    @Nested
    @DisplayName("Get watering description tests")
    class GetWateringDescriptionTests {
        @Test
        @DisplayName("TC-PLANT-02: Verifies that the watering description is mapped from the API Response.")
        void shouldMapDescriptionFromApiResponse() throws IOException, InterruptedException {
            String json = """
        {
          "data": [
            {
              "id": 998,
              "species_id": 235,
              "section": [
                {
                  "id": 2995,
                  "type": "watering",
                  "description": "Water twice per week."
                }
              ]
            }
          ]
        }
        """;

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(json);
            String result = service.getWateringDescription(998);
            assertEquals("Water twice per week.", result);
        }

        @Test
        @DisplayName("TC-PLANT-02: Verifies that exception can be thrown.")
        void shouldThrowExceptionGetWateringDescription() {
            assertThrows(RuntimeException.class, () -> {
                service.getWateringDescription(-1);
            });
        }

        @Test
        @DisplayName("TC-PLANT-02: Verifies that the return value is null if there is no watering description in the API Response.")
        void shouldReturnNullGetWateringDescription() throws IOException, InterruptedException {
            String json = """
        {
          "data": [
            {
              "id": 998,
              "species_id": 235,
              "section": [
                {
                  "id": 2995,
                  "type": "sunlight",
                  "description": "A lot of sun."
                }
              ]
            }
          ]
        }
        """;

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(json);
            String result = service.getWateringDescription(998);
            assertNull(result);
        }
    }
}
