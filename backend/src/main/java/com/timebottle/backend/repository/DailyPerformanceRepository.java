package com.timebottle.backend.repository;

import com.timebottle.backend.entity.DailyPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyPerformanceRepository extends JpaRepository<DailyPerformance, Long> {

    List<DailyPerformance> findByUid(String uid);
    
    List<DailyPerformance> findByUidOrderByRecordDateDesc(String uid);
    
    List<DailyPerformance> findByUidAndRecordDate(String uid, LocalDate recordDate);
    
    List<DailyPerformance> findByUidAndRecordDateBetween(String uid, LocalDate startDate, LocalDate endDate);
    
    List<DailyPerformance> findByProjectId(Long projectId);
    
    boolean existsByProjectId(Long projectId);
}
