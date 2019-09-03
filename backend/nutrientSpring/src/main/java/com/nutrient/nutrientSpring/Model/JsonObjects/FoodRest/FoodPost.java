package com.nutrient.nutrientSpring.Model.JsonObjects.FoodRest;

import com.nutrient.nutrientSpring.Model.JsonObjects.DietInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class FoodPost {
    DietInfo dietInfo;
    List<HashMap<String, Integer>> idsWithGrams;
}
