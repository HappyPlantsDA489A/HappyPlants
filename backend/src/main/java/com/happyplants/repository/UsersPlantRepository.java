package com.happyplants.repository;

import com.happyplants.model.UsersPlant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UsersPlantRepository extends JpaRepository<UsersPlant, UUID>, JpaSpecificationExecutor<UsersPlant> {
    List<UsersPlant> findAllByUserId(UUID userId);

    @Query("""
        SELECT up, MAX(wp.id.occuredAt), COUNT(wp.id.occuredAt)
        FROM UsersPlant up
        LEFT JOIN WateredPlant wp ON wp.usersPlants.id = up.id
        WHERE up.user.id = :userId
        GROUP BY up.id, up.user.id, up.plant.id
        ORDER BY up.createdAt DESC
    """)
    List<Object[]> findAllWithLastWateredByUserId(@Param("userId") UUID userId);
}