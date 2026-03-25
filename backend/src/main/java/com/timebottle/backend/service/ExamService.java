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
}
