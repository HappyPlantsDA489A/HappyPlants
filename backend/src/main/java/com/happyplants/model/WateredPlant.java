package com.happyplants.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "watered_plants")
public class WateredPlant {
    @EmbeddedId
    private WateredPlantId id;

    @MapsId("usersPlantsId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_plants_id", nullable = false)
    private UsersPlant usersPlants;

}