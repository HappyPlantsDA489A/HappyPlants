package com.happyplants.dto.response;

import java.util.UUID;

public record PlantResponse(
        UUID id,
        int perenualId,
        String commonName,
        String scientificName,
        String familyName,
        String cultivar,
        String speciesEpithet,
        String genus,
        String plantDescription,
        String wateringDescription,
        String sunDescription,
        String imageUrl
) {
}
