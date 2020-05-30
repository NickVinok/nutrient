package com.nutrient.nutrientSpring.Repos.NutrientRepository;

import com.nutrient.nutrientSpring.Model.NutrientModel.NormGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepo extends JpaRepository<NormGroups, Long> {
    NormGroups findByAgeStartLessThanEqualAndAgeEndGreaterThanEqualAndGenderAndIsPregnantAndIsFeeding
            (double age1, double age2, String gender, boolean isPregnant, boolean isFeeding);
}
