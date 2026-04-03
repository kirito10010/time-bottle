package com.timebottle.backend.service;

import com.timebottle.backend.entity.HourlyStreak;
import com.timebottle.backend.entity.Question;
import com.timebottle.backend.entity.User;
import com.timebottle.backend.repository.HourlyStreakRepository;
import com.timebottle.backend.repository.QuestionRepository;
import com.timebottle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@SuppressWarnings("null")
public class StreakService {

    @Autowired
    private HourlyStreakRepository hourlyStreakRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public Question getRandomQuestion() {
        List<Question> allQuestions = questionRepository.findAllByOrderByIdAsc();
        if (allQuestions.isEmpty()) return null;
        Collections.shuffle(allQuestions);
        return allQuestions.get(0);
    }

    public Map<String, Object> validateAnswer(@NonNull Integer questionId, String userAnswer) {
        Map<String, Object> result = new HashMap<>();
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            result.put("correct", false);
            result.put("correctOption", null);
            return result;
        }
        boolean correct = userAnswer != null && userAnswer.equalsIgnoreCase(question.getCorrectOption());
        result.put("correct", correct);
        result.put("correctOption", question.getCorrectOption());
        return result;
    }

    public void updateStreak(@NonNull Integer userId, int streakCount) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date hourTime = cal.getTime();

        hourlyStreakRepository.upsertStreak(userId, hourTime, streakCount);
    }

    public int getCurrentStreak(@NonNull Integer userId) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date hourTime = cal.getTime();

        Optional<HourlyStreak> streak = hourlyStreakRepository.findByUidAndHourTimeNative(userId, hourTime);
        return streak.map(HourlyStreak::getMaxStreak).orElse(0);
    }

    public List<Map<String, Object>> getCurrentHourRanking() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date hourTime = cal.getTime();

        List<HourlyStreak> topStreaks = hourlyStreakRepository.findTopByHourTime(hourTime, 10);

        List<Map<String, Object>> ranking = new ArrayList<>();
        int rank = 1;
        for (HourlyStreak streak : topStreaks) {
            if (streak.getMaxStreak() <= 0) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank);
            item.put("uid", streak.getUid());
            User user = userRepository.findById(streak.getUid()).orElse(null);
            item.put("username", user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "未知用户");
            item.put("avatar", user != null ? user.getAvatar() : null);
            item.put("maxStreak", streak.getMaxStreak());
            ranking.add(item);
            rank++;
        }
        return ranking;
    }

    public int[] getRewardPoints() {
        return new int[]{360, 270, 180, 90, 1};
    }

    @Transactional
    public void distributeRewards() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date lastHourTime = cal.getTime();

        List<HourlyStreak> topStreaks = hourlyStreakRepository.findTopByHourTime(lastHourTime, 5);
        int[] rewards = getRewardPoints();

        for (int i = 0; i < Math.min(topStreaks.size(), rewards.length); i++) {
            HourlyStreak streak = topStreaks.get(i);
            if (streak.getMaxStreak() <= 0) continue;
            User user = userRepository.findById(streak.getUid()).orElse(null);
            if (user != null) {
                user.setPoints(user.getPoints() + rewards[i]);
                userRepository.save(user);
            }
        }
    }
}
