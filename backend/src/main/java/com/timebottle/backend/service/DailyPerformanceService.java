package com.timebottle.backend.service;

import com.timebottle.backend.entity.DailyPerformance;
import com.timebottle.backend.repository.DailyPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class DailyPerformanceService {

    @Autowired
    private DailyPerformanceRepository dailyPerformanceRepository;

    public List<DailyPerformance> findByUid(String uid) {
        return dailyPerformanceRepository.findByUidOrderByRecordDateDesc(uid);
    }

    public DailyPerformance findById(@NonNull Long id) {
        return dailyPerformanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("绩效记录不存在"));
    }

    public DailyPerformance create(String uid, LocalDate recordDate, @NonNull Long projectId, String processType,
                                   BigDecimal quotaEfficiency, BigDecimal actualWorkload) {
        DailyPerformance performance = new DailyPerformance();
        performance.setUid(uid);
        performance.setRecordDate(recordDate);
        performance.setProjectId(projectId);
        performance.setProcessType(processType);
        performance.setQuotaEfficiency(quotaEfficiency);
        performance.setActualWorkload(actualWorkload);
        
        if (quotaEfficiency.compareTo(BigDecimal.ZERO) != 0) {
            performance.setPerformanceManDays(actualWorkload.divide(quotaEfficiency, 2, RoundingMode.HALF_UP));
        }
        
        return dailyPerformanceRepository.save(performance);
    }

    public DailyPerformance update(@NonNull Long id, LocalDate recordDate, @NonNull Long projectId, String processType,
                                   BigDecimal quotaEfficiency, BigDecimal actualWorkload) {
        DailyPerformance performance = dailyPerformanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("绩效记录不存在"));
        
        performance.setRecordDate(recordDate);
        performance.setProjectId(projectId);
        performance.setProcessType(processType);
        performance.setQuotaEfficiency(quotaEfficiency);
        performance.setActualWorkload(actualWorkload);
        
        if (quotaEfficiency.compareTo(BigDecimal.ZERO) != 0) {
            performance.setPerformanceManDays(actualWorkload.divide(quotaEfficiency, 2, RoundingMode.HALF_UP));
        }
        
        return dailyPerformanceRepository.save(performance);
    }

    @SuppressWarnings("null")
    public void delete(@NonNull Long id) {
        DailyPerformance performance = dailyPerformanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("绩效记录不存在"));
        dailyPerformanceRepository.delete(performance);
    }

    public void deleteBatch(@NonNull List<Long> ids) {
        dailyPerformanceRepository.deleteAllById(ids);
    }

    public boolean hasRecordsByProjectId(Long projectId) {
        return dailyPerformanceRepository.existsByProjectId(projectId);
    }
}
