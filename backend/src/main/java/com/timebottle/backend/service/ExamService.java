package com.timebottle.backend.service;

import com.timebottle.backend.entity.Question;
import com.timebottle.backend.entity.User;
import com.timebottle.backend.repository.QuestionRepository;
import com.timebottle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@SuppressWarnings("null")
public class ExamService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

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

    public Map<String, Object> validateAndSubmitExamWithDetails(Integer userId, List<Map<String, Object>> userAnswers) {
        Map<String, Object> result = new HashMap<>();

        int correctCount = 0;
        List<Map<String, Object>> details = new ArrayList<>();

        for (Map<String, Object> answer : userAnswers) {
            Integer questionId = (Integer) answer.get("questionId");
            String userAnswer = (String) answer.get("answer");

            if (questionId != null && userAnswer != null) {
                Question question = questionRepository.findById(questionId).orElse(null);
                if (question != null) {
                    boolean isCorrect = userAnswer.equalsIgnoreCase(question.getCorrectOption());
                    if (isCorrect) {
                        correctCount++;
                    }

                    Map<String, Object> detail = new HashMap<>();
                    detail.put("questionId", questionId);
                    detail.put("questionText", question.getQuestionText());
                    detail.put("optionA", question.getOptionA());
                    detail.put("optionB", question.getOptionB());
                    detail.put("optionC", question.getOptionC());
                    detail.put("optionD", question.getOptionD());
                    detail.put("userAnswer", userAnswer);
                    detail.put("correctOption", question.getCorrectOption());
                    detail.put("isCorrect", isCorrect);

                    details.add(detail);
                }
            }
        }

        int pointsEarned = calculatePoints(correctCount);
        if (pointsEarned > 0) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setPoints(user.getPoints() + pointsEarned);
                userRepository.save(user);
            }
        }

        result.put("correctCount", correctCount);
        result.put("details", details);
        return result;
    }
}
