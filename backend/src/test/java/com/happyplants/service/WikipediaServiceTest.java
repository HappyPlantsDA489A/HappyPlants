package com.happyplants.service;

import com.happyplants.exception.WikipediaException;
import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WikipediaService Tests")
public class WikipediaServiceTest {
    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private HttpClient mockClient;

    @Spy
    @InjectMocks
    private WikipediaService wikipediaService;

    @Nested
    @DisplayName("Get plant image url tests")
    class GetPlantImageUrlTests {

        @Test
        @DisplayName("Verifies that getPlantImageUrl() returns null if plantname is null.")
        void shouldReturnNull_getPlantImageUrl() {
            String result = wikipediaService.getPlantImageUrl(null);
            assertNull(result);
        }

        @Test
        @DisplayName("Verifies that getPlantImageUrl() returns null if plantname is blank.")
        void shouldReturnNull_getPlantImageUrl_blank() {
            String result = wikipediaService.getPlantImageUrl(" ");
            assertNull(result);
        }

        @Test
        void shouldReturnImageUrlScientificName_getPlantImageUrl() {
            String scientificName = "Monstera deliciosa";
            String commonName = "Swiss cheese plant";

            lenient().doReturn("https://image-for-scientific-name.jpg")
                    .when(wikipediaService).fetchImageUrl(scientificName);

            String result = wikipediaService.getPlantImageUrl(scientificName, commonName);

            assertEquals("https://image-for-scientific-name.jpg", result);
        }

        @Test
        void shouldReturnImageUrlCommonName_getPlantImageUrl() {
            String sciName = "Unknown name";
            String commonName = "Orchid";

            lenient().doReturn(null).when(wikipediaService).fetchImageUrl(sciName);
            lenient().doReturn("https://image-for-common-name.jpg")
                    .when(wikipediaService).fetchImageUrl(commonName);

            String result = wikipediaService.getPlantImageUrl(sciName, commonName);

            assertEquals("https://image-for-common-name.jpg", result);
        }
    }

    @Nested
    @DisplayName("Fetch Image Url Tests")
    class Fetch {
        @Test
        @DisplayName("HPF-PLANT-02.1: Verifies that an image url, originalImage, is mapped from the Api Response.")
        void shouldReturnImageUrl_fetchImageUrl_originalImage() throws IOException, InterruptedException {
            String json = """
                    {
                      "originalimage": {
                        "source": "https://en.wikipedia.org/images/monstera-deliciosa.jpg"
                      }
                    }""";

            doReturn("Monstera deliciosa")
                    .when(wikipediaService)
                    .getFirstArticleTitle("Monstera deliciosa");

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(json);

            String result = wikipediaService.fetchImageUrl("Monstera deliciosa");
            assertEquals("https://en.wikipedia.org/images/monstera-deliciosa.jpg", result);
        }


        @Test
        @DisplayName("HPF-PLANT-02.1: Verifies that an image url, thumbnail, is mapped from the Api Response.")
        void shouldReturnImageUrl_fetchImageUrl_thumbnail() throws IOException, InterruptedException {
            String json = """
                    {
                      "thumbnail": {
                        "source": "https://en.wikipedia.org/images/thumbnail.jpg"
                      }
                    }""";

            doReturn("Monstera deliciosa")
                    .when(wikipediaService)
                    .getFirstArticleTitle("Monstera deliciosa");

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(json);

            String result = wikipediaService.fetchImageUrl("Monstera deliciosa");
            assertEquals("https://en.wikipedia.org/images/thumbnail.jpg", result);
        }

        @Test
        @DisplayName("Should return null if there is no source field in the api response.")
        void shouldReturnNull_whenThumbnailSourceIsMissing() throws IOException, InterruptedException {
            String json = """
                    {
                      "thumbnail": {}
                    }""";

            doReturn("Monstera deliciosa")
                    .when(wikipediaService)
                    .getFirstArticleTitle("Monstera deliciosa");

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(json);

            String result = wikipediaService.fetchImageUrl("Monstera deliciosa");

            assertNull(result);
        }

        @Test
        @DisplayName("Should return null if there is no image field in the api response.")
        void shouldReturnNull_whenNoImageFieldsExist() throws IOException, InterruptedException {
            String json = """
                    {
                      "title": "Monstera deliciosa"
                    }""";

            doReturn("Monstera deliciosa")
                    .when(wikipediaService)
                    .getFirstArticleTitle("Monstera deliciosa");

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(json);

            String result = wikipediaService.fetchImageUrl("Monstera deliciosa");

            assertNull(result);
        }

        @Test
        @DisplayName("Verifies that fetchImageUrl() returns null if status code is not 200")
        void fetchImageUrl_statusCodeIsNot200() {
            lenient().doReturn(400).when(mockResponse).statusCode();
            String result = wikipediaService.fetchImageUrl("Monstera deliciosa");
            assertNull(result);
        }

        @Test
        @DisplayName("Verifies that fetchImageUrl() returns null if articleTitle is null.")
        void shouldReturnNull_fetchImageUrl_articleTitleNull() throws IOException, InterruptedException {
            doReturn("Monstera deliciosa")
                    .when(wikipediaService)
                    .getFirstArticleTitle("Monstera deliciosa");

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(404);

            String result = wikipediaService.fetchImageUrl("Monstera deliciosa");

            assertNull(result);
        }

        @Test
        @DisplayName("Verifies that fetchImageUrl() returns null if there is no article title in the api response.")
        void shouldReturnNull_articleTitleIsNull_fetchImageUrl() {
            lenient().doReturn(null).when(wikipediaService).getFirstArticleTitle("Monstera deliciosa");

            String result = wikipediaService.fetchImageUrl("Monstera deliciosa");
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Get first article title tests")
    class GetFirstArticleTitleTests {
        @Test
        @DisplayName("Verifies that an exception can be thrown.")
        void shouldThrowException_getFirstArticleTitle() {
            assertThrows(WikipediaException.class, () -> {
                wikipediaService.getFirstArticleTitle(null);
            });
        }

        @Test
        @DisplayName("Verifies that the article title can be mapped from the api response.")
        void shouldReturnTitle_getFirstArticleTitle() throws IOException, InterruptedException {
            String json = """
                    {
                      "batchcomplete": "",
                      "query": {
                        "searchinfo": {
                          "totalhits": 1
                        },
                        "search": [
                          {
                            "ns": 0,
                            "title": "Some plant title",
                            "pageid": 12345,
                            "size": 4567,
                            "wordcount": 890,
                            "timestamp": "2026-03-08T12:00:00Z"
                          }
                        ]
                      }
                    }""";

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(json);
            when(mockResponse.statusCode()).thenReturn(200);

            String result = wikipediaService.getFirstArticleTitle("Monstera deliciosa");
            assertEquals("Some plant title", result);
        }

        @Test
        @DisplayName("Verifies that getFirstArticleTitle() returns null if status code is not 200.")
        void shouldReturnNull_statusCodeIsNot200_getFirstArticleTitle() throws IOException, InterruptedException {
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(404);

            String result = wikipediaService.getFirstArticleTitle("Monstera deliciosa");

            assertNull(result);
        }

        @Test
        @DisplayName("Verifies that article title can be fetched even if plant name includes a dash.")
        void shouldHandleNameWithDash_getFirstArticleTitle() throws IOException, InterruptedException {
            String json = """
                    {
                      "query": {
                        "search": [
                          { "title": "Some plant title" }
                        ]
                      }
                    }""";

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(json);

            String result = wikipediaService.getFirstArticleTitle("Monstera - deliciosa");

            assertEquals("Some plant title", result);
        }

        @Test
        @DisplayName("Verifies that article title can be fetched even if plant name includes a pipe.")
        void shouldHandleNameWithPipe_getFirstArticleTitle() throws IOException, InterruptedException {
            String json = """
                    {
                      "query": {
                        "search": [
                          { "title": "Some plant title" }
                        ]
                      }
                    }""";

            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(json);

            String result = wikipediaService.getFirstArticleTitle("Monstera | deliciosa");

            assertEquals("Some plant title", result);
        }
    }
}
