package com.happyplants.service;

import com.happyplants.exception.UnauthorizedUserPlantAccessException;
import com.happyplants.exception.UserPlantNotFoundException;
import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.WateredPlant;
import com.happyplants.model.WateredPlantId;
import com.happyplants.dto.response.UserPlantResponse;
import com.happyplants.dto.response.WateredPlantResponse;
import com.happyplants.repository.UsersPlantRepository;
import com.happyplants.repository.WateredPlantRepository;
import com.happyplants.service.PerenualCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPlantService Tests")
public class UserPlantServiceTest {

    @Mock
    private UsersPlantRepository usersPlantRepository;

    @Mock
    private WateredPlantRepository wateredPlantRepository;

    @Mock
    private PlantService plantService;

    @Mock
    private PerenualApiService perenualApiService;

    @Mock
    private PerenualCacheService perenualCacheService;

    @InjectMocks
    private UserPlantService usersPlantService;

    private UUID userId;
    private UUID userPlantId;
    private User owner;
    private UsersPlant plant;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userPlantId = UUID.randomUUID();

        owner = new User();
        owner.setId(userId);
        owner.setEmail("owner@example.com");
        owner.setDisplayName("Owner");

        plant = new UsersPlant();
        plant.setId(userPlantId);
        plant.setUser(owner);
    }

    @Nested
    @DisplayName("Add Plant Tests")
    class AddPlantTests {

        @Test
        @DisplayName("HPF-COLL-01: Verify that plant is correctly associated with user and persisted")
        public void shouldAssociateAndSavePlantWhenAddingToUser() {
            Plant mockPlant = new Plant();
            mockPlant.setPerenualId(1);
            mockPlant.setCommonName("Snake Plant");

            when(plantService.getOrCreatePlant(1)).thenReturn(mockPlant);
            when(usersPlantRepository.save(any(UsersPlant.class))).thenAnswer(i -> i.getArguments()[0]);

            UsersPlant result = usersPlantService.addPlantToUser(owner, 1);

            assertNotNull(result);
            assertEquals(owner, result.getUser(), "Plant must be associated with the correct user");
            assertEquals(mockPlant, result.getPlant(), "Correct plant must be associated");
            verify(usersPlantRepository, times(1)).save(any(UsersPlant.class));
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that adding a plant with a non-existent Perenual ID returns null")
        public void shouldReturnNullWhenPlantServiceCannotFindExternalId() {
            when(plantService.getOrCreatePlant(999)).thenReturn(null);

            UsersPlant result = usersPlantService.addPlantToUser(owner, 999);

            assertNull(result);
            verify(usersPlantRepository, never()).save(any());
        }

        @Test
        @DisplayName("HPF-COLL-01: Verify that an exception is thrown when the database save fails")
        public void shouldThrowExceptionWhenDatabaseSaveFails() {
            when(plantService.getOrCreatePlant(1)).thenReturn(new Plant());
            when(usersPlantRepository.save(any())).thenThrow(new RuntimeException("Database connection lost"));

            assertThrows(RuntimeException.class, () -> usersPlantService.addPlantToUser(owner, 1));
        }
    }

    @Nested
    @DisplayName("Get Plants Tests")
    class GetPlantsTests {

        @Test
        @DisplayName("HPF-COLL-01: Verify that an empty list is returned when the user has no plants")
        public void shouldReturnEmptyListWhenUserHasNoPlants() {
            when(usersPlantRepository.findAllWithLastWateredByUserId(userId)).thenReturn(List.of());

            List<UserPlantResponse> result = usersPlantService.getPlantsForUser(userId);

            assertNotNull(result);
            assertTrue(result.isEmpty(), "Result should be an empty list when user has no plants");
        }
    }

    @Nested
    @DisplayName("Remove Plant Tests")
    class RemovePlantTests {

        @Test
        @DisplayName("HPF-COLL-03: Verify that plant is removed when owner requests it")
        public void shouldDeletePlantWhenOwnerRequestsRemoval() {
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

            usersPlantService.removeUserPlant(userId, userPlantId);

            verify(usersPlantRepository, times(1)).delete(plant);
        }

        @Test
        @DisplayName("HPF-COLL-03: Verify that an exception is thrown when unauthorized users try to remove another user's plant")
        public void shouldThrowUnauthorizedExceptionWhenNonOwnerTriesToRemove() {
            UUID strangerId = UUID.randomUUID();
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

            assertThrows(UnauthorizedUserPlantAccessException.class, () ->
                    usersPlantService.removeUserPlant(strangerId, userPlantId));

            verify(usersPlantRepository, never()).delete(plant);
        }

        @Test
        @DisplayName("HPF-COLL-03: Verify that an exception is thrown when the plant does not exist")
        public void shouldThrowNotFoundExceptionWhenPlantDoesNotExist() {
            UUID nonExistentId = UUID.randomUUID();
            when(usersPlantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThrows(UserPlantNotFoundException.class, () ->
                    usersPlantService.removeUserPlant(userId, nonExistentId));
        }
    }

    @Nested
    @DisplayName("Water Plant Tests")
    class WaterPlantTests {

        @Test
        @DisplayName("HPF-CARE-03: Verify that watering a plant saves a record and returns a DTO with a timestamp")
        public void shouldSaveWateredPlantAndReturnDTOWithTimestamp() {
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));
            when(wateredPlantRepository.save(any(WateredPlant.class))).thenAnswer(i -> i.getArgument(0));

            WateredPlantResponse result = usersPlantService.waterPlant(userId, userPlantId);

            ArgumentCaptor<WateredPlant> captor = ArgumentCaptor.forClass(WateredPlant.class);
            verify(wateredPlantRepository).save(captor.capture());

            WateredPlant saved = captor.getValue();
            assertNotNull(saved.getId(), "WateredPlantId should not be null");
            assertEquals(userPlantId, saved.getId().getUsersPlantsId(), "Saved watering should reference the correct plant");
            assertNotNull(result.wateredAt(), "Returned DTO should have a non-null timestamp");
        }

        @Test
        @DisplayName("HPF-CARE-03: Verify that watering a non-existent plant throws UserPlantNotFoundException")
        public void shouldThrowNotFoundExceptionWhenPlantDoesNotExist() {
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.empty());

            assertThrows(UserPlantNotFoundException.class,
                    () -> usersPlantService.waterPlant(userId, userPlantId));

            verify(wateredPlantRepository, never()).save(any());
        }

        @Test
        @DisplayName("HPF-CARE-03: Verify that watering a plant that belongs to another user throws UnauthorizedUserPlantAccessException")
        public void shouldThrowUnauthorizedExceptionWhenUserDoesNotOwnThePlant() {
            UUID differentUserId = UUID.randomUUID();
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

            assertThrows(UnauthorizedUserPlantAccessException.class,
                    () -> usersPlantService.waterPlant(differentUserId, userPlantId));

            verify(wateredPlantRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Watering History Tests")
    class WateringHistoryTests {

        @Test
        @DisplayName("HPF-CARE-03: Verify that retrieving watering history returns a list of WateredPlantDTOs")
        public void shouldReturnWateringHistoryAsDTOList() {
            WateredPlantId id1 = new WateredPlantId();
            id1.setUsersPlantsId(userPlantId);
            id1.setOccuredAt(OffsetDateTime.now().minusDays(1));

            WateredPlantId id2 = new WateredPlantId();
            id2.setUsersPlantsId(userPlantId);
            id2.setOccuredAt(OffsetDateTime.now().minusDays(3));

            WateredPlant w1 = new WateredPlant();
            w1.setId(id1);
            WateredPlant w2 = new WateredPlant();
            w2.setId(id2);

            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));
            when(wateredPlantRepository.findHistory(userPlantId)).thenReturn(List.of(w1, w2));

            List<WateredPlantResponse> result = usersPlantService.getWateringHistory(userId, userPlantId);

            assertEquals(2, result.size());
            assertEquals(id1.getOccuredAt(), result.get(0).wateredAt());
            assertEquals(id2.getOccuredAt(), result.get(1).wateredAt());
        }

        @Test
        @DisplayName("HPF-CARE-03: Verify that retrieving watering history for a non-existent plant throws UserPlantNotFoundException")
        public void shouldThrowNotFoundExceptionWhenPlantDoesNotExist() {
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.empty());

            assertThrows(UserPlantNotFoundException.class,
                    () -> usersPlantService.getWateringHistory(userId, userPlantId));

            verify(wateredPlantRepository, never()).findHistory(any());
        }

        @Test
        @DisplayName("HPF-CARE-03: Verify that retrieving watering history for another user's plant throws UnauthorizedUserPlantAccessException")
        public void shouldThrowUnauthorizedExceptionWhenUserDoesNotOwnThePlant() {
            UUID differentUserId = UUID.randomUUID();
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

            assertThrows(UnauthorizedUserPlantAccessException.class,
                    () -> usersPlantService.getWateringHistory(differentUserId, userPlantId));

            verify(wateredPlantRepository, never()).findHistory(any());
        }
    }
}
