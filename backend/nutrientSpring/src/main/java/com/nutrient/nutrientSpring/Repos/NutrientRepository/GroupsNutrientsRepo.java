package com.nutrient.nutrientSpring.Repos.NutrientRepository;

import com.nutrient.nutrientSpring.Model.NutrientModel.GroupsNutrients;
import com.nutrient.nutrientSpring.Model.NutrientModel.GroupsNutrientsCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface GroupsNutrientsRepo extends JpaRepository<GroupsNutrients, GroupsNutrientsCompositeKey> {
    ArrayList<GroupsNutrients> findByGroupsNutrientsCompositeKey_groupIdAndGroupsNutrientsCompositeKey_nutrientIdIn
            (long groupId, List<Long> nutrients);
}
