package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.Model.JsonObjects.FoodRest.FoodPost;
import com.nutrient.nutrientSpring.Model.JsonObjects.FoodRest.PackedJsonObject;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.CalculationResponse;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.Combination;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.Combinations;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.DietInfo;
import com.nutrient.nutrientSpring.Services.PackedFoodService;
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
                post.getIdsWithGrams()));

        r.setCombinations(calculationResult);
        r.setNorms(calculations.getPfcNorms());
        return r;
    }
}
