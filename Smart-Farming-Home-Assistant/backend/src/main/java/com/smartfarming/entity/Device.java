package com.smartfarming.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String deviceCode;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String data;

    private LocalDateTime lastUpdate;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        lastUpdate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = LocalDateTime.now();
    }
}