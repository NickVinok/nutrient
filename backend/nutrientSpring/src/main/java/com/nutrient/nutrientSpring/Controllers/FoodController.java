package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.Model.JsonObjects.FoodRest.PackedJsonObject;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.CalculationResponse;
import com.nutrient.nutrientSpring.Services.PackedFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
