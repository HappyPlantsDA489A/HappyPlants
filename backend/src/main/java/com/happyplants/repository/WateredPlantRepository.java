package com.happyplants.repository;

import com.happyplants.model.WateredPlant;
import com.happyplants.model.WateredPlantId;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface WateredPlantRepository extends JpaRepository<WateredPlant, WateredPlantId>, JpaSpecificationExecutor<WateredPlant> {

    @Query("""
    SELECT w 
    FROM WateredPlant w 
    WHERE w.usersPlants.id = :userPlantId 
    ORDER BY w.id.occuredAt DESC
    """)
    List<WateredPlant> findHistory(UUID userPlantId);

}