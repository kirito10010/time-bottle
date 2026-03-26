package com.timebottle.backend.service;

import com.timebottle.backend.entity.*;
import com.timebottle.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@SuppressWarnings("null")
public class CardExchangeService {

    @Autowired
    private CardGiftRepository cardGiftRepository;

    @Autowired
    private CardExchangeRepository cardExchangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCardRepository userCardRepository;

    @Autowired
    private AnimeCardRepository animeCardRepository;

    public List<User> searchUsers(String keyword, Integer excludeUserId) {
        return userRepository.searchByKeyword(keyword, excludeUserId);
    }

    public List<Map<String, Object>> findUsersWithCard(Long cardId, Integer minQuantity, Integer excludeUserId) {
        List<UserCard> userCards = userCardRepository.findByCardIdAndQuantityGreaterThan(cardId, 0);
        
        return userCards.stream()
            .filter(uc -> !uc.getUid().equals(excludeUserId.longValue()))
            .filter(uc -> uc.getQuantity() >= minQuantity)
            .map(uc -> {
                Map<String, Object> map = new HashMap<>();
                map.put("uid", uc.getUid());
                map.put("quantity", uc.getQuantity());
                
                User user = userRepository.findById(uc.getUid().intValue()).orElse(null);
                if (user != null) {
                    map.put("id", user.getId());
                    map.put("username", user.getUsername());
                    map.put("nickname", user.getNickname());
                    map.put("avatar", user.getAvatar());
                }
                return map;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getMyCards(Integer userId) {
        List<UserCard> userCards = userCardRepository.findByUid(userId.longValue());
        return userCards.stream().map(uc -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userCardId", uc.getId());
            map.put("cardId", uc.getCardId());
            map.put("quantity", uc.getQuantity());
            AnimeCard card = animeCardRepository.findById(uc.getCardId()).orElse(null);
            if (card != null) {
                map.put("cardName", card.getName());
                map.put("cardImageUrl", card.getImageUrl());
                map.put("cardType", card.getType());
                map.put("rarityLevel", card.getRarityLevel());
            }
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> searchCards(String keyword) {
        List<AnimeCard> cards;
        if (keyword != null && !keyword.trim().isEmpty()) {
            cards = animeCardRepository.findByNameContaining(keyword);
        } else {
            cards = animeCardRepository.findAll();
        }
        
        return cards.stream().map(card -> {
            Map<String, Object> map = new HashMap<>();
            map.put("cardId", card.getId());
            map.put("cardName", card.getName());
            map.put("cardImageUrl", card.getImageUrl());
            map.put("cardType", card.getType());
            map.put("rarityLevel", card.getRarityLevel());
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public CardGift sendGift(Integer senderId, Integer receiverId, Long cardId, Integer quantity) {
        UserCard senderCard = userCardRepository.findByUidAndCardId(senderId.longValue(), cardId).orElse(null);
        if (senderCard == null || senderCard.getQuantity() < quantity) {
            throw new RuntimeException("卡片数量不足");
        }

        senderCard.setQuantity(senderCard.getQuantity() - quantity);
        if (senderCard.getQuantity() == 0) {
            userCardRepository.delete(senderCard);
        } else {
            userCardRepository.save(senderCard);
        }

        UserCard receiverCard = userCardRepository.findByUidAndCardId(receiverId.longValue(), cardId).orElse(null);
        if (receiverCard == null) {
            receiverCard = new UserCard();
            receiverCard.setUid(receiverId.longValue());
            receiverCard.setCardId(cardId);
            receiverCard.setQuantity(quantity);
            receiverCard.setObtainedAt(LocalDateTime.now());
        } else {
            receiverCard.setQuantity(receiverCard.getQuantity() + quantity);
        }
        userCardRepository.save(receiverCard);

        CardGift gift = new CardGift();
        gift.setSenderId(senderId);
        gift.setReceiverId(receiverId);
        gift.setCardId(cardId);
        gift.setQuantity(quantity);
        gift.setStatus("completed");
        gift.setCreatedAt(new Date());
        gift.setUpdatedAt(new Date());
        
        return cardGiftRepository.save(gift);
    }

    @Transactional
    public CardExchange sendExchangeRequest(Integer senderId, Integer receiverId, 
            Long senderCardId, Integer senderQty, Long receiverCardId, Integer receiverQty) {
        
        UserCard senderCard = userCardRepository.findByUidAndCardId(senderId.longValue(), senderCardId).orElse(null);
        if (senderCard == null || senderCard.getQuantity() < senderQty) {
            throw new RuntimeException("您的卡片数量不足");
        }

        UserCard receiverCard = userCardRepository.findByUidAndCardId(receiverId.longValue(), receiverCardId).orElse(null);
        if (receiverCard == null || receiverCard.getQuantity() < receiverQty) {
            throw new RuntimeException("对方卡片数量不足");
        }

        CardExchange exchange = new CardExchange();
        exchange.setSenderId(senderId);
        exchange.setReceiverId(receiverId);
        exchange.setSenderCardId(senderCardId);
        exchange.setSenderCardQuantity(senderQty);
        exchange.setReceiverCardId(receiverCardId);
        exchange.setReceiverCardQuantity(receiverQty);
        exchange.setStatus("pending");
        exchange.setCreatedAt(new Date());
        exchange.setUpdatedAt(new Date());
        
        return cardExchangeRepository.save(exchange);
    }

    @Transactional
    public void acceptExchange(Long exchangeId, Integer userId) {
        CardExchange exchange = cardExchangeRepository.findById(exchangeId)
            .orElseThrow(() -> new RuntimeException("交换申请不存在"));
        
        if (!exchange.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        
        if (!"pending".equals(exchange.getStatus())) {
            throw new RuntimeException("该申请已处理");
        }

        UserCard senderCard = userCardRepository.findByUidAndCardId(exchange.getSenderId().longValue(), exchange.getSenderCardId()).orElse(null);
        UserCard receiverCard = userCardRepository.findByUidAndCardId(exchange.getReceiverId().longValue(), exchange.getReceiverCardId()).orElse(null);
        
        if (senderCard == null || senderCard.getQuantity() < exchange.getSenderCardQuantity() ||
            receiverCard == null || receiverCard.getQuantity() < exchange.getReceiverCardQuantity()) {
            throw new RuntimeException("卡片数量不足，无法完成交换");
        }

        senderCard.setQuantity(senderCard.getQuantity() - exchange.getSenderCardQuantity());
        if (senderCard.getQuantity() == 0) userCardRepository.delete(senderCard);
        else userCardRepository.save(senderCard);

        receiverCard.setQuantity(receiverCard.getQuantity() - exchange.getReceiverCardQuantity());
        if (receiverCard.getQuantity() == 0) userCardRepository.delete(receiverCard);
        else userCardRepository.save(receiverCard);

        UserCard senderGetsCard = userCardRepository.findByUidAndCardId(exchange.getSenderId().longValue(), exchange.getReceiverCardId()).orElse(null);
        if (senderGetsCard == null) {
            senderGetsCard = new UserCard();
            senderGetsCard.setUid(exchange.getSenderId().longValue());
            senderGetsCard.setCardId(exchange.getReceiverCardId());
            senderGetsCard.setQuantity(exchange.getReceiverCardQuantity());
            senderGetsCard.setObtainedAt(LocalDateTime.now());
        } else {
            senderGetsCard.setQuantity(senderGetsCard.getQuantity() + exchange.getReceiverCardQuantity());
        }
        userCardRepository.save(senderGetsCard);

        UserCard receiverGetsCard = userCardRepository.findByUidAndCardId(exchange.getReceiverId().longValue(), exchange.getSenderCardId()).orElse(null);
        if (receiverGetsCard == null) {
            receiverGetsCard = new UserCard();
            receiverGetsCard.setUid(exchange.getReceiverId().longValue());
            receiverGetsCard.setCardId(exchange.getSenderCardId());
            receiverGetsCard.setQuantity(exchange.getSenderCardQuantity());
            receiverGetsCard.setObtainedAt(LocalDateTime.now());
        } else {
            receiverGetsCard.setQuantity(receiverGetsCard.getQuantity() + exchange.getSenderCardQuantity());
        }
        userCardRepository.save(receiverGetsCard);

        exchange.setStatus("completed");
        exchange.setUpdatedAt(new Date());
        cardExchangeRepository.save(exchange);
    }

    @Transactional
    public void rejectExchange(Long exchangeId, Integer userId) {
        CardExchange exchange = cardExchangeRepository.findById(exchangeId)
            .orElseThrow(() -> new RuntimeException("交换申请不存在"));
        
        if (!exchange.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        
        exchange.setStatus("rejected");
        exchange.setUpdatedAt(new Date());
        cardExchangeRepository.save(exchange);
    }

    public List<CardGift> getReceivedGifts(Integer userId) {
        return cardGiftRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    public List<CardExchange> getPendingExchanges(Integer userId) {
        return cardExchangeRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(userId, "pending");
    }

    public List<CardExchange> getMyExchangeRequests(Integer userId) {
        return cardExchangeRepository.findBySenderIdOrderByCreatedAtDesc(userId);
    }

    public void fillGiftDetails(CardGift gift) {
        User sender = userRepository.findById(gift.getSenderId()).orElse(null);
        User receiver = userRepository.findById(gift.getReceiverId()).orElse(null);
        AnimeCard card = animeCardRepository.findById(gift.getCardId()).orElse(null);
        
        if (sender != null) {
            gift.setSenderName(sender.getNickname() != null ? sender.getNickname() : sender.getUsername());
            gift.setSenderAvatar(sender.getAvatar());
        }
        if (receiver != null) {
            gift.setReceiverName(receiver.getNickname() != null ? receiver.getNickname() : receiver.getUsername());
            gift.setReceiverAvatar(receiver.getAvatar());
        }
        if (card != null) {
            gift.setCardName(card.getName());
            gift.setCardImageUrl(card.getImageUrl());
        }
    }

    public void fillExchangeDetails(CardExchange exchange) {
        User sender = userRepository.findById(exchange.getSenderId()).orElse(null);
        User receiver = userRepository.findById(exchange.getReceiverId()).orElse(null);
        AnimeCard senderCard = animeCardRepository.findById(exchange.getSenderCardId()).orElse(null);
        AnimeCard receiverCard = animeCardRepository.findById(exchange.getReceiverCardId()).orElse(null);
        
        if (sender != null) {
            exchange.setSenderName(sender.getNickname() != null ? sender.getNickname() : sender.getUsername());
            exchange.setSenderAvatar(sender.getAvatar());
        }
        if (receiver != null) {
            exchange.setReceiverName(receiver.getNickname() != null ? receiver.getNickname() : receiver.getUsername());
            exchange.setReceiverAvatar(receiver.getAvatar());
        }
        if (senderCard != null) {
            exchange.setSenderCardName(senderCard.getName());
            exchange.setSenderCardImageUrl(senderCard.getImageUrl());
        }
        if (receiverCard != null) {
            exchange.setReceiverCardName(receiverCard.getName());
            exchange.setReceiverCardImageUrl(receiverCard.getImageUrl());
        }
    }
}
