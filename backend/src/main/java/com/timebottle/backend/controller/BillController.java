package com.timebottle.backend.controller;

import com.timebottle.backend.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    // 获取账单（支持多条件筛选）
    @GetMapping
    public ResponseEntity<?> getBills(@RequestParam(required = false) String date, 
                                    @RequestParam(required = false) String type, 
                                    @RequestParam(required = false) String categoryId, 
                                    @RequestParam(required = false) String account, 
                                    @RequestParam(required = false) String minAmount, 
                                    @RequestParam(required = false) String maxAmount, 
                                    @RequestParam(required = false) String remark, 
                                    @RequestParam(required = false, defaultValue = "0") Integer userId,
                                    @RequestParam(defaultValue = "1") Integer page, 
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            var bills = new ArrayList<>(billService.findAll(userId));
            
            // 应用筛选条件
            if (date != null) {
                LocalDate billDate = LocalDate.parse(date);
                bills = new ArrayList<>(bills.stream().filter(bill -> bill.getBillDate().equals(billDate)).toList());
            }
            
            if (type != null && !type.isEmpty()) {
                try {
                    String typeStr = type;
                    bills = new ArrayList<>(bills.stream().filter(bill -> bill.getType().equals(typeStr)).toList());
                } catch (NumberFormatException e) {
                }
            }
            
            if (categoryId != null && !categoryId.isEmpty()) {
                try {
                    Integer categoryIdInt = Integer.parseInt(categoryId);
                    bills = new ArrayList<>(bills.stream().filter(bill -> bill.getCategoryId().equals(categoryIdInt)).toList());
                } catch (NumberFormatException e) {
                    // 忽略格式错误的分类ID
                }
            }
            
            if (account != null) {
                bills = new ArrayList<>(bills.stream().filter(bill -> bill.getAccount().equals(account)).toList());
            }
            
            if (minAmount != null && !minAmount.isEmpty()) {
                try {
                    BigDecimal min = new BigDecimal(minAmount);
                    bills = new ArrayList<>(bills.stream().filter(bill -> bill.getAmount().compareTo(min) >= 0).toList());
                } catch (NumberFormatException e) {
                    // 忽略格式错误的金额
                }
            }
            
            if (maxAmount != null && !maxAmount.isEmpty()) {
                try {
                    BigDecimal max = new BigDecimal(maxAmount);
                    bills = new ArrayList<>(bills.stream().filter(bill -> bill.getAmount().compareTo(max) <= 0).toList());
                } catch (NumberFormatException e) {
                    // 忽略格式错误的金额
                }
            }
            
            if (remark != null) {
                bills = new ArrayList<>(bills.stream().filter(bill -> bill.getRemark() != null && bill.getRemark().contains(remark)).toList());
            }
            
            // 按时间从新到旧排序
            bills.sort((b1, b2) -> {
                if (b1.getBillDate() == null && b2.getBillDate() == null) {
                    return 0;
                } else if (b1.getBillDate() == null) {
                    return 1;
                } else if (b2.getBillDate() == null) {
                    return -1;
                } else {
                    return b2.getBillDate().compareTo(b1.getBillDate());
                }
            });
            
            // 计算分页
            List<?> paginatedBills = new ArrayList<>();
            if (!bills.isEmpty()) {
                int start = (page - 1) * pageSize;
                int end = Math.min(start + pageSize, bills.size());
                if (start < end) {
                    paginatedBills = bills.subList(start, end);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("bills", paginatedBills);
            response.put("total", bills.size());
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

    // 创建账单
    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            if (userId == null || userId == 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            Integer categoryId = (Integer) request.get("category_id");
            Integer type = (Integer) request.get("type");
            String account = (String) request.get("account");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            LocalDate billDate = LocalDate.parse((String) request.get("bill_date"));
            
            LocalTime billTime = null;
            if (request.get("bill_time") != null && !request.get("bill_time").toString().isEmpty()) {
                billTime = LocalTime.parse((String) request.get("bill_time"));
            }
            
            String remark = (String) request.get("remark");
            
            var bill = billService.create(userId, categoryId, type, account, amount, billDate, billTime, remark);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "创建账单成功");
            response.put("bill", bill);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 更新账单
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBill(@PathVariable @NonNull Long id, @RequestBody Map<String, Object> request) {
        try {
            Integer categoryId = (Integer) request.get("category_id");
            Integer type = (Integer) request.get("type");
            String account = (String) request.get("account");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            LocalDate billDate = LocalDate.parse((String) request.get("bill_date"));
            
            LocalTime billTime = null;
            if (request.get("bill_time") != null && !request.get("bill_time").toString().isEmpty()) {
                billTime = LocalTime.parse((String) request.get("bill_time"));
            }
            
            String remark = (String) request.get("remark");
            
            var bill = billService.update(id, categoryId, type, account, amount, billDate, billTime, remark);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "更新账单成功");
            response.put("bill", bill);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 删除账单
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBill(@PathVariable @NonNull Long id) {
        try {
            billService.delete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除账单成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("error", e.toString());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 获取所有账单，按时间从新到旧排序
    @GetMapping("/all")
    public ResponseEntity<?> getAllBills(@RequestParam(required = false, defaultValue = "0") Integer userId,
                                       @RequestParam(defaultValue = "1") Integer page, 
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            var bills = new ArrayList<>(billService.findAll(userId));
            
            // 按时间从新到旧排序
            bills.sort((b1, b2) -> {
                if (b1.getBillDate() == null && b2.getBillDate() == null) {
                    return 0;
                } else if (b1.getBillDate() == null) {
                    return 1;
                } else if (b2.getBillDate() == null) {
                    return -1;
                } else {
                    return b2.getBillDate().compareTo(b1.getBillDate());
                }
            });
            
            // 计算分页
            List<?> paginatedBills = new ArrayList<>();
            if (!bills.isEmpty()) {
                int start = (page - 1) * pageSize;
                int end = Math.min(start + pageSize, bills.size());
                if (start < end) {
                    paginatedBills = bills.subList(start, end);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("bills", paginatedBills);
            response.put("total", bills.size());
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
}
