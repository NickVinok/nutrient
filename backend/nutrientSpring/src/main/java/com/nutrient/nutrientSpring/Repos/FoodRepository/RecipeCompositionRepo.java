package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.RecipesComposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeCompositionRepo extends JpaRepository<RecipesComposition, Long> {
    @Query(value="from recipes_composition where recipe_id in (Select id from recipes where dish_type=?1) order by coef_for_men desc limit 136")
    List<RecipesComposition> getBestRecipesOfType(long dishId);
}
