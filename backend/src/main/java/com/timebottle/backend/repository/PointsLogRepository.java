package com.timebottle.backend.repository;

import com.timebottle.backend.entity.PointsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PointsLogRepository extends JpaRepository<PointsLog, Long> {
    List<PointsLog> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    @Query("SELECT p FROM PointsLog p WHERE p.userId = :userId AND p.type = :type AND DATE(p.createdAt) = DATE(:date)")
    List<PointsLog> findByUserIdAndTypeAndDate(@Param("userId") Integer userId, @Param("type") String type, @Param("date") Date date);
    
    @Query("SELECT p FROM PointsLog p WHERE p.userId = :userId AND p.type = :type AND DATE(p.createdAt) = CURRENT_DATE")
    List<PointsLog> findTodaySignInLog(@Param("userId") Integer userId, @Param("type") String type);
}
