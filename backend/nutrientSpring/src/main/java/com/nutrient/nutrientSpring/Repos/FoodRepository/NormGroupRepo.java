package com.nutrient.nutrientSpring.Repos.FoodRepository;

import com.nutrient.nutrientSpring.Model.FoodModel.NormGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormGroupRepo extends JpaRepository<NormGroup, Long> {
    NormGroup findByGenderAndMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndIsPregnantAndIsFeeding(
            String gender, double age1, double age2, boolean isPregnant, boolean isFeeding);
}
