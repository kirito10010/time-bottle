package com.timebottle.backend.controller;

import com.timebottle.backend.entity.AnimeCard;
import com.timebottle.backend.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public ResponseEntity<List<AnimeCard>> getAllCards() {
        List<AnimeCard> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
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

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String type = request.get("type");
            String imageUrl = request.get("imageUrl");

            if (name == null || name.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "卡片名称不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (type == null || type.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "卡片类型不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (imageUrl == null || imageUrl.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "图片URL不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            AnimeCard card = cardService.createCard(name, type, imageUrl);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建成功");
            response.put("card", card);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(@PathVariable @NonNull Long id, @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String type = request.get("type");
            String imageUrl = request.get("imageUrl");

            AnimeCard card = cardService.updateCard(id, name, type, imageUrl);
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
}
