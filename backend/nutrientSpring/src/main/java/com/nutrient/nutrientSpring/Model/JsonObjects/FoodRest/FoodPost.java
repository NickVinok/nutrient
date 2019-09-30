package com.nutrient.nutrientSpring.Model.JsonObjects.FoodRest;

import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.DietInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class FoodPost {
    DietInfo dietInfo;
    ArrayList<HashMap<String, Long>> idsWithGrams;
}
