package com.timebottle.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Embeddable
public class HourlyStreakId implements Serializable {

    @Column(name = "uid")
    private Integer uid;

    @Column(name = "hour_time")
    private Date hourTime;

    public HourlyStreakId() {}

    public HourlyStreakId(Integer uid, Date hourTime) {
        this.uid = uid;
        this.hourTime = hourTime;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HourlyStreakId that = (HourlyStreakId) o;
        return Objects.equals(uid, that.uid) && Objects.equals(hourTime, that.hourTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, hourTime);
    }
}
