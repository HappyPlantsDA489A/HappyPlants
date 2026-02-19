package com.happyplants.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "perenual_id")
    private Integer perenualId;

    @NotNull
    @Column(name = "common_name", nullable = false, length = Integer.MAX_VALUE)
    private String commonName;

    @NotNull
    @Column(name = "scientific_name", nullable = false, length = Integer.MAX_VALUE)
    private String scientificName;

    @Column(name = "family_name", length = Integer.MAX_VALUE)
    private String familyName;

    @Column(name = "cultivar", length = Integer.MAX_VALUE)
    private String cultivar;

    @Column(name = "species_epithet", length = Integer.MAX_VALUE)
    private String speciesEpithet;

    @Column(name = "genus", length = Integer.MAX_VALUE)
    private String genus;

    @Column(name = "plant_description", length = Integer.MAX_VALUE)
    private String plantDescription;

    @Column(name = "watering_description", length = Integer.MAX_VALUE)
    private String wateringDescription;

    @Column(name = "sun_description", length = Integer.MAX_VALUE)
    private String sunDescription;

    @Column(name = "partial_data")
    private boolean partialData;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getPerenualId() {
        return perenualId;
    }

    public void setPerenualId(Integer perenualId) {
        this.perenualId = perenualId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getCultivar() {
        return cultivar;
    }

    public void setCultivar(String cultivar) {
        this.cultivar = cultivar;
    }

    public String getSpeciesEpithet() {
        return speciesEpithet;
    }

    public void setSpeciesEpithet(String speciesEpithet) {
        this.speciesEpithet = speciesEpithet;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getPlantDescription() {return plantDescription;}

    public void setPlantDescription(String plantDescription) {this.plantDescription = plantDescription;}

    public String getWateringDescription() {
        return wateringDescription;
    }

    public void setWateringDescription(String wateringDescription) {
        this.wateringDescription = wateringDescription;
    }

    public String getSunDescription() {
        return sunDescription;
    }

    public void setSunDescription(String sunDescription) {
        this.sunDescription = sunDescription;
    }

    public boolean isPartialData() {return partialData;}

    public void setPartialData(boolean partialData) {this.partialData = partialData;}

}