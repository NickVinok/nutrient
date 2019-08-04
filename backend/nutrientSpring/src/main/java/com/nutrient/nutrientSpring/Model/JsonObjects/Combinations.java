package com.nutrient.nutrientSpring.Model.JsonObjects;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Combinations {
    private List<Combination> combinationList = new ArrayList<>();
    public void addCombination(Combination c){
        combinationList.add(c);
    }
}
