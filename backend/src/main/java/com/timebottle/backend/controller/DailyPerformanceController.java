package com.timebottle.backend.controller;

import com.timebottle.backend.entity.DailyPerformance;
import com.timebottle.backend.entity.ProductionProjectConfig;
import com.timebottle.backend.service.DailyPerformanceService;
import com.timebottle.backend.service.ProductionProjectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/daily-performances")
public class DailyPerformanceController {

    @Autowired
    private DailyPerformanceService dailyPerformanceService;

    @Autowired
    private ProductionProjectConfigService projectConfigService;

    @GetMapping
    public ResponseEntity<?> getDailyPerformances(
            @RequestParam String uid,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<DailyPerformance> performances = new ArrayList<>(dailyPerformanceService.findByUid(uid));
            
            int total = performances.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<DailyPerformance> paginatedPerformances = new ArrayList<>();
            if (start < total) {
                paginatedPerformances = performances.subList(start, end);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("performances", paginatedPerformances);
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
    public ResponseEntity<?> getDailyPerformance(@PathVariable @NonNull Long id) {
        try {
            DailyPerformance performance = dailyPerformanceService.findById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("performance", performance);
            
            Long projectId = performance.getProjectId();
            if (projectId == null) {
                response.put("projectName", "未知项目");
            } else {
                ProductionProjectConfig project = projectConfigService.findById(projectId);
                response.put("projectName", project.getProjectName());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    @SuppressWarnings("null")
    public ResponseEntity<?> createDailyPerformance(@RequestBody Map<String, Object> request) {
        try {
            String uid = (String) request.get("uid");
            LocalDate recordDate = LocalDate.parse((String) request.get("recordDate"));
            Long projectId = Long.valueOf(request.get("projectId").toString());
            String processType = (String) request.get("processType");
            BigDecimal quotaEfficiency = new BigDecimal(request.get("quotaEfficiency").toString());
            BigDecimal actualWorkload = new BigDecimal(request.get("actualWorkload").toString());
            
            DailyPerformance performance = dailyPerformanceService.create(
                    uid, recordDate, projectId, processType, quotaEfficiency, actualWorkload);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建绩效记录成功");
            response.put("performance", performance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @SuppressWarnings("null")
    public ResponseEntity<?> updateDailyPerformance(@PathVariable @NonNull Long id, @RequestBody Map<String, Object> request) {
        try {
            LocalDate recordDate = LocalDate.parse((String) request.get("recordDate"));
            Long projectId = Long.valueOf(request.get("projectId").toString());
            String processType = (String) request.get("processType");
            BigDecimal quotaEfficiency = new BigDecimal(request.get("quotaEfficiency").toString());
            BigDecimal actualWorkload = new BigDecimal(request.get("actualWorkload").toString());
            
            DailyPerformance performance = dailyPerformanceService.update(
                    id, recordDate, projectId, processType, quotaEfficiency, actualWorkload);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新绩效记录成功");
            response.put("performance", performance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDailyPerformance(@PathVariable @NonNull Long id) {
        try {
            dailyPerformanceService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除绩效记录成功");
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
    public ResponseEntity<?> deleteDailyPerformancesBatch(@RequestBody Map<String, Object> request) {
        try {
            List<Integer> idsInt = (List<Integer>) request.get("ids");
            List<Long> ids = idsInt.stream().map(Integer::longValue).toList();
            
            dailyPerformanceService.deleteBatch(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "批量删除绩效记录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
