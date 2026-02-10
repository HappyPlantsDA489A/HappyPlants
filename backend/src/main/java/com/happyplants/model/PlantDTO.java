package com.happyplants.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantDTO {
    private int id;
    private String genus;
    private String family;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("scientific_name")
    private String[] scientificName;

    @JsonProperty("default_image")
    private String imageURL;

    private String watering;
    private String sunlight;

    public PlantDTO() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String[] getScientificName() {
        return scientificName;
    }

    public void setScientificName(String[] scientificName) {
        this.scientificName = scientificName;
    }

    public String getWatering() {
        return watering;
    }

    public void setWatering(String watering) {
        this.watering = watering;
    }

    public String getSunlight() {
        return sunlight;
    }

    public void setSunlight(String sunlight) {
        this.sunlight = sunlight;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    // Convenience methods for frontend compatibility
    public String getName() {
        return commonName != null ? commonName : (scientificName != null && scientificName.length > 0 ? scientificName[0] : "Unknown");
    }

    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        if (scientificName != null && scientificName.length > 0) {
            desc.append("Scientific name: ").append(scientificName[0]).append(". ");
        }
        if (family != null) {
            desc.append("Family: ").append(family).append(". ");
        }
        if (genus != null) {
            desc.append("Genus: ").append(genus).append(". ");
        }
        if (watering != null) {
            desc.append("Watering: ").append(watering).append(". ");
        }
        if (sunlight != null) {
            desc.append("Sunlight: ").append(sunlight).append(".");
        }
        return desc.toString();
    }
}
