package com.happyplants.controller;

import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.dto.*;
import com.happyplants.service.UserService;
import com.happyplants.service.UserPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/plants")
@CrossOrigin(origins = "*")
@Tag(name = "User Plants")
public class UserPlantController {

    private final UserService userService;
    private final UserPlantService usersPlantService;

    public UserPlantController(UserService userService, UserPlantService usersPlantService) {
        this.userService = userService;
        this.usersPlantService = usersPlantService;
    }



    @PostMapping("/{perenualId}")
    @Operation(summary = "Add plant to user library")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserPlantDTO> addPlantToLibrary(@PathVariable int perenualId, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        UsersPlant newUserPlant = usersPlantService.addPlantToUser(user, perenualId);

        if (newUserPlant == null) {
            return ResponseEntity.notFound().build();
        }
        UserPlantDTO plantDto = usersPlantService.convertToDto(newUserPlant, null, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(plantDto);
    }

    @GetMapping
    @Operation(summary = "Get all plants in a user's library")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserPlantDTO>> getUserPlants(Authentication auth) {
        User user = userService.getCurrentUser(auth);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<UserPlantDTO> userPlants = usersPlantService.getPlantsForUser(user.getId());
        return ResponseEntity.ok(userPlants);
    }

    @GetMapping("/{plantId}")
    @Operation(summary = "Get a specific plant from the user's library")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserPlantDTO> getUserPlant(
            @PathVariable UUID plantId,
            Authentication auth
    ) {
        User user = userService.getCurrentUser(auth);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserPlantDTO plant = usersPlantService.getPlantForUser(plantId, user.getId());
        if (plant == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(plant);
    }

    @DeleteMapping("/{userPlantId}")
    @Operation(summary = "Remove a plant from a user's library")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeUserPlant(@PathVariable UUID userPlantId, Authentication auth) {
        User user = userService.getCurrentUser(auth);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        usersPlantService.removeUserPlant(user.getId(), userPlantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userPlantId}/dead")
    @Operation(summary = "Mark a users plant as dead")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markPlantAsDead(@PathVariable UUID userPlantId, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        usersPlantService.markPlantAsDead(user.getId(), userPlantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userPlantId}/watering-frequency")
    @Operation(summary = "Update watering frequency for a user's plant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateWateringFrequency(@PathVariable UUID userPlantId, @RequestBody UpdateWateringFrequencyDTO wateringDTO, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        usersPlantService.updateWateringFrequency(user.getId(), userPlantId, wateringDTO.wateringFrequencyDays());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userPlantId}/water")
    @Operation(summary = "Water a user's plant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WateredPlantDTO> waterPlant(@PathVariable UUID userPlantId, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        WateredPlantDTO plantDTO = usersPlantService.waterPlant(user.getId(), userPlantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(plantDTO);
    }

    @GetMapping("/{userPlantId}/waterings")
    @Operation(summary = "Get watering history for a user's plant")
    @PreAuthorize("isAuthenticated()")
    public List<WateredPlantDTO> getWateringHistory(@PathVariable UUID userPlantId, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        return usersPlantService.getWateringHistory(user.getId(), userPlantId);
    }

    @PatchMapping("/{userPlantId}/nickname")
    @Operation(summary = "Update nickname for a user's plant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateNickname(@PathVariable UUID userPlantId, @RequestBody UpdateNicknameDTO updateNicknameDTO, Authentication auth
    ) {
        User user = userService.getCurrentUser(auth);
        usersPlantService.updateNickname(user.getId(), userPlantId, updateNicknameDTO.nickname());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userPlantId}/image-url")
    @Operation(summary = "Update image URL for a user's plant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateImageUrl(@PathVariable UUID userPlantId, @RequestBody UpdateImageUrlDTO updateImageUrlDTO, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        usersPlantService.updateImageUrl(user.getId(), userPlantId, updateImageUrlDTO.imageUrl());
        return ResponseEntity.noContent().build();
    }
}
