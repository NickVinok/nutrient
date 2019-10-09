package com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NutrientNorms {
    PfcNorms pfcNorms;
    Vitamin vitaminNorms;
    Acid acidNorms;
    Mineral mineralNorms;
}
