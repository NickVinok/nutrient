package com.nutrient.nutrientSpring.Utils;

import lombok.Data;

import java.util.List;

@Data
public class Recipe {
    private long id;
    private String name;
    private List<Long> ingredientIds;
    private List<Double> ingredientWeights;

    public Recipe(long id, String name, List<Long> ingredientIds, List<Double> ingredientWeights){
        this.id=id;
        this.name=name;
        this.ingredientIds=ingredientIds;
        this.ingredientWeights=ingredientWeights;
    }
}
