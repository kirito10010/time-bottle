package com.timebottle.backend.controller;

import com.timebottle.backend.entity.Question;
import com.timebottle.backend.service.StreakService;
import com.timebottle.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/streak")
@CrossOrigin(origins = "*")
public class StreakController {

    @Autowired
    private StreakService streakService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/question")
    public ResponseEntity<?> getRandomQuestion(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }

        Question question = streakService.getRandomQuestion();
        if (question == null) {
            return ResponseEntity.ok(Map.of("message", "题库为空"));
        }

        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("id", question.getId());
        questionMap.put("questionText", question.getQuestionText());
        questionMap.put("optionA", question.getOptionA());
        questionMap.put("optionB", question.getOptionB());
        questionMap.put("optionC", question.getOptionC());
        questionMap.put("optionD", question.getOptionD());

        return ResponseEntity.ok(Map.of("question", questionMap));
    }

    @PostMapping("/answer")
    public ResponseEntity<?> submitAnswer(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> requestBody) {

        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }

        Integer questionId = (Integer) requestBody.get("questionId");
        String userAnswer = (String) requestBody.get("answer");
        Integer currentStreak = (Integer) requestBody.get("currentStreak");

        if (questionId == null || userAnswer == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "参数不完整"));
        }

        Map<String, Object> result = streakService.validateAnswer(questionId, userAnswer);
        boolean correct = (boolean) result.get("correct");

        int newStreak;
        if (correct) {
            newStreak = (currentStreak != null ? currentStreak : 0) + 1;
            streakService.updateStreak(userId, newStreak);
        } else {
            newStreak = 0;
            if (currentStreak != null && currentStreak > 0) {
                streakService.updateStreak(userId, currentStreak);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("correct", correct);
        response.put("correctOption", result.get("correctOption"));
        response.put("currentStreak", newStreak);
        response.put("ended", !correct);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-streak")
    public ResponseEntity<?> getMyStreak(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }

        int currentStreak = streakService.getCurrentStreak(userId);
        return ResponseEntity.ok(Map.of("maxStreak", currentStreak));
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }

        List<Map<String, Object>> ranking = streakService.getCurrentHourRanking();
        int myStreak = streakService.getCurrentStreak(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("ranking", ranking);
        response.put("myMaxStreak", myStreak);
        response.put("rewards", streakService.getRewardPoints());

        Calendar cal = Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int nextHour = (currentHour + 1) % 24;
        response.put("currentHour", currentHour);
        response.put("nextHour", nextHour);

        return ResponseEntity.ok(response);
    }

    private Integer extractUserId(String token) {
        if (token == null || token.isEmpty()) return null;
        String cleanToken = token.replace("Bearer ", "");
        return jwtUtil.extractUserId(cleanToken);
    }
}
