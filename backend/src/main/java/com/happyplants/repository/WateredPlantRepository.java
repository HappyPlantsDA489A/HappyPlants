package com.happyplants.repository;

import com.happyplants.model.WateredPlant;
import com.happyplants.model.WateredPlantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WateredPlantRepository extends JpaRepository<WateredPlant, WateredPlantId>, JpaSpecificationExecutor<WateredPlant> {
}