package com.timebottle.backend.service;

import com.timebottle.backend.entity.Question;
import com.timebottle.backend.entity.User;
import com.timebottle.backend.repository.QuestionRepository;
import com.timebottle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@SuppressWarnings("null")
public class ExamService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int MAX_EXAM_PER_DAY = 3;
    private Map<Integer, Map<LocalDate, Integer>> examCountMap = new HashMap<>();

    public List<Question> getRandomQuestions(int count) {
        List<Question> allQuestions = questionRepository.findAllByOrderByIdAsc();
        Collections.shuffle(allQuestions);
        
        if (allQuestions.size() <= count) {
            return allQuestions;
        }
        
        return allQuestions.subList(0, count);
    }

    public int calculatePoints(int correctCount) {
        if (correctCount == 3) return 10;
        if (correctCount == 4) return 30;
        if (correctCount == 5) return 60;
        return 0;
    }

    public int submitExam(Integer userId, int correctCount) {
        int pointsEarned = calculatePoints(correctCount);
        
        if (pointsEarned > 0) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setPoints(user.getPoints() + pointsEarned);
                userRepository.save(user);
            }
        }
        
        return pointsEarned;
    }

    public int validateAndSubmitExam(Integer userId, List<Map<String, Object>> userAnswers) {
        if (!canTakeExam(userId)) {
            return -1;
        }

        int correctCount = 0;
        
        for (Map<String, Object> answer : userAnswers) {
            Integer questionId = (Integer) answer.get("questionId");
            String userAnswer = (String) answer.get("answer");
            
            if (questionId != null && userAnswer != null) {
                Question question = questionRepository.findById(questionId).orElse(null);
                if (question != null && userAnswer.equalsIgnoreCase(question.getCorrectOption())) {
                    correctCount++;
                }
            }
        }

        incrementExamCount(userId);

        int pointsEarned = calculatePoints(correctCount);
        if (pointsEarned > 0) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setPoints(user.getPoints() + pointsEarned);
                userRepository.save(user);
            }
        }
        
        return correctCount;
    }

    public boolean canTakeExam(Integer userId) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> userExamCount = examCountMap.computeIfAbsent(userId, k -> new HashMap<>());
        int count = userExamCount.getOrDefault(today, 0);
        return count < MAX_EXAM_PER_DAY;
    }

    public int getRemainingExamsToday(Integer userId) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> userExamCount = examCountMap.computeIfAbsent(userId, k -> new HashMap<>());
        int count = userExamCount.getOrDefault(today, 0);
        return Math.max(0, MAX_EXAM_PER_DAY - count);
    }

    private void incrementExamCount(Integer userId) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> userExamCount = examCountMap.computeIfAbsent(userId, k -> new HashMap<>());
        userExamCount.put(today, userExamCount.getOrDefault(today, 0) + 1);
    }
}
