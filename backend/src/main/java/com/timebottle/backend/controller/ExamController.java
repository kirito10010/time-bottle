package com.timebottle.backend.controller;

import com.timebottle.backend.entity.Question;
import com.timebottle.backend.service.ExamService;
import com.timebottle.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        if (!examService.canTakeExam(userId)) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "今日考试次数已用完，请明天再来");
            return ResponseEntity.status(429).body(error);
        }

        List<Question> questions = examService.getRandomQuestions(5);
        
        List<Map<String, Object>> questionList = questions.stream().map(q -> {
            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("id", q.getId());
            questionMap.put("questionText", q.getQuestionText());
            questionMap.put("optionA", q.getOptionA());
            questionMap.put("optionB", q.getOptionB());
            questionMap.put("optionC", q.getOptionC());
            questionMap.put("optionD", q.getOptionD());
            return questionMap;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("questions", questionList);
        response.put("remainingExams", examService.getRemainingExamsToday(userId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitExam(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> requestBody) {
        
        Integer userId = extractUserId(token);
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请先登录");
            return ResponseEntity.status(401).body(error);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> userAnswers = (List<Map<String, Object>>) requestBody.get("answers");
        
        if (userAnswers == null || userAnswers.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请提供答题结果");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> result = examService.validateAndSubmitExamWithDetails(userId, userAnswers);
        
        int correctCount = (int) result.get("correctCount");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("correctCount", correctCount);
        response.put("pointsEarned", examService.calculatePoints(correctCount));
        response.put("message", correctCount >= 3 
            ? "恭喜！答对 " + correctCount + " 题，获得 " + examService.calculatePoints(correctCount) + " 积分" 
            : "很遗憾，答对 " + correctCount + " 题，未获得积分");
        response.put("details", result.get("details"));
        
        return ResponseEntity.ok(response);
    }

    private Integer extractUserId(String token) {
        if (token == null || token.isEmpty()) return null;
        String cleanToken = token.replace("Bearer ", "");
        return jwtUtil.extractUserId(cleanToken);
    }
}
