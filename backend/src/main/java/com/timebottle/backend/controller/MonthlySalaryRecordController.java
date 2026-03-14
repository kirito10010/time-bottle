package com.timebottle.backend.controller;

import com.timebottle.backend.entity.MonthlySalaryRecord;
import com.timebottle.backend.service.MonthlySalaryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salary-records")
public class MonthlySalaryRecordController {

    @Autowired
    private MonthlySalaryRecordService service;

    @GetMapping
    public ResponseEntity<?> getByUid(@RequestParam String uid) {
        List<MonthlySalaryRecord> records = service.getByUid(uid);
        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MonthlySalaryRecord record) {
        try {
            MonthlySalaryRecord saved = service.create(record);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建成功");
            response.put("record", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MonthlySalaryRecord record) {
        try {
            MonthlySalaryRecord saved = service.update(id, record);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新成功");
            response.put("record", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDelete(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "请选择要删除的记录");
                return ResponseEntity.badRequest().body(response);
            }
            service.batchDelete(ids);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "批量删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
