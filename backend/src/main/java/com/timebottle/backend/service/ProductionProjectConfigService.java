package com.timebottle.backend.service;

import com.timebottle.backend.entity.ProductionProjectConfig;
import com.timebottle.backend.repository.ProductionProjectConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductionProjectConfigService {

    @Autowired
    private ProductionProjectConfigRepository projectConfigRepository;

    public List<ProductionProjectConfig> findByUid(String uid) {
        return projectConfigRepository.findByUidOrderByCreatedAtDesc(uid);
    }

    public ProductionProjectConfig findById(@NonNull Long id) {
        return projectConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("项目配置不存在"));
    }

    public ProductionProjectConfig create(String uid, String projectName, BigDecimal operationQuota, BigDecimal qualityQuota) {
        if (projectConfigRepository.existsByUidAndProjectName(uid, projectName)) {
            throw new RuntimeException("项目名称已存在");
        }
        
        ProductionProjectConfig config = new ProductionProjectConfig();
        config.setUid(uid);
        config.setProjectName(projectName);
        config.setOperationQuota(operationQuota);
        config.setQualityQuota(qualityQuota);
        return projectConfigRepository.save(config);
    }

    public ProductionProjectConfig update(@NonNull Long id, String projectName, BigDecimal operationQuota, BigDecimal qualityQuota) {
        ProductionProjectConfig config = projectConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("项目配置不存在"));
        
        config.setProjectName(projectName);
        config.setOperationQuota(operationQuota);
        config.setQualityQuota(qualityQuota);
        return projectConfigRepository.save(config);
    }

    @SuppressWarnings("null")
    public void delete(@NonNull Long id) {
        ProductionProjectConfig config = projectConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("项目配置不存在"));
        projectConfigRepository.delete(config);
    }

    public void deleteBatch(@NonNull List<Long> ids) {
        projectConfigRepository.deleteAllById(ids);
    }
}
