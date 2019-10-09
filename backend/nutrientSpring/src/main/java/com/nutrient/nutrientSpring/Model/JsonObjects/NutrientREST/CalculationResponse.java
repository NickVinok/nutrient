package com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import lombok.Data;

import java.util.List;

@Data
public class CalculationResponse {
    private NutrientNorms norms;
    private List<Combination> combinations;
}
