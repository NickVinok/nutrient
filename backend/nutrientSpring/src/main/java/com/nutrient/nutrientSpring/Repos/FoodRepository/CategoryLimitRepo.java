package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.CategoryLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CategoryLimitRepo extends JpaRepository<CategoryLimit, Long> {
    List<CategoryLimit> findByCategory_idIn(Collection<Long> ids);
}
