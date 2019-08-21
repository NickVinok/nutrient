package com.nutrient.nutrientSpring.Repos.NutrientRepository;

import com.nutrient.nutrientSpring.Model.NutrientModel.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface NutrientRepo extends JpaRepository<Nutrient, Long> {
    Optional<Nutrient> findByName(String name);
    Optional<List<Nutrient>> findByNameIn(Collection names);
}
