package com.happyplants.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PerenualPlantData(
        int id,
        @JsonProperty("common_name") String commonName,
        @JsonProperty("scientific_name") List<String> scientificName,
        @JsonProperty("family") String familyName,
        String cultivar,
        @JsonProperty("species_epithet") String speciesEpithet,
        String genus,
        @JsonProperty("description") String plantDescription,
        @JsonProperty("watering") String wateringDescription,
        @JsonProperty("sunlight") List<String> sunDescription,
        @JsonProperty("default_image") PerenualImageData defaultImage
) {
        public Integer perenualId() {
                return id;
        }
}
