package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.FoodLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FoodLimitRepo extends JpaRepository<FoodLimit, Long> {
    List<FoodLimit> findByFood_IdIn(Collection<Long> ids);
}
