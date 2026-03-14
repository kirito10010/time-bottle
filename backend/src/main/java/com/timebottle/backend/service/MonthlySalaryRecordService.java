package com.timebottle.backend.service;

import com.timebottle.backend.entity.MonthlySalaryRecord;
import com.timebottle.backend.repository.MonthlySalaryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class MonthlySalaryRecordService {

    @Autowired
    private MonthlySalaryRecordRepository repository;

    public List<MonthlySalaryRecord> getByUid(String uid) {
        return repository.findByUidOrderByMonthDesc(uid);
    }

    public Optional<MonthlySalaryRecord> getById(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id);
    }

    public Optional<MonthlySalaryRecord> getByUidAndMonth(String uid, String month) {
        return repository.findByUidAndMonth(uid, month);
    }

    @Transactional
    public MonthlySalaryRecord create(MonthlySalaryRecord record) {
        if (repository.existsByUidAndMonth(record.getUid(), record.getMonth())) {
            throw new RuntimeException("该月份已存在工资记录");
        }
        calculateTotals(record);
        return repository.save(record);
    }

    @Transactional
    public MonthlySalaryRecord update(Long id, MonthlySalaryRecord updated) {
        if (id == null) {
            throw new RuntimeException("ID不能为空");
        }
        MonthlySalaryRecord existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("工资记录不存在"));

        existing.setMonth(updated.getMonth());
        existing.setPeriodStartDate(updated.getPeriodStartDate());
        existing.setPeriodEndDate(updated.getPeriodEndDate());
        existing.setAttendanceDays(updated.getAttendanceDays());
        existing.setBasicSalary(updated.getBasicSalary());
        existing.setPerformanceCoefficient(updated.getPerformanceCoefficient());
        existing.setPerformanceSalary(updated.getPerformanceSalary());
        existing.setPositionPerformance(updated.getPositionPerformance());
        existing.setMealAllowance(updated.getMealAllowance());
        existing.setHousingAllowance(updated.getHousingAllowance());
        existing.setFullAttendanceBonus(updated.getFullAttendanceBonus());
        existing.setOtherBonus(updated.getOtherBonus());
        existing.setPensionInsurance(updated.getPensionInsurance());
        existing.setMedicalInsurance(updated.getMedicalInsurance());
        existing.setUnemploymentInsurance(updated.getUnemploymentInsurance());
        existing.setLateDeduction(updated.getLateDeduction());
        existing.setOvertimeHours(updated.getOvertimeHours());
        existing.setOvertimeSalary(updated.getOvertimeSalary());

        calculateTotals(existing);
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) return;
        repository.deleteById(id);
    }

    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        repository.deleteAllById(ids);
    }

    private void calculateTotals(MonthlySalaryRecord record) {
        BigDecimal totalPayable = BigDecimal.ZERO;
        totalPayable = totalPayable.add(nullToZero(record.getBasicSalary()));
        totalPayable = totalPayable.add(nullToZero(record.getPerformanceSalary()));
        totalPayable = totalPayable.add(nullToZero(record.getPositionPerformance()));
        totalPayable = totalPayable.add(nullToZero(record.getMealAllowance()));
        totalPayable = totalPayable.add(nullToZero(record.getHousingAllowance()));
        totalPayable = totalPayable.add(nullToZero(record.getFullAttendanceBonus()));
        totalPayable = totalPayable.add(nullToZero(record.getOtherBonus()));
        totalPayable = totalPayable.add(nullToZero(record.getOvertimeSalary()));
        record.setTotalPayable(totalPayable.setScale(5, RoundingMode.HALF_UP));

        BigDecimal totalDeduction = BigDecimal.ZERO;
        totalDeduction = totalDeduction.add(nullToZero(record.getPensionInsurance()));
        totalDeduction = totalDeduction.add(nullToZero(record.getMedicalInsurance()));
        totalDeduction = totalDeduction.add(nullToZero(record.getUnemploymentInsurance()));
        totalDeduction = totalDeduction.add(nullToZero(record.getLateDeduction()));
        record.setTotalDeduction(totalDeduction.setScale(5, RoundingMode.HALF_UP));

        BigDecimal netSalary = totalPayable.subtract(totalDeduction);
        record.setNetSalary(netSalary.setScale(5, RoundingMode.HALF_UP));
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
