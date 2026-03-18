package com.happyplants.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PerenualRawSearchPlantData(
        int id,
        @JsonProperty("common_name") String commonName,
        @JsonProperty("scientific_name") List<String> scientificName,
        @JsonProperty("family") String familyName,
        String cultivar,
        @JsonProperty("species_epithet") String speciesEpithet,
        String genus,
        @JsonProperty("default_image") PerenualImageData defaultImage
) {
    public Integer perenualId() {
        return id;
    }
}
