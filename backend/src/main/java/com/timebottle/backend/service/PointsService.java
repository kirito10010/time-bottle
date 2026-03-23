package com.timebottle.backend.service;

import com.timebottle.backend.entity.PointsLog;
import com.timebottle.backend.entity.User;
import com.timebottle.backend.repository.PointsLogRepository;
import com.timebottle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PointsService {

    @Autowired
    private PointsLogRepository pointsLogRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int SIGN_IN_POINTS = 10;
    private static final int ACCOUNTING_POINTS = 1;

    @Transactional
    public Map<String, Object> signIn(@NonNull Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        List<PointsLog> todayLogs = pointsLogRepository.findTodaySignInLog(userId, "sign_in");
        if (!todayLogs.isEmpty()) {
            result.put("success", false);
            result.put("message", "今日已签到，请明天再来！");
            return result;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        User user = userOpt.get();

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChange(SIGN_IN_POINTS);
        log.setType("sign_in");
        log.setRemark("每日签到奖励");
        pointsLogRepository.save(log);

        user.setPoints(user.getPoints() + SIGN_IN_POINTS);
        user.setUpdatedAt(new Date());
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "签到成功！获得 " + SIGN_IN_POINTS + " 积分");
        result.put("points", user.getPoints());
        result.put("earnedPoints", SIGN_IN_POINTS);
        return result;
    }

    @Transactional
    public void addAccountingPoints(@NonNull Integer userId, String remark) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return;
        User user = userOpt.get();

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChange(ACCOUNTING_POINTS);
        log.setType("accounting");
        log.setRemark(remark != null ? remark : "记账奖励");
        pointsLogRepository.save(log);

        user.setPoints(user.getPoints() + ACCOUNTING_POINTS);
        user.setUpdatedAt(new Date());
        userRepository.save(user);
    }

    @Transactional
    public void deductAccountingPoints(@NonNull Integer userId, String remark) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return;
        User user = userOpt.get();

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChange(-ACCOUNTING_POINTS);
        log.setType("accounting");
        log.setRemark(remark != null ? remark : "删除账单扣减");
        pointsLogRepository.save(log);

        user.setPoints(Math.max(0, user.getPoints() - ACCOUNTING_POINTS));
        user.setUpdatedAt(new Date());
        userRepository.save(user);
    }

    public boolean hasSignedInToday(@NonNull Integer userId) {
        List<PointsLog> todayLogs = pointsLogRepository.findTodaySignInLog(userId, "sign_in");
        return !todayLogs.isEmpty();
    }

    public List<PointsLog> getUserPointsLogs(@NonNull Integer userId) {
        return pointsLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Integer getUserPoints(@NonNull Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::getPoints).orElse(0);
    }
}
