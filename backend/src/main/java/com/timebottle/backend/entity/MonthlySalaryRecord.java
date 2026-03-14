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
@Table(name = "monthly_salary_record")
public class MonthlySalaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", nullable = false, length = 50)
    private String uid;

    @Column(name = "month", nullable = false, length = 7)
    private String month;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "attendance_days", nullable = false, precision = 5, scale = 2)
    private BigDecimal attendanceDays = BigDecimal.ZERO;

    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 5)
    private BigDecimal basicSalary = BigDecimal.ZERO;

    @Column(name = "performance_coefficient", nullable = false, precision = 7, scale = 5)
    private BigDecimal performanceCoefficient = BigDecimal.ONE;

    @Column(name = "performance_salary", nullable = false, precision = 12, scale = 5)
    private BigDecimal performanceSalary = BigDecimal.ZERO;

    @Column(name = "position_performance", nullable = false, precision = 12, scale = 5)
    private BigDecimal positionPerformance = BigDecimal.ZERO;

    @Column(name = "meal_allowance", nullable = false, precision = 12, scale = 5)
    private BigDecimal mealAllowance = BigDecimal.ZERO;

    @Column(name = "housing_allowance", nullable = false, precision = 12, scale = 5)
    private BigDecimal housingAllowance = BigDecimal.ZERO;

    @Column(name = "full_attendance_bonus", nullable = false, precision = 12, scale = 5)
    private BigDecimal fullAttendanceBonus = BigDecimal.ZERO;

    @Column(name = "other_bonus", nullable = false, precision = 12, scale = 5)
    private BigDecimal otherBonus = BigDecimal.ZERO;

    @Column(name = "pension_insurance", nullable = false, precision = 12, scale = 5)
    private BigDecimal pensionInsurance = BigDecimal.ZERO;

    @Column(name = "medical_insurance", nullable = false, precision = 12, scale = 5)
    private BigDecimal medicalInsurance = BigDecimal.ZERO;

    @Column(name = "unemployment_insurance", nullable = false, precision = 12, scale = 5)
    private BigDecimal unemploymentInsurance = BigDecimal.ZERO;

    @Column(name = "late_deduction", nullable = false, precision = 12, scale = 5)
    private BigDecimal lateDeduction = BigDecimal.ZERO;

    @Column(name = "overtime_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "overtime_salary", nullable = false, precision = 12, scale = 5)
    private BigDecimal overtimeSalary = BigDecimal.ZERO;

    @Column(name = "total_payable", nullable = false, precision = 12, scale = 5)
    private BigDecimal totalPayable = BigDecimal.ZERO;

    @Column(name = "total_deduction", nullable = false, precision = 12, scale = 5)
    private BigDecimal totalDeduction = BigDecimal.ZERO;

    @Column(name = "net_salary", nullable = false, precision = 12, scale = 5)
    private BigDecimal netSalary = BigDecimal.ZERO;

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
