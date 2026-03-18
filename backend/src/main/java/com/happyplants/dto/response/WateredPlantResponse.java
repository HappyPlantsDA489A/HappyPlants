package com.happyplants.dto.response;

import java.time.OffsetDateTime;

public record WateredPlantResponse(
        OffsetDateTime wateredAt
) {
}
