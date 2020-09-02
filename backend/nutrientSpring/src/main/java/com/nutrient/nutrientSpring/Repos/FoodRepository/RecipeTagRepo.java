package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.Keys.RecipeTagKey;
import com.nutrient.nutrientSpring.Model.FoodModel.RecipeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeTagRepo extends JpaRepository<RecipeTag, RecipeTagKey> {
    List<RecipeTag> findByRecipeTagKey_TagsId(long tagId);
}
