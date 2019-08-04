package com.nutrient.nutrientSpring.Repos.FoodRepository;


import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MineralRepo extends JpaRepository<Mineral, Long>{
    Optional<Mineral> findByFood_id(Long food_id);
}
