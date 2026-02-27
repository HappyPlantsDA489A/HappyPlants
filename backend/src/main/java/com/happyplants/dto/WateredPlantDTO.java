package com.happyplants.dto;

import jakarta.persistence.Column;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WateredPlantDTO(
        OffsetDateTime occuredAt
) {}

