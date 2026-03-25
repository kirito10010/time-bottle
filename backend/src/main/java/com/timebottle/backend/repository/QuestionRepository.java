package com.timebottle.backend.repository;

import com.timebottle.backend.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    
    @NonNull
    Page<Question> findAll(@NonNull Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE " +
           "LOWER(q.questionText) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(q.optionA) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(q.optionB) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(q.optionC) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(q.optionD) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Question> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    List<Question> findAllByOrderByIdAsc();
}
