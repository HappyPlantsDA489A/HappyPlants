package com.happyplants.service;

import com.happyplants.exception.UnauthorizedUserPlantAccessException;
import com.happyplants.exception.UserPlantNotFoundException;
import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.WateredPlant;
import com.happyplants.model.WateredPlantId;
import com.happyplants.dto.UserPlantDTO;
import com.happyplants.dto.WateredPlantDTO;
import com.happyplants.repository.UsersPlantRepository;
import com.happyplants.repository.WateredPlantRepository;
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

            List<UserPlantDTO> result = usersPlantService.getPlantsForUser(userId);

            assertNotNull(result);
            assertTrue(result.isEmpty(), "Result should be an empty list when user has no plants");
        }

        @Test
        @DisplayName("HPF-COLL-02 & HPF-Plant-02: Verify that convertToDTO maps all fields correctly")
        public void shouldMapAllFieldsCorrectly(){
            Plant basePlant = new Plant();
            basePlant.setPerenualId(1);
            basePlant.setCommonName("Snake Plant");
            basePlant.setCommonName("Snake Plant");
            basePlant.setWateringDescription("Water when the top inch of soil is dry");

            UsersPlant usersPlant = new UsersPlant();
            usersPlant.setId(userPlantId);
            usersPlant.setNickname("My Snake Plant");
            usersPlant.setPlant(basePlant);

            OffsetDateTime lastWatered = OffsetDateTime.now();
            int timesWatered = 5;

            UserPlantDTO result = usersPlantService.convertToDto(usersPlant, lastWatered, timesWatered);

            assertNotNull(result);
            assertEquals("My Snake Plant", result.nickname());
            assertEquals("Snake Plant", result.plant().commonName());
            assertEquals("Water when the top inch of soil is dry", result.plant().wateringDescription());
            assertEquals(lastWatered, result.lastWateredAt());

            assertEquals("Snake Plant", result.plant().commonName());
            assertEquals("Water when the top inch of soil is dry", result.plant().wateringDescription());

        }

        @Test
        @DisplayName("HPF-COLL: Verify that multiple data fields are correctly mapped from Object arrray")
        public void shouldMapDatabaseResultsToDtoList(){
            plant.setPlant(new Plant());
            plant.getPlant().setCommonName("Snake Plant");

            OffsetDateTime lastWatered = OffsetDateTime.now();
            Long timesWatered = 5L;

            Object[] row = new Object[]{plant, lastWatered, timesWatered };
            when(usersPlantRepository.findAllWithLastWateredByUserId(userId)).thenReturn(List.<Object[]>of(row));

            List<UserPlantDTO> result = usersPlantService.getPlantsForUser(userId);

            assertEquals(1, result.size(), "Should return a list with one DTO");
            UserPlantDTO dto = result.get(0);

            assertEquals(5, dto.timesWatered(), "Times watered should be correctly mapped");
            assertEquals("Snake Plant", dto.plant().commonName(), "Plant common name should be correctly mapped");

        }

        @Test
        @DisplayName("HPF-CARE-03: Verify that timesWatered is set to 0 when count is null")
        public void shouldSetTimesWateredToZeroWhenCountIsNull(){
            Plant basePlant = new Plant();
            basePlant.setId(UUID.randomUUID());
            basePlant.setCommonName("Snake Plant");

            plant.setPlant(basePlant);

            Object[] row = new Object[]{plant, null, null };
            when(usersPlantRepository.findAllWithLastWateredByUserId(userId)).thenReturn(List.<Object[]>of(row));

            List<UserPlantDTO> result = usersPlantService.getPlantsForUser(userId);

            assertEquals(1,result.size());
            assertEquals(0, result.get(0).timesWatered(),"Times watered should be set to 0 when count is null");
            assertNull(result.get(0).lastWateredAt(), "Last watered should be null when not provided");
        }

    }

    @Nested
    @DisplayName("Get Single Plant Tests")
    class GetSinglePlantTests {

        @Test
        @DisplayName("HPF-PLANT-02: Should return UserPlantDTO when plant is found")
        public void shouldReturnUserPlantDTOWhenPlantIsFound() {
            plant.setPlant(new Plant());
            plant.getPlant().setCommonName("Monstera");

            OffsetDateTime lastWatered = OffsetDateTime.now();
            Long timesWatered = 3L;

            Object[] mockRow = new Object[]{plant, lastWatered, timesWatered};
            when(usersPlantRepository.findWithLastWateredByPlantId(userPlantId, userId))
                    .thenReturn(List.<Object[]>of(mockRow));

            UserPlantDTO result = usersPlantService.getPlantForUser(userPlantId, userId);

            assertNotNull(result);
            assertEquals("Monstera", result.plant().commonName(), "Common name should match");
            assertEquals(3, result.timesWatered());
            assertEquals(lastWatered, result.lastWateredAt());
        }

        @Test
        @DisplayName("HPF-CARE-03: Should handle null count and set timeWatered to 0")
        public void shouldHandleNullCountAndSetTimesWateredToZero() {
            plant.setPlant(new Plant());
            Object[] mockRow = new Object[]{plant, null, null};
            when(usersPlantRepository.findWithLastWateredByPlantId(userPlantId, userId))
                    .thenReturn(List.<Object[]>of(mockRow));

            UserPlantDTO result = usersPlantService.getPlantForUser(userPlantId, userId);

            assertEquals(0, result.timesWatered(), "Times watered should be set to 0 when count is null");
            assertNull(result.lastWateredAt());
        }

        @Test
        @DisplayName("HPF-PLANT-02: Should throw UserPlantNotFoundException when plant is not found")
        public void shouldThrowNotFoundExceptionWhenPlantIsNotFound() {
            when(usersPlantRepository.findWithLastWateredByPlantId(userPlantId, userId))
                    .thenReturn(List.of());

            assertThrows(UserPlantNotFoundException.class, () ->
                    usersPlantService.getPlantForUser(userPlantId, userId));
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

            WateredPlantDTO result = usersPlantService.waterPlant(userId, userPlantId);

            ArgumentCaptor<WateredPlant> captor = ArgumentCaptor.forClass(WateredPlant.class);
            verify(wateredPlantRepository).save(captor.capture());

            WateredPlant saved = captor.getValue();
            assertNotNull(saved.getId(), "WateredPlantId should not be null");
            assertEquals(userPlantId, saved.getId().getUsersPlantsId(), "Saved watering should reference the correct plant");
            assertNotNull(result.occuredAt(), "Returned DTO should have a non-null timestamp");
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
    @DisplayName("HPF-COLL-07: Watering History Tests")
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

            List<WateredPlantDTO> result = usersPlantService.getWateringHistory(userId, userPlantId);

            assertEquals(2, result.size());
            assertEquals(id1.getOccuredAt(), result.get(0).occuredAt());
            assertEquals(id2.getOccuredAt(), result.get(1).occuredAt());
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

    @Nested
    @DisplayName("Update Plant Details Tests")
    class UpdatePlantDetailsTests {
        @Test
        @DisplayName("HPF-COLL-02: Should update nickname and save when user is owner")
        public void shouldUpdateNicknameWhenUserIsOwner() {
            String newNickname = "Baby Bell";
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

            usersPlantService.updateNickname(userId, userPlantId, newNickname);

            assertEquals(newNickname, plant.getNickname());
            verify(usersPlantRepository).save(plant);
        }

        @Test
        @DisplayName("HPF-COLL-02: Should set nickname to null if provided string is blank")
        public void shouldSetNicknameToNullWhenBlank() {
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

            usersPlantService.updateNickname(userId, userPlantId, "   ");

            assertNull(plant.getNickname());
            verify(usersPlantRepository).save(plant);
        }

        @Test
        @DisplayName("HPF-PLANT-02.1: Should update imageUrl and save when user is owner")
        public void shouldUpdateImageUrlWhenUserIsOwner() {
            String newImageUrl = "https://example.com/image.jpg";
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

            usersPlantService.updateImageUrl(userId, userPlantId, newImageUrl);

            assertEquals(newImageUrl, plant.getImageUrl());
            verify(usersPlantRepository).save(plant);
        }

        @Test
        @DisplayName("HPF-USER-04: Should throw UnauthorizedException when non-owner tries to update")
        public void shouldThrowUnauthorizedExceptionWhenNonOwnerTriesToUpdate() {
            UUID differentUserId = UUID.randomUUID();
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.of(plant));

            assertThrows(UnauthorizedUserPlantAccessException.class, () ->
                    usersPlantService.updateNickname(differentUserId, userPlantId, "New Nickname"));
            verify(usersPlantRepository, never()).save(any());
        }

        @Test
        @DisplayName("HPF-COLL: Should throw NotFoundException when plant does not exist")
        public void shouldThrowNotFoundExceptionWhenPlantDoesNotExist() {
            when(usersPlantRepository.findById(userPlantId)).thenReturn(Optional.empty());

            assertThrows(UserPlantNotFoundException.class, () ->
                    usersPlantService.updateNickname(userId, userPlantId, "New Nickname"));
            verify(usersPlantRepository, never()).save(any());
        }
    }

}
