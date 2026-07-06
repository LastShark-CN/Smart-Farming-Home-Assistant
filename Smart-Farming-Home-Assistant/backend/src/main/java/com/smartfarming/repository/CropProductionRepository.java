package com.smartfarming.repository;

import com.smartfarming.entity.CropProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropProductionRepository extends JpaRepository<CropProduction, Long> {

    List<CropProduction> findByUserId(Long userId);

    List<CropProduction> findByUserIdAndYear(Long userId, Integer year);

    @Query("SELECT c.cropName, SUM(c.production), c.area FROM CropProduction c WHERE c.userId = ?1 GROUP BY c.cropName, c.area")
    List<Object[]> aggregateProductionByUserId(Long userId);
}
