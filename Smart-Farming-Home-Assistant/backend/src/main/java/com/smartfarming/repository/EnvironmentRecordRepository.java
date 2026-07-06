package com.smartfarming.repository;

import com.smartfarming.entity.EnvironmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnvironmentRecordRepository extends JpaRepository<EnvironmentRecord, Long> {

    List<EnvironmentRecord> findByUserIdAndRecordTimeBetweenOrderByRecordTimeAsc(
            Long userId, LocalDateTime startTime, LocalDateTime endTime);

    List<EnvironmentRecord> findTop1ByUserIdOrderByRecordTimeDesc(Long userId);
}
