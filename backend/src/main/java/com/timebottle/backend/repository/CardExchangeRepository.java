package com.timebottle.backend.repository;

import com.timebottle.backend.entity.CardExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardExchangeRepository extends JpaRepository<CardExchange, Long> {
    List<CardExchange> findBySenderIdOrderByCreatedAtDesc(Integer senderId);
    List<CardExchange> findByReceiverIdOrderByCreatedAtDesc(Integer receiverId);
    List<CardExchange> findByReceiverIdAndStatusOrderByCreatedAtDesc(Integer receiverId, String status);
    List<CardExchange> findBySenderIdAndStatusOrderByCreatedAtDesc(Integer senderId, String status);
}
