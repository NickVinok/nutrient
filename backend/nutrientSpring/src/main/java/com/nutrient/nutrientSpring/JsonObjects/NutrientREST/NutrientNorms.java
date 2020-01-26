package com.nutrient.nutrientSpring.JsonObjects.NutrientREST;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NutrientNorms {
    private PfcNorms pfcNorms;
    private Vitamin vitaminNorms;
    private Acid acidNorms;
    private Mineral mineralNorms;
}
