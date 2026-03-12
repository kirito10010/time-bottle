package com.timebottle.backend.service;

import com.timebottle.backend.entity.Bill;
import com.timebottle.backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public List<Bill> findByDate(Integer userId, LocalDate date) {
        return billRepository.findByUserIdAndBillDateAndIsDeleted(userId, date, "0");
    }

    public List<Bill> findAll(Integer userId) {
        return billRepository.findByUserIdAndIsDeleted(userId, "0");
    }

    public Bill create(Integer userId, Integer categoryId, Integer type, String account, BigDecimal amount, 
                      LocalDate billDate, LocalTime billTime, String remark) {
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
        return billRepository.save(bill);
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

    public void delete(@NonNull Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账单不存在"));
        
        bill.setIsDeleted("1");
        billRepository.save(bill);
    }
}
