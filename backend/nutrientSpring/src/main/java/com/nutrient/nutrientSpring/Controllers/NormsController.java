package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.DietInfo;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.NutrientNorms;
import com.nutrient.nutrientSpring.Utils.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/norms")
public class NormsController {
    @Autowired
    Calculations calculations;

    @PostMapping
    public NutrientNorms postNormsForPerson(@RequestBody DietInfo dietInfo){

       calculations.calculateNormsForPerson
                (
                        dietInfo.getGender(),
                        dietInfo.getWorkingGroup(),
                        dietInfo.getAge(),
                        dietInfo.getMass(),
                        dietInfo.getHeight(),
                        dietInfo.getDiet_type(),
                        dietInfo.getDietRestrictions(),
                        dietInfo.isPregnancy()
                );

        return new NutrientNorms(calculations.getPfcNorms(), calculations.getVitaminNorms(),
                calculations.getAcidNorms(), calculations.getMineralNorms());
    }
}
