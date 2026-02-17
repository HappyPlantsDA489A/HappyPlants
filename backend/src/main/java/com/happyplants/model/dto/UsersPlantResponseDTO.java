package com.happyplants.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UsersPlantResponseDTO(
   UUID id,
   String nickname,
   String imageUrl,
   Integer wateringFrequencyDays,
   String sunLight,
   OffsetDateTime createdAt,
   PlantResponseDTO plant
) {}
