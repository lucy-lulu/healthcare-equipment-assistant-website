package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.models.Category;
import com.unimelbproject.healthcareequipmentassistant.repositories.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepository;

    public List<Category> getTopLevelCategories() {
        return categoryRepository.findByParentIdIsNull();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getSubcategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }
}
