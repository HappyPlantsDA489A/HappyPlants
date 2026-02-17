package com.happyplants.controller;

import com.happyplants.model.Plant;
import com.happyplants.model.User;
import com.happyplants.model.UsersPlant;
import com.happyplants.model.dto.PlantDTO;
import com.happyplants.model.dto.UsersPlantResponseDTO;
import com.happyplants.service.PlantService;
import com.happyplants.service.UserService;
import com.happyplants.service.UsersPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/plants")
@Tag(name = "User Plants")
public class UserPlantsController {

    private final UserService userService;
    private final PlantService plantService;
    private final UsersPlantService usersPlantService;

    public UserPlantsController(UserService userService, PlantService plantService, UsersPlantService usersPlantService) {
        this.userService = userService;
        this.plantService = plantService;
        this.usersPlantService = usersPlantService;
    }



    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsersPlantResponseDTO> addPlantToLibrary(@RequestBody PlantDTO plantDTO, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        Plant plant = plantService.getOrCreatePlant(plantDTO);
        UsersPlant newUserPlant = usersPlantService.addPlantToUser(user, plant, plantDTO);
        UsersPlantResponseDTO responseDTO = usersPlantService.convertToDto(newUserPlant);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UsersPlantResponseDTO>> getUserPlants(Authentication auth) {
        User user = userService.getCurrentUser(auth);
        List<UsersPlant> plants = usersPlantService.getPlantsForUser(user.getId());

        List<UsersPlantResponseDTO> response = plants.stream().map(usersPlantService::convertToDto).toList();
        return ResponseEntity.ok(response);
    }
}
