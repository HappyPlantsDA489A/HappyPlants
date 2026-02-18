package com.happyplants.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {
    List<PlantDTO> data;

    public List<PlantDTO> getData() {
        return data;
    }

    public void setData(List<PlantDTO> data) {
        this.data = data;
    }
}
