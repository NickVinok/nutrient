package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.FoodTag;
import com.nutrient.nutrientSpring.Model.FoodModel.Keys.FoodTagKey;
import com.nutrient.nutrientSpring.Model.FoodModel.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FoodTagRepo extends JpaRepository<FoodTag, FoodTagKey> {
    List<FoodTag> findByTagsIn(Collection<Tags> tags);
}
