package com.happyplants.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantDTO {
    int id;
    String common_name;
    String scientific_name;
    String genus;
    String family;

    public PlantDTO(){
    }
}
