package com.happyplants.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerenualPlantDTO(
       @JsonProperty("id")
       Integer perenualId,

        @JsonProperty("common_name")
        String commonName,

        @JsonProperty("scientific_name")
       List<String> scientificName,

        @JsonProperty("family")
        String familyName,

        String cultivar,

        @JsonProperty("species_epithet")
        String speciesEpithet,

        String genus,

        @JsonProperty("watering")
        String wateringDescription,

        @JsonProperty("sunlight")
        String sunDescription
){}


