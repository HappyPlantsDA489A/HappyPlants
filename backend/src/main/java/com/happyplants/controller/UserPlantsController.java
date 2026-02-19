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

@RestController
@RequestMapping("/user/plants")
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
        UserPlantDTO plantDto = usersPlantService.convertToDto(newUserPlant);
        return ResponseEntity.status(HttpStatus.CREATED).body(plantDto);
    }

    @GetMapping
    @Operation(summary = "Get all plants in a users library")
    @PreAuthorize("isAuthenticated()")
    public List<UserPlantDTO> getUserPlants(Authentication auth) {
        User user = userService.getCurrentUser(auth);
        return usersPlantService.getPlantsForUser(user.getId())
                .stream()
                .map(usersPlantService::convertToDto)
                .toList();
    }
}
