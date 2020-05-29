package com.nutrient.nutrientSpring.Services;

import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import com.nutrient.nutrientSpring.Model.NutrientModel.GroupsNutrients;
import com.nutrient.nutrientSpring.Model.NutrientModel.Nutrient;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.GroupRepo;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.GroupsNutrientsRepo;
import com.nutrient.nutrientSpring.Repos.NutrientRepository.NutrientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NutrientService {
    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private NutrientRepo nutrientRepo;

    @Autowired
    private GroupsNutrientsRepo groupsNutrientsRepo;

    private Vitamin vitaminNorms;
    private Mineral mineralNorms;
    private Acid acidNorms;
    private Float ashNorm;

    public void getNutrientsValueForGender(String gender, double age, boolean isPregnant, boolean isFeeding) {
        if(gender.equals("Male")) {
            gender="М";
        }else{
            gender="Ж";
        }
        long groupId = groupRepo.findByAgeStartLessThanEqualAndAgeEndGreaterThanEqualAndGenderAndIsPregnantAndIsFeeding
                (age, age, gender, isPregnant, isFeeding).getId();

        /*List<String> tmpMineralsNames = Stream.of("calcium ", "phosphorus", "magnesum ", "kalium ",
                "natrium ", "ferrum", "zincum ", "cuprum ", "manganum", "selenum", "fluorum")
                .collect(Collectors.toList());
        List<String> tmpVitaminNames = Stream.of("c", "b1", "b2", "b6", "b3","b12", "b9", "b5",
                "alpha_carotene","a", "beta_carotene", "e", "d", "k", "b4")
                .collect(Collectors.toList());
        List<String> tmpAcidNames = Stream.of("tryptophan","threonine","isoleucine","leucine","lysine",
                "methionine", "cystine", "phenylalanine","tyrosine","valine","arginine","histidine",
                "alanine","aspartic_acid","glutamic_acid","glycine","proline","serine","omega_3", "omega_6", "omega_9")
                .collect(Collectors.toList());*/

        List<Long> vitaminIds = nutrientRepo.findByNutrientType_Id(2)
                .stream().map(Nutrient::getId).collect(Collectors.toList());
        List<Long> mineralIds =  nutrientRepo.findByNutrientType_Id(3)
                .stream().map(Nutrient::getId).collect(Collectors.toList());
        List<Long> acidIds =  nutrientRepo.findByNutrientType_Id(4)
                .stream().map(Nutrient::getId).collect(Collectors.toList());

        acidNorms = new Acid(
                groupsNutrientsRepo.findByGroupsNutrientsCompositeKey_groupIdAndGroupsNutrientsCompositeKey_nutrientIdIn
                        (groupId, acidIds)
                        .stream()
                        .map(GroupsNutrients::getValue)
                        .collect(Collectors.toList()));
        vitaminNorms = new Vitamin(
                groupsNutrientsRepo.findByGroupsNutrientsCompositeKey_groupIdAndGroupsNutrientsCompositeKey_nutrientIdIn
                        (groupId, vitaminIds)
                        .stream()
                        .map(GroupsNutrients::getValue)
                        .collect(Collectors.toList()));
        mineralNorms = new Mineral(
                groupsNutrientsRepo.findByGroupsNutrientsCompositeKey_groupIdAndGroupsNutrientsCompositeKey_nutrientIdIn
                        (groupId, mineralIds)
                        .stream()
                        .map(GroupsNutrients::getValue)
                        .collect(Collectors.toList()));
        this.ashNorm = (float)mineralNorms.calculateAsh();
    }

    public Vitamin getVitaminNorms() { return vitaminNorms; }

    public Mineral getMineralNorms() {
        return mineralNorms;
    }

    public Acid getAcidNorms() {
        return acidNorms;
    }

    public Float getAshNorm() { return this.ashNorm; }
}
