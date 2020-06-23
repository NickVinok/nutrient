package com.nutrient.nutrientSpring.Repos.FoodRepository;


import com.nutrient.nutrientSpring.Model.FoodModel.Category;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.RecipeMock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long>{
    //Убираем всю еду, которая не подпадает под подходящие для комбинирования категории
    List<Food> findByIdIn(Collection<Long> ids);
    List<Food> findByIdInAndCategory_IdIn(Collection<Long> ids, Collection<Long> categories);
}
