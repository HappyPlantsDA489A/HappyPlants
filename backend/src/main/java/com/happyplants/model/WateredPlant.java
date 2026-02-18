package com.happyplants.model;

import jakarta.persistence.*;

@Entity
@Table(name = "watered_plants")
public class WateredPlant {
    @EmbeddedId
    private WateredPlantId id;

    @MapsId("usersPlantsId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_plants_id", nullable = false)
    private UsersPlant usersPlants;

    public WateredPlantId getId() {
        return id;
    }

    public void setId(WateredPlantId id) {
        this.id = id;
    }

    public UsersPlant getUsersPlants() {
        return usersPlants;
    }

    public void setUsersPlants(UsersPlant usersPlants) {
        this.usersPlants = usersPlants;
    }

}