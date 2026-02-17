package com.happyplants.model.dto;

import java.util.UUID;

public record PlantResponseDTO(
        Integer perenualId,
        String commonName,
        String scientificName,
        String wateringDescription,
        String sunDescription
) {}


