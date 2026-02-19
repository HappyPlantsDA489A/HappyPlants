package com.happyplants.model.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UsersPlantDTO(
        UUID id,
        String nickname,
        String imageUrl,
        Integer wateringFrequencyDays,
        OffsetDateTime createdAt,
        OffsetDateTime diedAt,
        PlantDTO plant
) implements Serializable {
}
