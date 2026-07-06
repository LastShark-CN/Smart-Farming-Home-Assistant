package com.smartfarming.repository;

import com.smartfarming.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByUserId(Long userId);

    List<Crop> findByUserIdAndCategory(Long userId, String category);
}
