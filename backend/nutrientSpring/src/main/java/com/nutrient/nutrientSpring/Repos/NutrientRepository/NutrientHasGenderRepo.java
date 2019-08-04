package com.nutrient.nutrientSpring.Repos.NutrientRepository;


import com.nutrient.nutrientSpring.Model.NutrientModel.NutrientHasGender;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutritionCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutrientHasGenderRepo extends JpaRepository<NutrientHasGender, NutritionCompositeKey> {
    List<NutrientHasGender> findByNutritionCompositeKey_Gender(Long gender);
    List<NutrientHasGender> findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(Long gender, List<Long> nutrients);
}
