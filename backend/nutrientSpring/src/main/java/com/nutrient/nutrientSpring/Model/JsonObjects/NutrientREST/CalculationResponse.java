package com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import lombok.Data;

@Data
public class CalculationResponse {
    private PfcNorms norms;
    private Combinations combinations;

    public PfcNorms getNorms(){
        return norms;
    }

    public Combinations combinations(){
        return combinations;
    }

    public void setNorms(PfcNorms p1){
        this.norms = p1;
    }

    public void setCombinations(Combinations s1){
        this.combinations = s1;
    }
}
