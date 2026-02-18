package com.happyplants.repository;

import com.happyplants.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PlantRepository extends JpaRepository<Plant, UUID>, JpaSpecificationExecutor<Plant> {
}