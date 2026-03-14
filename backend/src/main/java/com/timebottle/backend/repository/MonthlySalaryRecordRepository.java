package com.timebottle.backend.repository;

import com.timebottle.backend.entity.MonthlySalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlySalaryRecordRepository extends JpaRepository<MonthlySalaryRecord, Long> {

    List<MonthlySalaryRecord> findByUidOrderByMonthDesc(String uid);
    
    Optional<MonthlySalaryRecord> findByUidAndMonth(String uid, String month);
    
    boolean existsByUidAndMonth(String uid, String month);
    
    void deleteByUidAndMonth(String uid, String month);
}
