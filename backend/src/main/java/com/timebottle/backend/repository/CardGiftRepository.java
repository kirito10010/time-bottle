package com.timebottle.backend.repository;

import com.timebottle.backend.entity.CardGift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardGiftRepository extends JpaRepository<CardGift, Long> {
    List<CardGift> findBySenderIdOrderByCreatedAtDesc(Integer senderId);
    List<CardGift> findByReceiverIdOrderByCreatedAtDesc(Integer receiverId);
    List<CardGift> findByReceiverIdAndStatusOrderByCreatedAtDesc(Integer receiverId, String status);
    List<CardGift> findBySenderIdAndStatusOrderByCreatedAtDesc(Integer senderId, String status);
}
