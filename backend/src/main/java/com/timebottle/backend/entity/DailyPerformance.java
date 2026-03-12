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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Entity
@Table(name = "daily_performance")
public class DailyPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", nullable = false, length = 50)
    private String uid;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "process_type", nullable = false, columnDefinition = "ENUM('作业', '质检')")
    private String processType;

    @Column(name = "quota_efficiency", nullable = false, precision = 10, scale = 2)
    private BigDecimal quotaEfficiency;

    @Column(name = "actual_workload", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualWorkload;

    @Column(name = "performance_man_days", nullable = false, precision = 10, scale = 3, insertable = false, updatable = false)
    private BigDecimal performanceManDays;

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
