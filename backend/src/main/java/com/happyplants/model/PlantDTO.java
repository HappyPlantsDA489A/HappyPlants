package com.happyplants.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantDTO {
    int id;
    String genus;
    String family;

    public PlantDTO(){
    }

    @JsonProperty("common_name")
    private String common_name;

    @JsonProperty ("scientific_name")
    private String[] scientific_name;

}
