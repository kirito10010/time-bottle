package com.timebottle.backend.service;

import com.timebottle.backend.entity.AnimeCard;
import com.timebottle.backend.entity.UserCard;
import com.timebottle.backend.repository.AnimeCardRepository;
import com.timebottle.backend.repository.UserCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class CardService {

    @Autowired
    private AnimeCardRepository animeCardRepository;

    @Autowired
    private UserCardRepository userCardRepository;

    public List<AnimeCard> getAllCards() {
        return animeCardRepository.findAll();
    }

    public Optional<AnimeCard> getCardById(@NonNull Long id) {
        return animeCardRepository.findById(id);
    }

    public List<AnimeCard> getCardsByType(@NonNull String type) {
        return animeCardRepository.findByType(type);
    }

    @Transactional
    public AnimeCard createCard(@NonNull String name, @NonNull String type, @NonNull String imageUrl) {
        AnimeCard card = new AnimeCard();
        card.setName(name);
        card.setType(type);
        card.setImageUrl(imageUrl);
        return animeCardRepository.save(card);
    }

    @Transactional
    public AnimeCard updateCard(@NonNull Long id, String name, String type, String imageUrl) {
        Optional<AnimeCard> cardOpt = animeCardRepository.findById(id);
        if (cardOpt.isEmpty()) {
            throw new RuntimeException("卡片不存在");
        }
        AnimeCard card = cardOpt.get();
        if (name != null && !name.isEmpty()) card.setName(name);
        if (type != null && !type.isEmpty()) card.setType(type);
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
}
