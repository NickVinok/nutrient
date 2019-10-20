package com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class Combinations {
    private List<Combination> combinationList = new ArrayList<>();
    public void addCombination(Combination c){
        combinationList.add(c);
    }
    private HashMap<Long, Long> overallCategoryCounter;
}
