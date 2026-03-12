package com.timebottle.backend.controller;

import com.timebottle.backend.entity.BillCategory;
import com.timebottle.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 获取所有分类
    @GetMapping
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false, defaultValue = "0") Integer userId) {
        try {
            List<BillCategory> categories = categoryService.findAll(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取指定类型的分类
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getCategoriesByType(@PathVariable int type, 
                                                 @RequestParam(required = false, defaultValue = "0") Integer userId) {
        try {
            List<BillCategory> categories = categoryService.findByTypeAndUserId(type, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            int type = (int) request.get("type");
            int userId = (int) request.get("userId");
            int sort = request.get("sort") != null ? ((Number) request.get("sort")).intValue() : 0;
            boolean isDefault = request.get("isDefault") != null && (boolean) request.get("isDefault");
            BillCategory category = categoryService.create(name, type, userId, sort, isDefault);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建分类成功");
            response.put("category", category);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable int id, @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            int type = (int) request.get("type");
            int sort = request.get("sort") != null ? ((Number) request.get("sort")).intValue() : 0;
            String userRole = request.get("userRole") != null ? (String) request.get("userRole") : "0";
            BillCategory category = categoryService.update(id, name, type, sort, userRole);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新分类成功");
            response.put("category", category);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id, @RequestParam(required = false, defaultValue = "0") String userRole) {
        try {
            categoryService.delete(id, userRole);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除分类成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
