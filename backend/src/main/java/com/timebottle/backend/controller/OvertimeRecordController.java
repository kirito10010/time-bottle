package com.timebottle.backend.controller;

import com.timebottle.backend.entity.OvertimeRecord;
import com.timebottle.backend.entity.ProductionProjectConfig;
import com.timebottle.backend.service.OvertimeRecordService;
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
@RequestMapping("/api/overtime-records")
public class OvertimeRecordController {

    @Autowired
    private OvertimeRecordService overtimeRecordService;

    @Autowired
    private ProductionProjectConfigService projectConfigService;

    @GetMapping
    public ResponseEntity<?> getOvertimeRecords(
            @RequestParam String uid,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            List<OvertimeRecord> records = new ArrayList<>(overtimeRecordService.findByUid(uid));
            
            int total = records.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<OvertimeRecord> paginatedRecords = new ArrayList<>();
            if (start < total) {
                paginatedRecords = records.subList(start, end);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("records", paginatedRecords);
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
    public ResponseEntity<?> getOvertimeRecord(@PathVariable @NonNull Long id) {
        try {
            OvertimeRecord record = overtimeRecordService.findById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("record", record);
            
            Long projectId = record.getProjectId();
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
    public ResponseEntity<?> createOvertimeRecord(@RequestBody Map<String, Object> request) {
        try {
            String uid = (String) request.get("uid");
            LocalDate recordDate = LocalDate.parse((String) request.get("recordDate"));
            Long projectId = Long.valueOf(request.get("projectId").toString());
            BigDecimal overtimeHours = new BigDecimal(request.get("overtimeHours").toString());
            String description = (String) request.get("description");
            
            OvertimeRecord record = overtimeRecordService.create(
                    uid, recordDate, projectId, overtimeHours, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建加班记录成功");
            response.put("record", record);
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
    public ResponseEntity<?> updateOvertimeRecord(@PathVariable @NonNull Long id, @RequestBody Map<String, Object> request) {
        try {
            LocalDate recordDate = LocalDate.parse((String) request.get("recordDate"));
            Long projectId = Long.valueOf(request.get("projectId").toString());
            BigDecimal overtimeHours = new BigDecimal(request.get("overtimeHours").toString());
            String description = (String) request.get("description");
            
            OvertimeRecord record = overtimeRecordService.update(
                    id, recordDate, projectId, overtimeHours, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新加班记录成功");
            response.put("record", record);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOvertimeRecord(@PathVariable @NonNull Long id) {
        try {
            overtimeRecordService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除加班记录成功");
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
    public ResponseEntity<?> deleteOvertimeRecordsBatch(@RequestBody Map<String, Object> request) {
        try {
            List<Integer> idsInt = (List<Integer>) request.get("ids");
            List<Long> ids = idsInt.stream().map(Integer::longValue).toList();
            
            overtimeRecordService.deleteBatch(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "批量删除加班记录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
