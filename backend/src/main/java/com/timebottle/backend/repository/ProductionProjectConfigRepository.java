package com.timebottle.backend.repository;

import com.timebottle.backend.entity.ProductionProjectConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionProjectConfigRepository extends JpaRepository<ProductionProjectConfig, Long> {

    List<ProductionProjectConfig> findByUid(String uid);
    
    List<ProductionProjectConfig> findByUidOrderByCreatedAtDesc(String uid);
    
    boolean existsByUidAndProjectName(String uid, String projectName);
}
