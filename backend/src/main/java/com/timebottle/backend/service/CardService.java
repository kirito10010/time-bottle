package com.timebottle.backend.service;

import com.timebottle.backend.entity.AnimeCard;
import com.timebottle.backend.entity.PointsLog;
import com.timebottle.backend.entity.User;
import com.timebottle.backend.entity.UserCard;
import com.timebottle.backend.repository.AnimeCardRepository;
import com.timebottle.backend.repository.PointsLogRepository;
import com.timebottle.backend.repository.UserCardRepository;
import com.timebottle.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@SuppressWarnings("null")
public class CardService {

    @Autowired
    private AnimeCardRepository animeCardRepository;

    @Autowired
    private UserCardRepository userCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsLogRepository pointsLogRepository;

    private static final int MAX_DRAW_COUNT = 10;
    private static final int SINGLE_DRAW_COST = 10;
    private static final int TEN_DRAW_COST = 90;
    
    private final ConcurrentHashMap<Long, ReentrantLock> userDrawLocks = new ConcurrentHashMap<>();

    private static final Map<Integer, Double> RARITY_PROBABILITIES = new TreeMap<>();
    static {
        RARITY_PROBABILITIES.put(1, 0.70);
        RARITY_PROBABILITIES.put(2, 0.20);
        RARITY_PROBABILITIES.put(3, 0.07);
        RARITY_PROBABILITIES.put(4, 0.02);
        RARITY_PROBABILITIES.put(5, 0.01);
    }

    public List<AnimeCard> getAllCards() {
        return animeCardRepository.findAll();
    }

    public Page<AnimeCard> getCardsPage(int page, int size) {
        return animeCardRepository.findAllByOrderByIdDesc(PageRequest.of(page, size));
    }

    public long getTotalCardCount() {
        return animeCardRepository.count();
    }

    public List<String> getAllSeriesNames() {
        return animeCardRepository.findAllDistinctSeriesNames();
    }

    public Optional<AnimeCard> getCardById(@NonNull Long id) {
        return animeCardRepository.findById(id);
    }

    public List<AnimeCard> getCardsByType(@NonNull String type) {
        return animeCardRepository.findByType(type);
    }

    @Transactional
    public AnimeCard createCard(@NonNull String seriesName, @NonNull String name, @NonNull String type, 
                                 @NonNull Integer rarityLevel, @NonNull String imageUrl) {
        AnimeCard card = new AnimeCard();
        card.setSeriesName(seriesName);
        card.setName(name);
        card.setType(type);
        card.setRarityLevel(rarityLevel);
        card.setImageUrl(imageUrl);
        return animeCardRepository.save(card);
    }

    @Transactional
    public AnimeCard updateCard(@NonNull Long id, String seriesName, String name, String type, 
                                 Integer rarityLevel, String imageUrl) {
        Optional<AnimeCard> cardOpt = animeCardRepository.findById(id);
        if (cardOpt.isEmpty()) {
            throw new RuntimeException("卡片不存在");
        }
        AnimeCard card = cardOpt.get();
        if (seriesName != null && !seriesName.isEmpty()) card.setSeriesName(seriesName);
        if (name != null && !name.isEmpty()) card.setName(name);
        if (type != null && !type.isEmpty()) card.setType(type);
        if (rarityLevel != null) card.setRarityLevel(rarityLevel);
        if (imageUrl != null && !imageUrl.isEmpty()) card.setImageUrl(imageUrl);
        return animeCardRepository.save(card);
    }

    @Transactional
    public void deleteCard(@NonNull Long id) {
        if (!animeCardRepository.existsById(id)) {
            throw new RuntimeException("卡片不存在");
        }
        animeCardRepository.deleteById(id);
    }

    public List<UserCard> getUserCards(@NonNull Long uid) {
        return userCardRepository.findByUid(uid);
    }

    public List<AnimeCard> getUserAnimeCards(@NonNull Long uid) {
        List<UserCard> userCards = userCardRepository.findByUid(uid);
        List<AnimeCard> animeCards = new ArrayList<>();
        for (UserCard uc : userCards) {
            animeCardRepository.findById(uc.getCardId()).ifPresent(animeCards::add);
        }
        return animeCards;
    }

    public long getUserCardCount(@NonNull Long uid) {
        return userCardRepository.countByUid(uid);
    }

    @Transactional
    public UserCard giveCardToUser(@NonNull Long uid, @NonNull Long cardId) {
        if (userCardRepository.existsByUidAndCardId(uid, cardId)) {
            throw new RuntimeException("用户已拥有该卡片");
        }
        UserCard userCard = new UserCard();
        userCard.setUid(uid);
        userCard.setCardId(cardId);
        return userCardRepository.save(userCard);
    }

    @Transactional
    public void removeCardFromUser(@NonNull Long uid, @NonNull Long cardId) {
        Optional<UserCard> userCardOpt = userCardRepository.findByUidAndCardId(uid, cardId);
        if (userCardOpt.isPresent()) {
            userCardRepository.delete(userCardOpt.get());
        }
    }

    public boolean userHasCard(@NonNull Long uid, @NonNull Long cardId) {
        return userCardRepository.existsByUidAndCardId(uid, cardId);
    }

    private AnimeCard drawCardByRarity(List<AnimeCard> allCards, Random random) {
        double roll = random.nextDouble();
        double cumulative = 0.0;
        int selectedRarity = 1;
        
        for (Map.Entry<Integer, Double> entry : RARITY_PROBABILITIES.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                selectedRarity = entry.getKey();
                break;
            }
        }
        
        List<AnimeCard> cardsOfRarity = new ArrayList<>();
        for (AnimeCard card : allCards) {
            if (card.getRarityLevel() != null && card.getRarityLevel().equals(selectedRarity)) {
                cardsOfRarity.add(card);
            }
        }
        
        if (cardsOfRarity.isEmpty()) {
            return allCards.get(random.nextInt(allCards.size()));
        }
        
        return cardsOfRarity.get(random.nextInt(cardsOfRarity.size()));
    }

    @Transactional
    public AnimeCard drawRandomCard(@NonNull Long uid) {
        ReentrantLock userLock = userDrawLocks.computeIfAbsent(uid, k -> new ReentrantLock());
        
        if (!userLock.tryLock()) {
            throw new RuntimeException("正在处理中，请勿重复操作");
        }
        
        try {
            List<AnimeCard> allCards = animeCardRepository.findAll();
            if (allCards.isEmpty()) {
                throw new RuntimeException("卡片池为空，无法抽卡");
            }
            
            User user = userRepository.findById(uid.intValue())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            if (user.getPoints() < SINGLE_DRAW_COST) {
                throw new RuntimeException("积分不足，单抽需要" + SINGLE_DRAW_COST + "积分，当前积分：" + user.getPoints());
            }
            
            user.setPoints(user.getPoints() - SINGLE_DRAW_COST);
            userRepository.save(user);
            
            PointsLog log = new PointsLog();
            log.setUserId(uid.intValue());
            log.setChange(-SINGLE_DRAW_COST);
            log.setType("draw");
            log.setRemark("单抽消耗");
            pointsLogRepository.save(log);
            
            Random random = new Random();
            AnimeCard drawnCard = drawCardByRarity(allCards, random);
            
            Optional<UserCard> existingCard = userCardRepository.findByUidAndCardId(uid, drawnCard.getId());
            if (existingCard.isPresent()) {
                UserCard userCard = existingCard.get();
                userCard.setQuantity(userCard.getQuantity() + 1);
                userCardRepository.save(userCard);
            } else {
                UserCard userCard = new UserCard();
                userCard.setUid(uid);
                userCard.setCardId(drawnCard.getId());
                userCard.setQuantity(1);
                userCardRepository.save(userCard);
            }
            
            return drawnCard;
        } finally {
            userLock.unlock();
            userDrawLocks.remove(uid);
        }
    }

    @Transactional
    public List<AnimeCard> drawMultipleCards(@NonNull Long uid, int count) {
        if (count <= 0) {
            throw new RuntimeException("抽卡数量必须大于0");
        }
        if (count > MAX_DRAW_COUNT) {
            throw new RuntimeException("单次最多抽" + MAX_DRAW_COUNT + "张卡");
        }
        
        ReentrantLock userLock = userDrawLocks.computeIfAbsent(uid, k -> new ReentrantLock());
        
        if (!userLock.tryLock()) {
            throw new RuntimeException("正在处理中，请勿重复操作");
        }
        
        try {
            List<AnimeCard> allCards = animeCardRepository.findAll();
            if (allCards.isEmpty()) {
                throw new RuntimeException("卡片池为空，无法抽卡");
            }
            
            int cost = count == 10 ? TEN_DRAW_COST : count * SINGLE_DRAW_COST;
            
            User user = userRepository.findById(uid.intValue())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            if (user.getPoints() < cost) {
                throw new RuntimeException("积分不足，需要" + cost + "积分，当前积分：" + user.getPoints());
            }
            
            user.setPoints(user.getPoints() - cost);
            userRepository.save(user);
            
            PointsLog log = new PointsLog();
            log.setUserId(uid.intValue());
            log.setChange(-cost);
            log.setType("draw");
            log.setRemark(count + "连抽消耗");
            pointsLogRepository.save(log);
            
            List<AnimeCard> drawnCards = new java.util.ArrayList<>();
            Random random = new Random();
            
            for (int i = 0; i < count; i++) {
                AnimeCard drawnCard = drawCardByRarity(allCards, random);
                drawnCards.add(drawnCard);
                
                Optional<UserCard> existingCard = userCardRepository.findByUidAndCardId(uid, drawnCard.getId());
                if (existingCard.isPresent()) {
                    UserCard userCard = existingCard.get();
                    userCard.setQuantity(userCard.getQuantity() + 1);
                    userCardRepository.save(userCard);
                } else {
                    UserCard userCard = new UserCard();
                    userCard.setUid(uid);
                    userCard.setCardId(drawnCard.getId());
                    userCard.setQuantity(1);
                    userCardRepository.save(userCard);
                }
            }
            
            return drawnCards;
        } finally {
            userLock.unlock();
            userDrawLocks.remove(uid);
        }
    }

    public List<Map<String, Object>> getAlbumData(@NonNull Long uid) {
        List<AnimeCard> allCards = animeCardRepository.findAll();
        List<UserCard> userCards = userCardRepository.findByUid(uid);
        
        Map<Long, Integer> userCardQuantity = new HashMap<>();
        for (UserCard uc : userCards) {
            userCardQuantity.put(uc.getCardId(), uc.getQuantity());
        }
        
        List<Map<String, Object>> albumData = new ArrayList<>();
        for (AnimeCard card : allCards) {
            Map<String, Object> cardData = new HashMap<>();
            cardData.put("id", card.getId());
            cardData.put("seriesName", card.getSeriesName());
            cardData.put("name", card.getName());
            cardData.put("type", card.getType());
            cardData.put("rarityLevel", card.getRarityLevel());
            cardData.put("imageUrl", card.getImageUrl());
            
            Integer quantity = userCardQuantity.get(card.getId());
            cardData.put("owned", quantity != null && quantity > 0);
            cardData.put("quantity", quantity != null ? quantity : 0);
            
            albumData.add(cardData);
        }
        
        return albumData;
    }
}
