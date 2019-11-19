package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.DietInfo;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.CalculationResponse;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.NutrientNorms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/calculate")
public class NutrientRestController {
    @Autowired
    Calculations calculations;

    @PostMapping
    public CalculationResponse postCombinations(@RequestBody DietInfo dietInfo){
        CalculationResponse r = new CalculationResponse();
        r.setCombinations(calculations.getEfficientCombinations(
                dietInfo.getGender(),
                dietInfo.getWorkingGroup(),
                dietInfo.getAge(),
                dietInfo.getMass(),
                dietInfo.getHeight(),
                dietInfo.getDiet_type(),
                dietInfo.getDietRestrictions(),
                dietInfo.isPregnancy()
                ).getCombinationList());
        r.setNorms(new NutrientNorms(calculations.getPfcNorms(),
                calculations.getVitaminNorms(), calculations.getAcidNorms(), calculations.getMineralNorms()));
        return r;
    }

}
