package com.happyplants.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerenualImageDTO(
        @JsonProperty("original_url")
        String originalUrl,

        @JsonProperty("regular_url")
        String regularUrl,

        @JsonProperty("medium_url")
        String mediumUrl,

        @JsonProperty("small_url")
        String smallUrl,

        @JsonProperty("thumbnail")
        String thumbnail
) {
}
