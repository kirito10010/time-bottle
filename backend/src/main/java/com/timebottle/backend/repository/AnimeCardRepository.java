package com.timebottle.backend.repository;

import com.timebottle.backend.entity.AnimeCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimeCardRepository extends JpaRepository<AnimeCard, Long> {
    List<AnimeCard> findByType(String type);
    List<AnimeCard> findByNameContaining(String name);
    boolean existsByName(String name);
    
    @Query("SELECT DISTINCT a.seriesName FROM AnimeCard a ORDER BY a.seriesName")
    List<String> findAllDistinctSeriesNames();
}
