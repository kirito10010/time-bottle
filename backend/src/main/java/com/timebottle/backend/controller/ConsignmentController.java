package com.timebottle.backend.controller;

import com.timebottle.backend.entity.Consignment;
import com.timebottle.backend.service.ConsignmentService;
import com.timebottle.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consignments")
@SuppressWarnings("null")
public class ConsignmentController {

    @Autowired
    private ConsignmentService consignmentService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getConsignments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String series,
            @RequestParam(required = false) Integer rarity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Page<Consignment> consignmentPage = consignmentService.searchConsignments(keyword, series, rarity, page, size);
        List<Map<String, Object>> items = consignmentService.getConsignmentsWithCardInfo(consignmentPage);
        
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("currentPage", consignmentPage.getNumber());
        response.put("totalPages", consignmentPage.getTotalPages());
        response.put("totalElements", consignmentPage.getTotalElements());
        response.put("seriesList", consignmentService.getAllSeriesNames());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/series")
    public ResponseEntity<?> getAllSeries() {
        List<String> seriesList = consignmentService.getAllSeriesNames();
        return ResponseEntity.ok(seriesList);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyConsignments(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String series,
            @RequestParam(required = false) Integer rarity) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of());
            return ResponseEntity.ok(response);
        }
        
        List<Map<String, Object>> items = consignmentService.getMyConsignments(userId, keyword, series, rarity);
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("seriesList", consignmentService.getAllSeriesNames());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sellable")
    public ResponseEntity<?> getMySellableCards(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String series,
            @RequestParam(required = false) Integer rarity) {
        System.out.println("=== /sellable called, token: " + (token != null ? "present" : "null") + " ===");
        Integer userId = extractUserId(token);
        System.out.println("=== Extracted userId: " + userId + " ===");
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of());
            return ResponseEntity.ok(response);
        }
        
        List<Map<String, Object>> items = consignmentService.getMySellableCards(userId, keyword, series, rarity);
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("seriesList", consignmentService.getAllSeriesNames());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/list")
    public ResponseEntity<?> listCard(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Long cardId = ((Number) request.get("cardId")).longValue();
            Integer unitPrice = ((Number) request.get("unitPrice")).intValue();
            Integer quantity = ((Number) request.get("quantity")).intValue();
            
            Consignment consignment = consignmentService.listCard(userId, cardId, unitPrice, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "上架成功");
            response.put("consignmentId", consignment.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/delist/{id}")
    public ResponseEntity<?> delistCard(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            consignmentService.delistCard(userId, id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "下架成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/buy/{id}")
    public ResponseEntity<?> buyCard(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Integer quantity = ((Number) request.get("quantity")).intValue();
            
            consignmentService.buyCard(userId, id, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "购买成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/recycle")
    public ResponseEntity<?> recycleCard(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Long cardId = ((Number) request.get("cardId")).longValue();
            Integer quantity = ((Number) request.get("quantity")).intValue();
            
            int earnedPoints = consignmentService.recycleCard(userId, cardId, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "回收成功，获得 " + earnedPoints + " 积分");
            response.put("earnedPoints", earnedPoints);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/batch-recycle")
    public ResponseEntity<?> batchRecycleCards(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        Integer userId = extractUserId(token);
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        try {
            String series = (String) request.get("series");
            Integer rarity = request.get("rarity") != null ? ((Number) request.get("rarity")).intValue() : null;
            Integer keepCount = request.get("keepCount") != null ? ((Number) request.get("keepCount")).intValue() : 0;

            int earnedPoints = consignmentService.batchRecycleCards(userId, series, rarity, keepCount);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量回收成功，获得 " + earnedPoints + " 积分");
            response.put("earnedPoints", earnedPoints);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private Integer extractUserId(String token) {
        if (token == null || token.isEmpty()) return null;
        String cleanToken = token.replace("Bearer ", "");
        return jwtUtil.extractUserId(cleanToken);
    }
}
