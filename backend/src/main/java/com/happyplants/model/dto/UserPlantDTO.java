package com.happyplants.model.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserPlantDTO(
        UUID id,
        String nickname,
        String imageUrl,
        Integer wateringFrequencyDays,
        OffsetDateTime createdAt,
        OffsetDateTime diedAt,
        OffsetDateTime lastWateredAt,
        int timesWatered,
        PlantDTO plant
) implements Serializable {
}
