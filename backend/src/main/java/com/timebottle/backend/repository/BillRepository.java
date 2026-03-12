package com.timebottle.backend.repository;

import com.timebottle.backend.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByUserIdAndBillDateAndIsDeleted(Integer userId, LocalDate billDate, String isDeleted);

    List<Bill> findByUserIdAndIsDeleted(Integer userId, String isDeleted);
    
    List<Bill> findByCategoryIdAndIsDeleted(Integer categoryId, String isDeleted);
    
    boolean existsByCategoryIdAndIsDeleted(Integer categoryId, String isDeleted);
}
