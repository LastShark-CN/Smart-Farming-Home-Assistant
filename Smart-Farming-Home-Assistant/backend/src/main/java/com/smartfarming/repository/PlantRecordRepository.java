package com.smartfarming.repository;

import com.smartfarming.entity.PlantRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PlantRecordRepository extends JpaRepository<PlantRecord, Long> {

    Page<PlantRecord> findByUserIdOrderByRecordDateDesc(Long userId, Pageable pageable);

    Page<PlantRecord> findByUserIdAndCropIdOrderByRecordDateDesc(Long userId, Long cropId, Pageable pageable);

    Page<PlantRecord> findByUserIdAndRecordDateOrderByRecordDateDesc(Long userId, LocalDate recordDate, Pageable pageable);

    Page<PlantRecord> findByUserIdAndCropIdAndRecordDateOrderByRecordDateDesc(Long userId, Long cropId, LocalDate recordDate, Pageable pageable);

    void deleteByUserId(Long userId);
}
