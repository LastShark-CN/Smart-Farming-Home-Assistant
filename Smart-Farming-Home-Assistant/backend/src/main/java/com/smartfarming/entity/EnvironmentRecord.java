package com.smartfarming.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "environment_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvironmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Double temperature;
    private Double humidity;
    private Double co2;
    private Double soilPh;

    @Column(nullable = false)
    private LocalDateTime recordTime;

    @PrePersist
    protected void onCreate() {
        if (recordTime == null) {
            recordTime = LocalDateTime.now();
        }
    }
}
