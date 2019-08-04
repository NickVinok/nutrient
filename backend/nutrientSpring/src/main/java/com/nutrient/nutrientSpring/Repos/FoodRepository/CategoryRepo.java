package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long>{
    List<Category> findByNameIn(Collection<String> names);
    List<Category> findByNameNotIn(Collection<String> names);
    Optional<Category> findByName(String name);
}
