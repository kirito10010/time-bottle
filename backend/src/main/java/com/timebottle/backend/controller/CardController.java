package com.timebottle.backend.controller;

import com.timebottle.backend.entity.AnimeCard;
import com.timebottle.backend.service.CardService;
import com.timebottle.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AnimeCard> cardPage = cardService.getCardsPage(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("items", cardPage.getContent());
        response.put("currentPage", cardPage.getNumber());
        response.put("totalPages", cardPage.getTotalPages());
        response.put("totalElements", cardPage.getTotalElements());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCardById(@PathVariable @NonNull Long id) {
        return cardService.getCardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<AnimeCard>> getCardsByType(@PathVariable @NonNull String type) {
        List<AnimeCard> cards = cardService.getCardsByType(type);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserCards(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("cards", List.of());
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }
        
        List<AnimeCard> userCards = cardService.getUserAnimeCards(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("cards", userCards);
        response.put("count", userCards.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/album")
    public ResponseEntity<?> getAlbumData(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("cards", List.of());
            response.put("totalCards", 0);
            response.put("ownedCards", 0);
            response.put("completionRate", 0);
            return ResponseEntity.ok(response);
        }
        
        List<Map<String, Object>> albumData = cardService.getAlbumData(userId);
        
        long totalCards = albumData.size();
        long ownedCards = albumData.stream().filter(c -> (Boolean) c.get("owned")).count();
        int completionRate = totalCards > 0 ? (int) ((ownedCards * 100) / totalCards) : 0;
        
        Map<String, Object> response = new HashMap<>();
        response.put("cards", albumData);
        response.put("totalCards", totalCards);
        response.put("ownedCards", ownedCards);
        response.put("completionRate", completionRate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/draw")
    public ResponseEntity<?> drawCard(
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) Map<String, Integer> request) {
        Long userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.badRequest().body(response);
        }

        int count = 1;
        if (request != null && request.containsKey("count")) {
            count = request.get("count");
        }

        try {
            List<AnimeCard> drawnCards = cardService.drawMultipleCards(userId, count);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cards", drawnCards);
            response.put("count", drawnCards.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody Map<String, Object> request) {
        try {
            String seriesName = (String) request.get("seriesName");
            String name = (String) request.get("name");
            String type = (String) request.get("type");
            Integer rarityLevel = request.get("rarityLevel") != null 
                ? ((Number) request.get("rarityLevel")).intValue() 
                : 3;
            String imageUrl = (String) request.get("imageUrl");

            if (seriesName == null || seriesName.toString().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "系列名称不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (name == null || name.toString().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "卡片名称不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (type == null || type.toString().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "卡片类型不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (imageUrl == null || imageUrl.toString().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "图片URL不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            AnimeCard card = cardService.createCard(seriesName, name, type, rarityLevel, imageUrl);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建成功");
            response.put("card", card);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
    }    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(@PathVariable @NonNull Long id, @RequestBody Map<String, Object> request) {
        try {
            String seriesName = (String) request.get("seriesName");
            String name = (String) request.get("name");
            String type = (String) request.get("type");
            Integer rarityLevel = request.get("rarityLevel") != null 
                ? ((Number) request.get("rarityLevel")).intValue() 
                : null;
            String imageUrl = (String) request.get("imageUrl");

            AnimeCard card = cardService.updateCard(id, seriesName, name, type, rarityLevel, imageUrl);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新成功");
            response.put("card", card);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable @NonNull Long id) {
        try {
            cardService.deleteCard(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private Long extractUserId(String token) {
        if (token == null || token.isEmpty()) return null;
        String cleanToken = token.replace("Bearer ", "");
        Integer userId = jwtUtil.extractUserId(cleanToken);
        return userId != null ? userId.longValue() : null;
    }
}
