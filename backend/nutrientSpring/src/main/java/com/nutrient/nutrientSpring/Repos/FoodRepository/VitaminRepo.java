package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VitaminRepo extends JpaRepository<Vitamin, Long>{
    Optional<Vitamin> findByFood_id(Long food_id);
}
