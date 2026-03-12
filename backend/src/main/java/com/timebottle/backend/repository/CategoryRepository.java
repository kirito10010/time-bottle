package com.timebottle.backend.repository;

import com.timebottle.backend.entity.BillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<BillCategory, Integer> {

    List<BillCategory> findByIsDeleted(String isDeleted);

    List<BillCategory> findByTypeAndIsDeleted(String type, String isDeleted);

    List<BillCategory> findByUserIdAndIsDeleted(Integer userId, String isDeleted);

    List<BillCategory> findByTypeAndUserIdAndIsDeleted(String type, Integer userId, String isDeleted);

    Optional<BillCategory> findByNameAndUserIdAndIsDeleted(String name, Integer userId, String isDeleted);

    Optional<BillCategory> findByNameAndTypeAndUserIdAndIsDeleted(String name, String type, Integer userId, String isDeleted);
}
