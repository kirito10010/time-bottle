package com.timebottle.backend.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "hourly_streak")
@IdClass(HourlyStreakId.class)
public class HourlyStreak {

    @Id
    @Column(name = "uid", nullable = false)
    private Integer uid;

    @Id
    @Column(name = "hour_time", nullable = false)
    private Date hourTime;

    @Column(name = "max_streak", nullable = false)
    private Integer maxStreak = 0;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Date getHourTime() {
        return hourTime;
    }

    public void setHourTime(Date hourTime) {
        this.hourTime = hourTime;
    }

    public Integer getMaxStreak() {
        return maxStreak;
    }

    public void setMaxStreak(Integer maxStreak) {
        this.maxStreak = maxStreak;
    }
}
