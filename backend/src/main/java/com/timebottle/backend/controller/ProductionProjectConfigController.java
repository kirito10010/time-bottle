package com.timebottle.backend.controller;

import com.timebottle.backend.entity.ProductionProjectConfig;
import com.timebottle.backend.service.ProductionProjectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project-configs")
public class ProductionProjectConfigController {

    @Autowired
    private ProductionProjectConfigService projectConfigService;

    @GetMapping
    public ResponseEntity<?> getProjectConfigs(
            @RequestParam String uid,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<ProductionProjectConfig> configs = new ArrayList<>(projectConfigService.findByUid(uid));
            
            int total = configs.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<ProductionProjectConfig> paginatedConfigs = new ArrayList<>();
            if (start < total) {
                paginatedConfigs = configs.subList(start, end);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("configs", paginatedConfigs);
            response.put("total", total);
            response.put("page", page);
            response.put("pageSize", pageSize);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectConfig(@PathVariable @NonNull Long id) {
        try {
            ProductionProjectConfig config = projectConfigService.findById(id);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createProjectConfig(@RequestBody Map<String, Object> request) {
        try {
            String uid = (String) request.get("uid");
            String projectName = (String) request.get("projectName");
            BigDecimal operationQuota = new BigDecimal(request.get("operationQuota").toString());
            BigDecimal qualityQuota = new BigDecimal(request.get("qualityQuota").toString());
            
            ProductionProjectConfig config = projectConfigService.create(uid, projectName, operationQuota, qualityQuota);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建项目配置成功");
            response.put("config", config);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProjectConfig(@PathVariable @NonNull Long id, @RequestBody Map<String, Object> request) {
        try {
            String projectName = (String) request.get("projectName");
            BigDecimal operationQuota = new BigDecimal(request.get("operationQuota").toString());
            BigDecimal qualityQuota = new BigDecimal(request.get("qualityQuota").toString());
            
            ProductionProjectConfig config = projectConfigService.update(id, projectName, operationQuota, qualityQuota);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新项目配置成功");
            response.put("config", config);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjectConfig(@PathVariable @NonNull Long id) {
        try {
            projectConfigService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除项目配置成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/batch")
    @SuppressWarnings({"unchecked", "null"})
    public ResponseEntity<?> deleteProjectConfigsBatch(@RequestBody Map<String, Object> request) {
        try {
            List<Integer> idsInt = (List<Integer>) request.get("ids");
            List<Long> ids = idsInt.stream().map(Integer::longValue).toList();
            
            projectConfigService.deleteBatch(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "批量删除项目配置成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
