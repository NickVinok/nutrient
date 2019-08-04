package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcidsRepo extends JpaRepository<Acid, Long> {
    Optional<Acid> findByFood_id(Long food_id);
}
