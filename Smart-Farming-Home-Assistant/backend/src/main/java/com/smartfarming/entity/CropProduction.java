package com.smartfarming.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crop_production")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private Double production;

    private Double area;

    @Column(name = "crop_year", nullable = false)
    private Integer year;

    @Column(name = "crop_month", nullable = false)
    private Integer month;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
