package com.timebottle.backend.repository;

import com.timebottle.backend.entity.OvertimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRecordRepository extends JpaRepository<OvertimeRecord, Long> {

    List<OvertimeRecord> findByUid(String uid);
    
    List<OvertimeRecord> findByUidOrderByRecordDateDesc(String uid);
    
    List<OvertimeRecord> findByUidAndRecordDate(String uid, LocalDate recordDate);
    
    List<OvertimeRecord> findByUidAndRecordDateBetween(String uid, LocalDate startDate, LocalDate endDate);
    
    List<OvertimeRecord> findByProjectId(Long projectId);
    
    boolean existsByProjectId(Long projectId);
}
