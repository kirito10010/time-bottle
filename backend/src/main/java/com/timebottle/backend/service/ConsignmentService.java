package com.timebottle.backend.service;

import com.timebottle.backend.entity.AnimeCard;
import com.timebottle.backend.entity.Consignment;
import com.timebottle.backend.entity.PointsLog;
import com.timebottle.backend.entity.User;
import com.timebottle.backend.entity.UserCard;
import com.timebottle.backend.repository.AnimeCardRepository;
import com.timebottle.backend.repository.ConsignmentRepository;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class ConsignmentService {

    @Autowired
    private ConsignmentRepository consignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCardRepository userCardRepository;

    @Autowired
    private AnimeCardRepository animeCardRepository;

    @Autowired
    private PointsLogRepository pointsLogRepository;

    public Page<Consignment> getAvailableConsignments(int page, int size) {
        return consignmentRepository.findAvailable(PageRequest.of(page, size));
    }

    public Page<Consignment> searchConsignments(String keyword, int page, int size) {
        if (keyword == null || keyword.isEmpty()) {
            return getAvailableConsignments(page, size);
        }
        return consignmentRepository.searchByKeyword(keyword, PageRequest.of(page, size));
    }

    public List<Map<String, Object>> getConsignmentsWithCardInfo(Page<Consignment> consignmentPage) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Consignment c : consignmentPage.getContent()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("sellerId", c.getSellerId());
            item.put("cardId", c.getCardId());
            item.put("unitPrice", c.getUnitPrice());
            item.put("quantity", c.getQuantity());
            item.put("createdAt", c.getCreatedAt());

            Optional<User> sellerOpt = userRepository.findById(c.getSellerId());
            if (sellerOpt.isPresent()) {
                User seller = sellerOpt.get();
                Map<String, Object> sellerInfo = new HashMap<>();
                sellerInfo.put("id", seller.getId());
                sellerInfo.put("nickname", seller.getNickname());
                sellerInfo.put("avatar", seller.getAvatar());
                item.put("seller", sellerInfo);
            }

            Optional<AnimeCard> cardOpt = animeCardRepository.findById(c.getCardId());
            if (cardOpt.isPresent()) {
                AnimeCard card = cardOpt.get();
                Map<String, Object> cardInfo = new HashMap<>();
                cardInfo.put("id", card.getId());
                cardInfo.put("name", card.getName());
                cardInfo.put("seriesName", card.getSeriesName());
                cardInfo.put("type", card.getType());
                cardInfo.put("rarityLevel", card.getRarityLevel());
                cardInfo.put("imageUrl", card.getImageUrl());
                item.put("card", cardInfo);
            }

            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getMyConsignments(@NonNull Integer userId) {
        List<Consignment> consignments = consignmentRepository.findBySellerId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Consignment c : consignments) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId());
            item.put("cardId", c.getCardId());
            item.put("unitPrice", c.getUnitPrice());
            item.put("quantity", c.getQuantity());
            item.put("createdAt", c.getCreatedAt());

            Optional<AnimeCard> cardOpt = animeCardRepository.findById(c.getCardId());
            if (cardOpt.isPresent()) {
                AnimeCard card = cardOpt.get();
                Map<String, Object> cardInfo = new HashMap<>();
                cardInfo.put("id", card.getId());
                cardInfo.put("name", card.getName());
                cardInfo.put("seriesName", card.getSeriesName());
                cardInfo.put("type", card.getType());
                cardInfo.put("rarityLevel", card.getRarityLevel());
                cardInfo.put("imageUrl", card.getImageUrl());
                item.put("card", cardInfo);
            }

            result.add(item);
        }
        
        return result;
    }

    @Transactional
    public Consignment listCard(@NonNull Integer sellerId, @NonNull Long cardId, 
                                 @NonNull Integer unitPrice, @NonNull Integer quantity) {
        if (unitPrice <= 0) {
            throw new RuntimeException("单价必须大于0");
        }
        if (quantity <= 0) {
            throw new RuntimeException("数量必须大于0");
        }

        Optional<UserCard> userCardOpt = userCardRepository.findByUidAndCardId(sellerId.longValue(), cardId);
        if (userCardOpt.isEmpty()) {
            throw new RuntimeException("您没有这张卡片");
        }
        
        UserCard userCard = userCardOpt.get();
        if (userCard.getQuantity() < quantity) {
            throw new RuntimeException("卡片数量不足，您只有 " + userCard.getQuantity() + " 张");
        }

        userCard.setQuantity(userCard.getQuantity() - quantity);
        if (userCard.getQuantity() <= 0) {
            userCardRepository.delete(userCard);
        } else {
            userCardRepository.save(userCard);
        }

        Consignment consignment = new Consignment();
        consignment.setSellerId(sellerId);
        consignment.setCardId(cardId);
        consignment.setUnitPrice(unitPrice);
        consignment.setQuantity(quantity);
        
        return consignmentRepository.save(consignment);
    }

    @Transactional
    public void delistCard(@NonNull Integer sellerId, @NonNull Long consignmentId) {
        Optional<Consignment> consignmentOpt = consignmentRepository.findById(consignmentId);
        if (consignmentOpt.isEmpty()) {
            throw new RuntimeException("寄售记录不存在");
        }
        
        Consignment consignment = consignmentOpt.get();
        if (!consignment.getSellerId().equals(sellerId)) {
            throw new RuntimeException("只能下架自己的商品");
        }

        Optional<UserCard> userCardOpt = userCardRepository.findByUidAndCardId(sellerId.longValue(), consignment.getCardId());
        if (userCardOpt.isPresent()) {
            UserCard userCard = userCardOpt.get();
            userCard.setQuantity(userCard.getQuantity() + consignment.getQuantity());
            userCardRepository.save(userCard);
        } else {
            UserCard userCard = new UserCard();
            userCard.setUid(sellerId.longValue());
            userCard.setCardId(consignment.getCardId());
            userCard.setQuantity(consignment.getQuantity());
            userCardRepository.save(userCard);
        }

        consignmentRepository.delete(consignment);
    }

    @Transactional
    public void buyCard(@NonNull Integer buyerId, @NonNull Long consignmentId, @NonNull Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("购买数量必须大于0");
        }

        Optional<Consignment> consignmentOpt = consignmentRepository.findById(consignmentId);
        if (consignmentOpt.isEmpty()) {
            throw new RuntimeException("商品不存在");
        }
        
        Consignment consignment = consignmentOpt.get();
        
        if (consignment.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }
        
        if (consignment.getQuantity() < quantity) {
            throw new RuntimeException("库存不足，当前库存: " + consignment.getQuantity());
        }

        int totalCost = consignment.getUnitPrice() * quantity;

        Optional<User> buyerOpt = userRepository.findById(buyerId);
        if (buyerOpt.isEmpty()) {
            throw new RuntimeException("买家不存在");
        }
        User buyer = buyerOpt.get();
        
        if (buyer.getPoints() < totalCost) {
            throw new RuntimeException("积分不足，需要 " + totalCost + " 积分");
        }

        Optional<User> sellerOpt = userRepository.findById(consignment.getSellerId());
        if (sellerOpt.isEmpty()) {
            throw new RuntimeException("卖家不存在");
        }
        User seller = sellerOpt.get();

        buyer.setPoints(buyer.getPoints() - totalCost);
        buyer.setUpdatedAt(new Date());
        userRepository.save(buyer);

        PointsLog buyerLog = new PointsLog();
        buyerLog.setUserId(buyerId);
        buyerLog.setChange(-totalCost);
        buyerLog.setType("exchange");
        buyerLog.setRemark("购买卡片[" + consignment.getCardId() + "] x" + quantity);
        pointsLogRepository.save(buyerLog);

        seller.setPoints(seller.getPoints() + totalCost);
        seller.setUpdatedAt(new Date());
        userRepository.save(seller);

        PointsLog sellerLog = new PointsLog();
        sellerLog.setUserId(consignment.getSellerId());
        sellerLog.setChange(totalCost);
        sellerLog.setType("exchange");
        sellerLog.setRemark("出售卡片[" + consignment.getCardId() + "] x" + quantity);
        pointsLogRepository.save(sellerLog);

        Optional<UserCard> buyerCardOpt = userCardRepository.findByUidAndCardId(buyerId.longValue(), consignment.getCardId());
        if (buyerCardOpt.isPresent()) {
            UserCard buyerCard = buyerCardOpt.get();
            buyerCard.setQuantity(buyerCard.getQuantity() + quantity);
            userCardRepository.save(buyerCard);
        } else {
            UserCard buyerCard = new UserCard();
            buyerCard.setUid(buyerId.longValue());
            buyerCard.setCardId(consignment.getCardId());
            buyerCard.setQuantity(quantity);
            userCardRepository.save(buyerCard);
        }

        consignment.setQuantity(consignment.getQuantity() - quantity);
        if (consignment.getQuantity() <= 0) {
            consignmentRepository.delete(consignment);
        } else {
            consignmentRepository.save(consignment);
        }
    }

    public List<Map<String, Object>> getMySellableCards(@NonNull Integer userId) {
        System.out.println("=== getMySellableCards called with userId: " + userId + " ===");
        List<UserCard> userCards = userCardRepository.findByUid(userId.longValue());
        System.out.println("=== Found " + userCards.size() + " user cards ===");
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (UserCard uc : userCards) {
            System.out.println("=== UserCard: uid=" + uc.getUid() + ", cardId=" + uc.getCardId() + ", quantity=" + uc.getQuantity() + " ===");
            if (uc.getQuantity() > 0) {
                Optional<AnimeCard> cardOpt = animeCardRepository.findById(uc.getCardId());
                if (cardOpt.isPresent()) {
                    AnimeCard card = cardOpt.get();
                    Map<String, Object> item = new HashMap<>();
                    item.put("cardId", card.getId());
                    item.put("name", card.getName());
                    item.put("seriesName", card.getSeriesName());
                    item.put("type", card.getType());
                    item.put("rarityLevel", card.getRarityLevel());
                    item.put("imageUrl", card.getImageUrl());
                    item.put("quantity", uc.getQuantity());
                    result.add(item);
                }
            }
        }
        
        System.out.println("=== Returning " + result.size() + " sellable cards ===");
        return result;
    }
}
