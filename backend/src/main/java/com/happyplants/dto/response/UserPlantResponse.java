package com.happyplants.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserPlantResponse(
        UUID id,
        String nickname,
        String imageUrl,
        Integer wateringFrequencyDays,
        OffsetDateTime createdAt,
        OffsetDateTime diedAt,
        OffsetDateTime lastWateredAt,
        int timesWatered,
        PlantResponse plant
) {
}
