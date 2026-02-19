package com.happyplants.repository;

import com.happyplants.model.UsersPlant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface UsersPlantRepository extends JpaRepository<UsersPlant, UUID>, JpaSpecificationExecutor<UsersPlant> {
    List<UsersPlant> findAllByUserId(UUID userId);
}