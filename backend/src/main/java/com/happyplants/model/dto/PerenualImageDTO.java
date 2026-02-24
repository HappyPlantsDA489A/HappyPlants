package com.happyplants.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerenualImageDTO(
        @JsonProperty("medium_url")
        String imageUrl
) {
}
