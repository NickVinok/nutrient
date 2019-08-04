package com.nutrient.nutrientSpring.Repos.NutrientRepository;

import com.nutrient.nutrientSpring.Model.NutrientModel.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientRepo extends JpaRepository<Nutrient, Long> {
}
