package com.happyplants.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PerenualImageData(
        @JsonProperty("original_url") String originalUrl,
        @JsonProperty("regular_url") String regularUrl,
        @JsonProperty("medium_url") String mediumUrl,
        @JsonProperty("small_url") String smallUrl,
        String thumbnail
) {
}
