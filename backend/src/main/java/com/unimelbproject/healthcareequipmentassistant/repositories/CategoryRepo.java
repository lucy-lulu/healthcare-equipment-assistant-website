package com.unimelbproject.healthcareequipmentassistant.repositories;

import com.unimelbproject.healthcareequipmentassistant.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    List<Category> findByParentIdIsNull();  //get top level categories

    List<Category> findAll(); //get all categories

    List<Category> findByParentId(Long parentId); //get categories list under the parent category
}

