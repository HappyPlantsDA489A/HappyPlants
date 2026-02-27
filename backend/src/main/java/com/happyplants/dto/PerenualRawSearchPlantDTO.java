package com.happyplants.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PerenualRawSearchPlantDTO(
        @JsonProperty("id") Integer perenualId,
        @JsonProperty("common_name") String commonName,
        @JsonProperty("scientific_name") List<String> scientificName,
        @JsonProperty("family") String familyName,
        @JsonProperty("cultivar") String cultivar,
        @JsonProperty("species_epithet") String speciesEpithet,
        @JsonProperty("genus") String genus,
        @JsonProperty("default_image") PerenualImageDTO defaultImage
) {}
