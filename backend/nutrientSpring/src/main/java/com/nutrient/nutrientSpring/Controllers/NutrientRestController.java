package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.DietInfo;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.CalculationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/calculate")
public class NutrientRestController {
    @Autowired
    Calculations calculations;

    @PostMapping
    public CalculationResponse getCombinations(@RequestBody DietInfo dietInfo){
        CalculationResponse r = new CalculationResponse();
        r.setCombinations(calculations.getEfficientCombinations(
                dietInfo.getGender(),
                dietInfo.getWorkingGroup(),
                dietInfo.getAge(),
                dietInfo.getMass(),
                dietInfo.getHeight(),
                dietInfo.getDiet_type(),
                dietInfo.getDietRestrictions()
                ));
        r.setNorms(calculations.getPfcNorms());
        return r;
    }
}
