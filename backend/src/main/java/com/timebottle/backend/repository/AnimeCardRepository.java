package com.timebottle.backend.repository;

import com.timebottle.backend.entity.AnimeCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    Page<AnimeCard> findAllByOrderByIdDesc(Pageable pageable);
    long count();
    
    List<AnimeCard> findByRarityLevel(Integer rarityLevel);
    List<AnimeCard> findBySeriesName(String seriesName);
    List<AnimeCard> findByNameContainingAndSeriesNameAndRarityLevel(String name, String seriesName, Integer rarityLevel);
    List<AnimeCard> findByNameContainingAndSeriesName(String name, String seriesName);
    List<AnimeCard> findByNameContainingAndRarityLevel(String name, Integer rarityLevel);
    List<AnimeCard> findBySeriesNameAndRarityLevel(String seriesName, Integer rarityLevel);
    
    Page<AnimeCard> findByNameContainingAndSeriesNameAndRarityLevel(String name, String seriesName, Integer rarityLevel, Pageable pageable);
    Page<AnimeCard> findByNameContainingAndSeriesName(String name, String seriesName, Pageable pageable);
    Page<AnimeCard> findByNameContainingAndRarityLevel(String name, Integer rarityLevel, Pageable pageable);
    Page<AnimeCard> findBySeriesNameAndRarityLevel(String seriesName, Integer rarityLevel, Pageable pageable);
    Page<AnimeCard> findByNameContaining(String name, Pageable pageable);
    Page<AnimeCard> findBySeriesName(String seriesName, Pageable pageable);
    Page<AnimeCard> findByRarityLevel(Integer rarityLevel, Pageable pageable);
}
