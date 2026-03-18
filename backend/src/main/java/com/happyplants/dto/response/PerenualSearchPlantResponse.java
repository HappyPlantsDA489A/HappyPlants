package com.happyplants.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PerenualSearchPlantResponse(
        @JsonProperty("id") int perenualId,
        @JsonProperty("common_name") String commonName,
        @JsonProperty("scientific_name") List<String> scientificName,
        @JsonProperty("family") String familyName,
        String cultivar,
        @JsonProperty("species_epithet") String speciesEpithet,
        String genus,
        String imageUrl
) {
}
