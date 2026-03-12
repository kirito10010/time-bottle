package com.timebottle.backend.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Entity
@Table(name = "production_project_config")
public class ProductionProjectConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", nullable = false, length = 50)
    private String uid;

    @Column(name = "project_name", nullable = false, length = 100)
    private String projectName;

    @Column(name = "operation_quota", nullable = false, precision = 10, scale = 2)
    private BigDecimal operationQuota;

    @Column(name = "quality_quota", nullable = false, precision = 10, scale = 2)
    private BigDecimal qualityQuota;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }
}
