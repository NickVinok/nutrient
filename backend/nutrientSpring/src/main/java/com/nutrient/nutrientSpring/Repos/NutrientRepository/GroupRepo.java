package com.nutrient.nutrientSpring.Repos.NutrientRepository;

import com.nutrient.nutrientSpring.Model.NutrientModel.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface GroupRepo extends JpaRepository<Group, Long> {
    Group findByAgeStartGreaterThanEqualAndAgeEndLessThanEqualAndGenderAndIsPregnantAndIsFeeding
            (double age1, double age2, String gender, boolean isPregnant, boolean isFeeding);
}
