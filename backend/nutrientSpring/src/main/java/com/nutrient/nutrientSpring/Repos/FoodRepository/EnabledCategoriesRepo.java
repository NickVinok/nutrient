package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.DietTypes;
import com.nutrient.nutrientSpring.Model.FoodModel.EnabledCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnabledCategoriesRepo extends JpaRepository<EnabledCategories, Long> {
    List<EnabledCategories> findByDietAndEnabled(DietTypes diet, boolean enabled);
}
