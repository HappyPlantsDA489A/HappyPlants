package com.happyplants.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
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

}