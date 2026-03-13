package com.timebottle.backend.service;

import com.timebottle.backend.entity.ProductionProjectConfig;
import com.timebottle.backend.repository.ProductionProjectConfigRepository;
import com.timebottle.backend.repository.DailyPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Service
public class ProductionProjectConfigService {

    @Autowired
    private ProductionProjectConfigRepository projectConfigRepository;

    @Autowired
    private DailyPerformanceRepository dailyPerformanceRepository;

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
        
        if (dailyPerformanceRepository.existsByProjectId(id)) {
            throw new RuntimeException("该项目已被工作汇报使用，无法删除");
        }
        
        projectConfigRepository.delete(config);
    }

    public String deleteBatch(@NonNull List<Long> ids) {
        List<String> usedProjects = new ArrayList<>();
        
        for (Long id : ids) {
            if (id != null && dailyPerformanceRepository.existsByProjectId(id)) {
                ProductionProjectConfig config = projectConfigRepository.findById(id).orElse(null);
                if (config != null) {
                    usedProjects.add(config.getProjectName());
                }
            }
        }
        
        if (!usedProjects.isEmpty()) {
            throw new RuntimeException("以下项目已被工作汇报使用，无法删除：" + String.join("、", usedProjects));
        }
        
        projectConfigRepository.deleteAllById(ids);
        return "批量删除成功";
    }
}
