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
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Entity
@Table(name = "bill_categories")
public class BillCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "type", nullable = false, columnDefinition = "ENUM('0', '1')")
    private String type;

    @Column(name = "is_default", columnDefinition = "ENUM('0', '1') DEFAULT '0'")
    private String isDefault = "0";

    @Column(name = "sort", columnDefinition = "TINYINT UNSIGNED DEFAULT 0")
    private Integer sort = 0;

    @Column(name = "is_deleted", columnDefinition = "ENUM('0', '1') DEFAULT '0'")
    private String isDeleted = "0";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        if (isDefault == null) isDefault = "0";
        if (sort == null) sort = 0;
        if (isDeleted == null) isDeleted = "0";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }
}
