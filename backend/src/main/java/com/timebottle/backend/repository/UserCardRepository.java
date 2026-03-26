package com.timebottle.backend.repository;

import com.timebottle.backend.entity.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    List<UserCard> findByUid(Long uid);
    Optional<UserCard> findByUidAndCardId(Long uid, Long cardId);
    boolean existsByUidAndCardId(Long uid, Long cardId);
    long countByUid(Long uid);
    List<UserCard> findByCardIdAndQuantityGreaterThan(Long cardId, Integer quantity);
}
