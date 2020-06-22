package com.nutrient.nutrientSpring.JsonObjects.RationRest;

import com.nutrient.nutrientSpring.JsonObjects.NutrientNorms;
import com.nutrient.nutrientSpring.Utils.Ration.Ration;
import lombok.Data;

import java.util.List;

@Data
public class RationResponse {
    NutrientNorms nutrientNorms;
    List<Ration> ration;
}
