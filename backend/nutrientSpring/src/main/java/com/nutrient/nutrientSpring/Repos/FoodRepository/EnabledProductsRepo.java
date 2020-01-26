package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.DietTypes;
import com.nutrient.nutrientSpring.Model.FoodModel.EnabledProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnabledProductsRepo extends JpaRepository<EnabledProducts, Long> {
    List<EnabledProducts> findByDietAndEnable(DietTypes diet, boolean enabled);
}
