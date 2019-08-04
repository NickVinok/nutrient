package com.nutrient.nutrientSpring.Services;

import com.nutrient.nutrientSpring.Model.NutrientModel.NutrientHasGender;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutritionCompositeKey;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.GenderRepo;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.NutrientHasGenderRepo;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.NutrientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NutrientService {
    @Autowired
    private GenderRepo genderRepo;

    @Autowired
    private NutrientRepo nutrientRepo;

    @Autowired
    private NutrientHasGenderRepo nutrientHasGenderRepo;

    public List<NutrientHasGender> getNutrientsValueForGender(String gender){
        Long id = genderRepo.findByName(gender).get().getId();
        return nutrientHasGenderRepo.findByNutritionCompositeKey_Gender(id);
    }

    public Float getValueOfCertainMineral(Long id, String gender){
        return nutrientHasGenderRepo.findById(
                new NutritionCompositeKey(genderRepo.findByName(gender).get().getId(), id))
                .get().getValue();
    }

    public Float getMineralsSum(String gender, List<Long> ids){
        List<NutrientHasGender> tmp = nutrientHasGenderRepo.findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(
                genderRepo.findByName(gender).get().getId(), ids);
        Float sum = new Float(0);
        for(NutrientHasGender mineral : tmp){
            sum+=mineral.getValue();
        }
        return sum;
    }
}
