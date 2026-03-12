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
import java.time.LocalTime;
import java.time.ZoneId;

@Data
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "type", nullable = false, columnDefinition = "ENUM('0', '1')")
    private String type;

    @Column(name = "account", nullable = false, columnDefinition = "ENUM('现金','微信','支付宝','银行卡','其他') DEFAULT '现金'")
    private String account = "现金";

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "remark")
    private String remark;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "bill_time")
    private LocalTime billTime;

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
        if (isDeleted == null) isDeleted = "0";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }
}
