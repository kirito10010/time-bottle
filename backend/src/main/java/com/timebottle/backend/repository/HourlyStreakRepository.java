package com.timebottle.backend.repository;

import com.timebottle.backend.entity.HourlyStreak;
import com.timebottle.backend.entity.HourlyStreakId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface HourlyStreakRepository extends JpaRepository<HourlyStreak, HourlyStreakId> {

    @Query(value = "SELECT * FROM hourly_streak WHERE hour_time = :hourTime ORDER BY max_streak DESC LIMIT :limit", nativeQuery = true)
    List<HourlyStreak> findTopByHourTime(@Param("hourTime") Date hourTime, @Param("limit") int limit);

    @Query(value = "SELECT * FROM hourly_streak WHERE uid = :uid AND hour_time = :hourTime", nativeQuery = true)
    Optional<HourlyStreak> findByUidAndHourTime(@Param("uid") Integer uid, @Param("hourTime") Date hourTime);

    @Query(value = "SELECT * FROM hourly_streak WHERE hour_time = :hourTime AND uid = :uid", nativeQuery = true)
    Optional<HourlyStreak> findByUidAndHourTimeNative(@Param("uid") Integer uid, @Param("hourTime") Date hourTime);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO hourly_streak (uid, hour_time, max_streak) VALUES (:uid, :hourTime, :maxStreak) " +
            "ON DUPLICATE KEY UPDATE max_streak = GREATEST(max_streak, :maxStreak)", nativeQuery = true)
    void upsertStreak(@Param("uid") Integer uid, @Param("hourTime") Date hourTime, @Param("maxStreak") int maxStreak);

    @Query(value = "SELECT DISTINCT hour_time FROM hourly_streak ORDER BY hour_time DESC LIMIT 1", nativeQuery = true)
    Date findLatestHourTime();
}
