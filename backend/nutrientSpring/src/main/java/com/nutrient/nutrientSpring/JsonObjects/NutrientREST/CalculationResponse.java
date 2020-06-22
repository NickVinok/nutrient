package com.nutrient.nutrientSpring.JsonObjects.NutrientREST;

import com.nutrient.nutrientSpring.JsonObjects.NutrientNorms;
import com.nutrient.nutrientSpring.Utils.Combination;
import lombok.Data;

import java.util.List;

@Data
public class CalculationResponse {
    private NutrientNorms norms;
    private List<Combination> combinations;
}
