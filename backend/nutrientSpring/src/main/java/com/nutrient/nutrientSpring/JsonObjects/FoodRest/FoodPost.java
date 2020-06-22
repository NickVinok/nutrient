package com.nutrient.nutrientSpring.JsonObjects.FoodRest;

import com.nutrient.nutrientSpring.JsonObjects.DietInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class FoodPost {
    DietInfo dietInfo;
    ArrayList<HashMap<String, Long>> idsWithGrams;
}
