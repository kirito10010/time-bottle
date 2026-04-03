package com.timebottle.backend.scheduler;

import com.timebottle.backend.service.StreakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StreakRewardScheduler {

    private static final Logger log = LoggerFactory.getLogger(StreakRewardScheduler.class);

    @Autowired
    private StreakService streakService;

    @Scheduled(cron = "0 0 * * * ?")
    public void distributeHourlyRewards() {
        try {
            log.info("开始发放每小时连对答题奖励...");
            streakService.distributeRewards();
            log.info("每小时连对答题奖励发放完成");
        } catch (Exception e) {
            log.error("发放每小时连对答题奖励失败", e);
        }
    }
}
