package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.JsonObjects.DietInfo;
import com.nutrient.nutrientSpring.JsonObjects.FoodRest.FoodPost;
import com.nutrient.nutrientSpring.JsonObjects.FoodRest.PackedJsonObject;
import com.nutrient.nutrientSpring.JsonObjects.NutrientNorms;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.*;
import com.nutrient.nutrientSpring.Services.PackedFoodService;
import com.nutrient.nutrientSpring.Utils.Combination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    @Autowired
    Calculations calculations;

    @Autowired
    PackedFoodService packedFoodService;

    @GetMapping
    public List<PackedJsonObject> getFood(){
        return packedFoodService.getPackedFood();
    }

    @PostMapping
    public CalculationResponse postFood(@RequestBody FoodPost post){
        CalculationResponse r = new CalculationResponse();
        DietInfo dietInfo = post.getDietInfo();
        List<Combination> calculationResult = new ArrayList<>();
        calculationResult.add(calculations.calculateCustomCombination(
                dietInfo.getGender(),
                dietInfo.getWorkingGroup(),
                dietInfo.getAge(),
                dietInfo.getMass(),
                dietInfo.getHeight(),
                dietInfo.getDiet_type(),
                dietInfo.getDietRestrictions(),
                dietInfo.isPregnancy(),
                post.getIdsWithGrams()));

        r.setCombinations(calculationResult);
        r.setNorms(new NutrientNorms(calculations.getPfcNorms(),
                calculations.getVitaminNorms(), calculations.getAcidNorms(), calculations.getMineralNorms()));
        return r;
    }
}
