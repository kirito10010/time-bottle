package com.timebottle.backend.service;

import com.timebottle.backend.entity.OvertimeRecord;
import com.timebottle.backend.repository.OvertimeRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OvertimeRecordService {

    @Autowired
    private OvertimeRecordRepository overtimeRecordRepository;

    public List<OvertimeRecord> findByUid(String uid) {
        return overtimeRecordRepository.findByUidOrderByRecordDateDesc(uid);
    }

    public OvertimeRecord findById(@NonNull Long id) {
        return overtimeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("加班记录不存在"));
    }

    public OvertimeRecord create(String uid, LocalDate recordDate, @NonNull Long projectId, 
                                  BigDecimal overtimeHours, String description) {
        OvertimeRecord record = new OvertimeRecord();
        record.setUid(uid);
        record.setRecordDate(recordDate);
        record.setProjectId(projectId);
        record.setOvertimeHours(overtimeHours);
        record.setDescription(description);
        
        return overtimeRecordRepository.save(record);
    }

    public OvertimeRecord update(@NonNull Long id, LocalDate recordDate, @NonNull Long projectId, 
                                  BigDecimal overtimeHours, String description) {
        OvertimeRecord record = overtimeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("加班记录不存在"));
        
        record.setRecordDate(recordDate);
        record.setProjectId(projectId);
        record.setOvertimeHours(overtimeHours);
        record.setDescription(description);
        
        return overtimeRecordRepository.save(record);
    }

    @SuppressWarnings("null")
    public void delete(@NonNull Long id) {
        OvertimeRecord record = overtimeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("加班记录不存在"));
        overtimeRecordRepository.delete(record);
    }

    public void deleteBatch(@NonNull List<Long> ids) {
        overtimeRecordRepository.deleteAllById(ids);
    }
}
