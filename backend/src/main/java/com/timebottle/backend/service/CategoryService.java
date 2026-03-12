package com.timebottle.backend.service;

import com.timebottle.backend.entity.BillCategory;
import com.timebottle.backend.repository.CategoryRepository;
import com.timebottle.backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private BillRepository billRepository;

    public List<BillCategory> findAll() {
        List<BillCategory> categories = categoryRepository.findByIsDeleted("0");
        categories.sort(Comparator.comparing(BillCategory::getSort));
        return categories;
    }

    public List<BillCategory> findAll(Integer userId) {
        List<BillCategory> categories = new ArrayList<>();
        categories.addAll(categoryRepository.findByUserIdAndIsDeleted(0, "0"));
        if (userId != null && userId > 0) {
            categories.addAll(categoryRepository.findByUserIdAndIsDeleted(userId, "0"));
        }
        categories.sort(Comparator.comparing(BillCategory::getSort));
        return categories;
    }

    public List<BillCategory> findByType(int type) {
        List<BillCategory> categories = categoryRepository.findByTypeAndIsDeleted(String.valueOf(type), "0");
        categories.sort(Comparator.comparing(BillCategory::getSort));
        return categories;
    }

    public List<BillCategory> findByTypeAndUserId(int type, Integer userId) {
        List<BillCategory> categories = new ArrayList<>();
        categories.addAll(categoryRepository.findByTypeAndUserIdAndIsDeleted(String.valueOf(type), 0, "0"));
        if (userId != null && userId > 0) {
            categories.addAll(categoryRepository.findByTypeAndUserIdAndIsDeleted(String.valueOf(type), userId, "0"));
        }
        categories.sort(Comparator.comparing(BillCategory::getSort));
        return categories;
    }

    public BillCategory create(String name, int type, int userId, int sort, boolean isDefault) {
        int effectiveUserId = isDefault ? 0 : userId;
        String typeStr = String.valueOf(type);
        
        Optional<BillCategory> deletedCategory = categoryRepository
                .findByNameAndTypeAndUserIdAndIsDeleted(name, typeStr, effectiveUserId, "1");
        
        if (deletedCategory.isPresent()) {
            BillCategory category = deletedCategory.get();
            category.setIsDefault(isDefault ? "1" : "0");
            category.setSort(sort);
            category.setIsDeleted("0");
            return categoryRepository.save(category);
        }
        
        BillCategory category = new BillCategory();
        category.setUserId(effectiveUserId);
        category.setName(name);
        category.setType(typeStr);
        category.setIsDefault(isDefault ? "1" : "0");
        category.setSort(sort);
        category.setIsDeleted("0");
        return categoryRepository.save(category);
    }

    public BillCategory update(int id, String name, int type, int sort, String userRole) {
        BillCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        if ("1".equals(category.getIsDefault()) && !"2".equals(userRole)) {
            throw new RuntimeException("系统默认分类不能修改");
        }
        
        category.setName(name);
        category.setType(String.valueOf(type));
        category.setSort(sort);
        return categoryRepository.save(category);
    }

    public void delete(int id, String userRole) {
        BillCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        if ("1".equals(category.getIsDefault()) && !"2".equals(userRole)) {
            throw new RuntimeException("系统默认分类不能删除");
        }
        
        // 检查是否有账单使用此分类
        if (billRepository.existsByCategoryIdAndIsDeleted(id, "0")) {
            throw new RuntimeException("该分类下存在账单，无法删除");
        }
        
        category.setIsDeleted("1");
        categoryRepository.save(category);
    }
}
