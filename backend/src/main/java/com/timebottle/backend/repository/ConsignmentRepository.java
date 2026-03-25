package com.timebottle.backend.repository;

import com.timebottle.backend.entity.Consignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsignmentRepository extends JpaRepository<Consignment, Long> {
    
    List<Consignment> findBySellerId(@NonNull Integer sellerId);
    
    Page<Consignment> findBySellerId(@NonNull Integer sellerId, @NonNull Pageable pageable);
    
    List<Consignment> findByCardId(@NonNull Long cardId);
    
    @Query("SELECT c FROM Consignment c WHERE c.quantity > 0 ORDER BY c.createdAt DESC")
    Page<Consignment> findAvailable(@NonNull Pageable pageable);
    
    @Query("SELECT c FROM Consignment c WHERE c.cardId = :cardId AND c.quantity > 0 ORDER BY c.unitPrice ASC")
    List<Consignment> findByCardIdOrderByPrice(@Param("cardId") @NonNull Long cardId);
    
    @Query("SELECT c FROM Consignment c WHERE c.sellerId = :sellerId AND c.quantity > 0")
    List<Consignment> findActiveBySellerId(@Param("sellerId") @NonNull Integer sellerId);
    
    @Query("SELECT c FROM Consignment c JOIN AnimeCard a ON c.cardId = a.id " +
           "WHERE c.quantity > 0 AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.seriesName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.createdAt DESC")
    Page<Consignment> searchByKeyword(@Param("keyword") String keyword, @NonNull Pageable pageable);
    
    @Query("SELECT c FROM Consignment c WHERE c.quantity > 0 AND c.unitPrice BETWEEN :minPrice AND :maxPrice ORDER BY c.unitPrice ASC")
    Page<Consignment> findByPriceRange(@Param("minPrice") @NonNull Integer minPrice, @Param("maxPrice") @NonNull Integer maxPrice, @NonNull Pageable pageable);
    
    void deleteByQuantity(@NonNull Integer quantity);
}
