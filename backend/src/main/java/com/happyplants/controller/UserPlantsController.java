package com.happyplants.controller;

import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.dto.UserPlantDTO;
import com.happyplants.service.UserService;
import com.happyplants.service.UsersPlantService;
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
public class UserPlantsController {

    private final UserService userService;
    private final UsersPlantService usersPlantService;

    public UserPlantsController(UserService userService, UsersPlantService usersPlantService) {
        this.userService = userService;
        this.usersPlantService = usersPlantService;
    }



    @PostMapping("/{perenualId}")
    @Operation(summary = "Add plant to user library")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserPlantDTO> addPlantToLibrary(@PathVariable int perenualId, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        UsersPlant newUserPlant = usersPlantService.addPlantToUser(user, perenualId);
        UserPlantDTO plantDto = usersPlantService.convertToDto(newUserPlant, null, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(plantDto);
    }

    @GetMapping
    @Operation(summary = "Get all plants in a user's library")
    @PreAuthorize("isAuthenticated()")
    public List<UserPlantDTO> getUserPlants(Authentication auth) {
        User user = userService.getCurrentUser(auth);

        return usersPlantService.getPlantsForUser(user.getId());
    }

    @GetMapping("/{plantId}")
    @Operation(summary = "Get a specific plant from the user's library")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserPlantDTO> getUserPlant(
            @PathVariable UUID plantId,
            Authentication auth
    ) {
        User user = userService.getCurrentUser(auth);
        UserPlantDTO plant = usersPlantService.getPlantForUser(plantId, user.getId());

        return ResponseEntity.ok(plant);
    }

    @DeleteMapping("/{userPlantId}")
    @Operation(summary = "Remove a plant from a user's library")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeUserPlant(@PathVariable UUID userPlantId, Authentication auth) {
        User user = userService.getCurrentUser(auth);
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
}
