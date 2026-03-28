package com.timebottle.backend.controller;

import com.timebottle.backend.entity.CardExchange;
import com.timebottle.backend.entity.CardGift;
import com.timebottle.backend.entity.User;
import com.timebottle.backend.service.CardExchangeService;
import com.timebottle.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/card-exchange")
@CrossOrigin(origins = "*")
public class CardExchangeController {

    @Autowired
    private CardExchangeService cardExchangeService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/search-users")
    public ResponseEntity<?> searchUsers(
            @RequestParam String keyword,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        List<User> users = cardExchangeService.searchUsers(keyword, userId);
        List<Map<String, Object>> userList = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("nickname", u.getNickname());
            map.put("avatar", u.getAvatar());
            return map;
        }).toList();
        
        return ResponseEntity.ok(Map.of("users", userList));
    }

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        List<User> users = cardExchangeService.getAllUsers(userId);
        List<Map<String, Object>> userList = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("nickname", u.getNickname());
            map.put("avatar", u.getAvatar());
            return map;
        }).toList();
        
        return ResponseEntity.ok(Map.of("users", userList));
    }

    @GetMapping("/find-users-with-card")
    public ResponseEntity<?> findUsersWithCard(
            @RequestParam Long cardId,
            @RequestParam(defaultValue = "1") Integer minQuantity,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        List<Map<String, Object>> users = cardExchangeService.findUsersWithCard(cardId, minQuantity, userId);
        return ResponseEntity.ok(Map.of("users", users));
    }

    @GetMapping("/my-cards")
    public ResponseEntity<?> getMyCards(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        List<Map<String, Object>> cards = cardExchangeService.getMyCards(userId);
        return ResponseEntity.ok(Map.of("cards", cards));
    }

    @GetMapping("/search-cards")
    public ResponseEntity<?> searchCards(
            @RequestParam(required = false) String keyword,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        List<Map<String, Object>> cards = cardExchangeService.searchCards(keyword);
        return ResponseEntity.ok(Map.of("cards", cards));
    }

    @PostMapping("/gift")
    public ResponseEntity<?> sendGift(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Integer senderId = extractUserId(token);
        if (senderId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        Integer receiverId = (Integer) request.get("receiverId");
        Long cardId = Long.valueOf(request.get("cardId").toString());
        Integer quantity = request.get("quantity") != null ? (Integer) request.get("quantity") : 1;
        
        try {
            CardGift gift = cardExchangeService.sendGift(senderId, receiverId, cardId, quantity);
            return ResponseEntity.ok(Map.of("success", true, "message", "赠送成功", "gift", gift));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/exchange")
    public ResponseEntity<?> sendExchangeRequest(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Integer senderId = extractUserId(token);
        if (senderId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        Integer receiverId = (Integer) request.get("receiverId");
        Long senderCardId = Long.valueOf(request.get("senderCardId").toString());
        Integer senderQty = request.get("senderCardQuantity") != null ? (Integer) request.get("senderCardQuantity") : 1;
        Long receiverCardId = Long.valueOf(request.get("receiverCardId").toString());
        Integer receiverQty = request.get("receiverCardQuantity") != null ? (Integer) request.get("receiverCardQuantity") : 1;
        
        try {
            CardExchange exchange = cardExchangeService.sendExchangeRequest(
                senderId, receiverId, senderCardId, senderQty, receiverCardId, receiverQty);
            return ResponseEntity.ok(Map.of("success", true, "message", "交换申请已发送", "exchange", exchange));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/exchange/{id}/accept")
    public ResponseEntity<?> acceptExchange(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        try {
            cardExchangeService.acceptExchange(id, userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "交换成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/exchange/{id}/reject")
    public ResponseEntity<?> rejectExchange(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        try {
            cardExchangeService.rejectExchange(id, userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "已拒绝交换"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/gifts/received")
    public ResponseEntity<?> getReceivedGifts(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        List<CardGift> gifts = cardExchangeService.getReceivedGifts(userId);
        gifts.forEach(cardExchangeService::fillGiftDetails);
        return ResponseEntity.ok(Map.of("gifts", gifts));
    }

    @GetMapping("/exchanges/pending")
    public ResponseEntity<?> getPendingExchanges(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        List<CardExchange> exchanges = cardExchangeService.getPendingExchanges(userId);
        exchanges.forEach(cardExchangeService::fillExchangeDetails);
        return ResponseEntity.ok(Map.of("exchanges", exchanges));
    }

    @GetMapping("/exchanges/my-requests")
    public ResponseEntity<?> getMyExchangeRequests(@RequestHeader(value = "Authorization", required = false) String token) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "请先登录"));
        }
        
        List<CardExchange> exchanges = cardExchangeService.getMyExchangeRequests(userId);
        exchanges.forEach(cardExchangeService::fillExchangeDetails);
        return ResponseEntity.ok(Map.of("exchanges", exchanges));
    }

    private Integer extractUserId(String token) {
        if (token == null || token.isEmpty()) return null;
        String cleanToken = token.replace("Bearer ", "");
        return jwtUtil.extractUserId(cleanToken);
    }
}
