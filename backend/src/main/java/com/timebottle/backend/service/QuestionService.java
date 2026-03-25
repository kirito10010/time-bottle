package com.timebottle.backend.service;

import com.timebottle.backend.entity.Question;
import com.timebottle.backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@SuppressWarnings("null")
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Map<String, Object> getQuestions(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Question> questionPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            questionPage = questionRepository.searchByKeyword(keyword.trim(), pageable);
        } else {
            questionPage = questionRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("questions", questionPage.getContent());
        response.put("currentPage", questionPage.getNumber());
        response.put("totalPages", questionPage.getTotalPages());
        response.put("totalElements", questionPage.getTotalElements());
        
        return response;
    }

    public Question getQuestionById(Integer id) {
        return questionRepository.findById(id).orElse(null);
    }

    public Question createQuestion(Question question) {
        question.setCreatedAt(new Date());
        return questionRepository.save(question);
    }

    public Question updateQuestion(Integer id, Question questionData) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return null;
        }
        
        if (questionData.getQuestionText() != null) {
            question.setQuestionText(questionData.getQuestionText());
        }
        if (questionData.getOptionA() != null) {
            question.setOptionA(questionData.getOptionA());
        }
        if (questionData.getOptionB() != null) {
            question.setOptionB(questionData.getOptionB());
        }
        if (questionData.getOptionC() != null) {
            question.setOptionC(questionData.getOptionC());
        }
        if (questionData.getOptionD() != null) {
            question.setOptionD(questionData.getOptionD());
        }
        if (questionData.getCorrectOption() != null) {
            question.setCorrectOption(questionData.getCorrectOption());
        }
        
        return questionRepository.save(question);
    }

    public boolean deleteQuestion(Integer id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public int batchDeleteQuestions(List<Integer> ids) {
        int count = 0;
        for (Integer id : ids) {
            if (questionRepository.existsById(id)) {
                questionRepository.deleteById(id);
                count++;
            }
        }
        return count;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAllByOrderByIdAsc();
    }
}
