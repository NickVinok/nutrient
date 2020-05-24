package com.nutrient.nutrientSpring.Services;

import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutrientHasGender;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutritionCompositeKey;
import com.nutrient.nutrientSpring.Repos.FoodRepository.*;
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
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private AcidsRepo acidsRepo;
    @Autowired
    private MineralRepo mineralRepo;
    @Autowired
    private VitaminRepo vitaminRepo;
    @Autowired
    private NormGroupRepo normGroupRepo;

    private Vitamin vitaminNorms;
    private Mineral mineralNorms;
    private Acid acidNorms;

    public void getNutrientsValueForGender(String gender, double age, boolean isPregnant, boolean isFeeding) {
        /*Long id = genderRepo.findByName(gender).get().getId();
        List<NutrientHasGender> tmp = nutrientHasGenderRepo.findByNutritionCompositeKey_Gender(id);

        List<String> tmpMineralsNames = Stream.of("calcium ", "phosphorus", "magnesum ", "kalium ",
                "natrium ", "ferrum", "zincum ", "cuprum ", "manganum", "selenum", "fluorum")
                .collect(Collectors.toList());
        List<String> tmpVitaminNames = Stream.of("c", "b1", "b2", "b6", "b3","b12", "b9", "b5",
                "alpha_carotene","a", "beta_carotene", "e", "d", "k", "b4")
                .collect(Collectors.toList());
        List<String> tmpAcidNames = Stream.of("tryptophan","threonine","isoleucine","leucine","lysine",
                "methionine", "cystine", "phenylalanine","tyrosine","valine","arginine","histidine",
                "alanine","aspartic_acid","glutamic_acid","glycine","proline","serine","omega_3", "omega_6", "omega_9")
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

        acidNorms = new Acid(nutrientHasGenderRepo.findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(id,acidIds)
                .stream()
                .map(NutrientHasGender::getValue)
                .collect(Collectors.toList()));
        vitaminNorms = new Vitamin(nutrientHasGenderRepo.findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(id,vitaminIds)
                .stream()
                .map(NutrientHasGender::getValue)
                .collect(Collectors.toList()));
        mineralNorms = new Mineral(nutrientHasGenderRepo.findByNutritionCompositeKey_GenderAndNutritionCompositeKey_NutrientIn(id,mineralIds)
                .stream()
                .map(NutrientHasGender::getValue)
                .collect(Collectors.toList()));*/
        if(gender=="Male"){
            gender="М";
        } else{
            gender="Ж";
        }

        long normId = normGroupRepo.findByGenderAndMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndIsPregnantAndIsFeeding(
                gender, age, age, isPregnant, isFeeding).getFood().getId();
        vitaminNorms = vitaminRepo.findByFood_id(normId).get();
        mineralNorms = mineralRepo.findByFood_id(normId).get();
        acidNorms = acidsRepo.findByFood_id(normId).get();
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

    public Vitamin getVitaminNorms() { return vitaminNorms; }

    public Mineral getMineralNorms() {
        return mineralNorms;
    }

    public Acid getAcidNorms() {
        return acidNorms;
    }
}
