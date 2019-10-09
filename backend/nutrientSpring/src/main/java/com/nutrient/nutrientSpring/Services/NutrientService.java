package com.nutrient.nutrientSpring.Services;

import com.nutrient.nutrientSpring.Model.NutrientModel.Nutrient;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutrientHasGender;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutritionCompositeKey;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.GenderRepo;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.NutrientHasGenderRepo;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.NutrientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NutrientService {
    @Autowired
    private GenderRepo genderRepo;

    @Autowired
    private NutrientRepo nutrientRepo;

    @Autowired
    private NutrientHasGenderRepo nutrientHasGenderRepo;

    private List<NutrientHasGender> vitaminNorms;
    private List<NutrientHasGender> mineralNorms;
    private List<NutrientHasGender> acidNorms;

    public void getNutrientsValueForGender(String gender) {
        Long id = genderRepo.findByName(gender).get().getId();
        List<NutrientHasGender> tmp = nutrientHasGenderRepo.findByNutritionCompositeKey_Gender(id);

        List<String> tmpMineralsNames = Stream.of("calcium ", "phosphorus", "magnesum ", "kalium ",
                "natrium ", "ferrum", "zincum ", "cuprum ", "manganum", "selenum", "fluorum")
                .collect(Collectors.toList());
        List<String> tmpVitaminNames = Stream.of("c", "b1", "b2", "b6", "b3","b12", "b9", "b5",
                "alpha_carotene","a", "beta_carotene", "e", "d", "k", "b4")
                .collect(Collectors.toList());
        List<String> tmpAcidNames = Stream.of("tryptophan","threonine","isoleucine","leucine","lysine",
                "methionine", "cystine", "phenylalanine","tyrosine","valine","arginine","histidine",
                "alanine","aspartic_acid","glutamic_acid","glycine","proline","serine")
                .collect(Collectors.toList());

        List<Long> vitaminIds = nutrientRepo.findByNameIn(tmpVitaminNames).get().stream()
                .map(Nutrient::getId)
                .collect(Collectors.toList());
        List<Long> mineralIds =  nutrientRepo.findByNameIn(tmpMineralsNames).get().stream()
                .map(Nutrient::getId)
                .collect(Collectors.toList());
        List<Long> acidIds =  nutrientRepo.findByNameIn(tmpAcidNames).get().stream()
                .map(Nutrient::getId)
                .collect(Collectors.toList());

        vitaminNorms = nutrientHasGenderRepo.findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(id,vitaminIds);
        mineralNorms = nutrientHasGenderRepo.findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(id,mineralIds);
        acidNorms = nutrientHasGenderRepo.findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(id,acidIds);
    }

    public Float getValueOfCertainNutrient(String nutrientName, String gender) {
        NutritionCompositeKey key = new NutritionCompositeKey(nutrientRepo.findByName(nutrientName).get().getId(),
                genderRepo.findByName(gender).get().getId());
        return nutrientHasGenderRepo.findById(key).get().getValue();
    }

    public Float getMineralsSum(String gender, List<Long> ids) {
        List<NutrientHasGender> tmp = nutrientHasGenderRepo.findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(
                genderRepo.findByName(gender).get().getId(), ids);
        Float sum =  0f;
        for (NutrientHasGender mineral : tmp) {
            sum += mineral.getValue();
        }
        return sum;
    }

    public List<NutrientHasGender> getVitaminNorms() {
        return vitaminNorms;
    }

    public List<NutrientHasGender> getMineralNorms() {
        return mineralNorms;
    }

    public List<NutrientHasGender> getAcidNorms() {
        return acidNorms;
    }
}
