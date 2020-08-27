package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.DietTypesTags;
import com.nutrient.nutrientSpring.Model.FoodModel.Keys.DietTypesTagsKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietTypesTagsRepo extends JpaRepository<DietTypesTags, DietTypesTagsKey> {
    List<DietTypesTags> findByDietTypes(long dietType);
}
