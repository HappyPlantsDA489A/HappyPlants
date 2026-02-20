package com.happyplants.model.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.happyplants.model.Plant}
 */
public record PlantDTO(
        UUID id,
        Integer perenualId,
        @NotNull String commonName,
        @NotNull String scientificName,
        String familyName,
        String cultivar,
        String speciesEpithet,
        String genus,
        String plantDescription,
        String wateringDescription,
        String sunDescription
        ) implements Serializable {
}