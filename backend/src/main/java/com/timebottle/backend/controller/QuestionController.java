package com.timebottle.backend.controller;

import com.timebottle.backend.entity.Question;
import com.timebottle.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> response = questionService.getQuestions(page, size, keyword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Integer id) {
        Question question = questionService.getQuestionById(id);
        if (question == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "题目不存在");
            return ResponseEntity.status(404).body(error);
        }
        return ResponseEntity.ok(question);
    }

    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody Question question) {
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "题目内容不能为空");
            return ResponseEntity.badRequest().body(error);
        }
        if (question.getOptionA() == null || question.getOptionA().trim().isEmpty() ||
            question.getOptionB() == null || question.getOptionB().trim().isEmpty() ||
            question.getOptionC() == null || question.getOptionC().trim().isEmpty() ||
            question.getOptionD() == null || question.getOptionD().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "所有选项不能为空");
            return ResponseEntity.badRequest().body(error);
        }
        if (question.getCorrectOption() == null || 
            !List.of("A", "B", "C", "D").contains(question.getCorrectOption().toUpperCase())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "正确答案必须是 A、B、C 或 D");
            return ResponseEntity.badRequest().body(error);
        }
        
        question.setCorrectOption(question.getCorrectOption().toUpperCase());
        Question savedQuestion = questionService.createQuestion(question);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "添加成功");
        response.put("question", savedQuestion);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Integer id, @RequestBody Question questionData) {
        Question updatedQuestion = questionService.updateQuestion(id, questionData);
        if (updatedQuestion == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "题目不存在");
            return ResponseEntity.status(404).body(error);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "更新成功");
        response.put("question", updatedQuestion);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Integer id) {
        boolean deleted = questionService.deleteQuestion(id);
        if (!deleted) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "题目不存在");
            return ResponseEntity.status(404).body(error);
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteQuestions(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "请选择要删除的题目");
            return ResponseEntity.badRequest().body(error);
        }
        
        int count = questionService.batchDeleteQuestions(ids);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "成功删除 " + count + " 个题目");
        response.put("deletedCount", count);
        return ResponseEntity.ok(response);
    }
}
