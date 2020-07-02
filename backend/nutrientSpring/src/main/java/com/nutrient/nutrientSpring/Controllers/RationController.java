package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.JsonObjects.DietInfo;
import com.nutrient.nutrientSpring.JsonObjects.NutrientNorms;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.CalculationResponse;
import com.nutrient.nutrientSpring.JsonObjects.RationRest.RationRequest;
import com.nutrient.nutrientSpring.JsonObjects.RationRest.RationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generate/ration")
public class RationController {
    @Autowired
    Calculations calculations;

    @PostMapping
    public RationResponse postRation(@RequestBody RationRequest rr){
        DietInfo dietInfo = rr.getDietInfo();
        RationResponse rationResponse = new RationResponse();
        rationResponse.setRation(calculations.calculateRationForPerson(
                        dietInfo.getGender(),
                        dietInfo.getWorkingGroup(),
                        dietInfo.getAge(),
                        dietInfo.getMass(),
                        dietInfo.getHeight(),
                        dietInfo.getDiet_type(),
                        dietInfo.getDietRestrictions(),
                        dietInfo.isPregnancy(),
                        rr.getDays(),
                        rr.getMeals()
                ));
        rationResponse.setNutrientNorms(new NutrientNorms(calculations.getPfcNorms(),
                calculations.getVitaminNorms(), calculations.getAcidNorms(), calculations.getMineralNorms()));
        rationResponse.calculateRationAvg();
        return rationResponse;
    }
}
