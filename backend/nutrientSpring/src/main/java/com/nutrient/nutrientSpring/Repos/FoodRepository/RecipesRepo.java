package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.RecipeMock;
import com.nutrient.nutrientSpring.Model.FoodModel.Recipes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface RecipesRepo extends JpaRepository<Recipes, Long> {
    @Query(value="select id, name, REPLACE(REPLACE(JSON_EXTRACT(products, '$[*].product_id'),'[','' ), ']', '') as products, REPLACE(REPLACE(JSON_EXTRACT(products, '$[*].weight_source'),'[','' ), ']', '') as weight from recipes",
            nativeQuery = true)
    List<Object[]> extractAvailableRecipes();
    List<Recipes> findByIdIn(Collection<Long> ids);
}
