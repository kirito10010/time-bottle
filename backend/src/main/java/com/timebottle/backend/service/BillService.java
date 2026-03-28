package com.timebottle.backend.service;

import com.timebottle.backend.entity.Bill;
import com.timebottle.backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PointsService pointsService;

    public List<Bill> findByDate(Integer userId, LocalDate date) {
        return billRepository.findByUserIdAndBillDateAndIsDeleted(userId, date, "0");
    }

    public List<Bill> findAll(Integer userId) {
        return billRepository.findByUserIdAndIsDeleted(userId, "0");
    }

    @Transactional
    public Bill create(@NonNull Integer userId, Integer categoryId, Integer type, String account, BigDecimal amount, 
                      LocalDate billDate, LocalTime billTime, String remark) {
        List<Bill> todayBills = billRepository.findByUserIdAndBillDateAndIsDeleted(userId, billDate, "0");
        int todayCount = todayBills.size();
        
        if (todayCount >= 10) {
            int extraCount = todayCount - 10 + 1;
            int deductPoints = extraCount * 20;
            pointsService.deductPoints(userId, deductPoints, "记账超额扣减 - 今日第" + (todayCount + 1) + "条记录");
        }
        
        Bill bill = new Bill();
        bill.setUserId(userId);
        bill.setCategoryId(categoryId);
        bill.setType(String.valueOf(type));
        bill.setAccount(account);
        bill.setAmount(amount);
        bill.setBillDate(billDate);
        bill.setBillTime(billTime);
        bill.setRemark(remark);
        bill.setIsDeleted("0");
        Bill savedBill = billRepository.save(bill);
        
        pointsService.addAccountingPoints(userId, "记账奖励 - " + (remark != null ? remark : "账单#" + savedBill.getId()));
        
        return savedBill;
    }

    public Bill update(@NonNull Long id, Integer categoryId, Integer type, String account, BigDecimal amount, 
                      LocalDate billDate, LocalTime billTime, String remark) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账单不存在"));
        
        bill.setCategoryId(categoryId);
        bill.setType(String.valueOf(type));
        bill.setAccount(account);
        bill.setAmount(amount);
        bill.setBillDate(billDate);
        bill.setBillTime(billTime);
        bill.setRemark(remark);
        return billRepository.save(bill);
    }

    @Transactional
    public void delete(@NonNull Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账单不存在"));
        
        bill.setIsDeleted("1");
        billRepository.save(bill);
        
        Integer userId = bill.getUserId();
        if (userId != null) {
            pointsService.deductAccountingPoints(userId, "删除账单扣减 - 账单#" + id);
        }
    }
}
