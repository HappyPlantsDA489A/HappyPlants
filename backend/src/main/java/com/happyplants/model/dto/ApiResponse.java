package com.happyplants.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponse(
        List<PerenualRawSearchPlantDTO> data
){}
