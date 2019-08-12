package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.Model.JsonObjects.DietInfo;
import com.nutrient.nutrientSpring.Model.JsonObjects.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/calculate")
public class NutrientRestController {
    @Autowired
    Calculations calculations;

    @PostMapping
    public Response getCombinations(@RequestBody DietInfo dietInfo){
        Response r = new Response();
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
