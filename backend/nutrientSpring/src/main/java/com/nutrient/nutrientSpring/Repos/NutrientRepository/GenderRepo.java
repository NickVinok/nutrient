package com.nutrient.nutrientSpring.Repos.NutrientRepository;

import com.nutrient.nutrientSpring.Model.NutrientModel.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenderRepo extends JpaRepository<Gender, Long> {
    Optional<Gender> findByName(String name);
}
