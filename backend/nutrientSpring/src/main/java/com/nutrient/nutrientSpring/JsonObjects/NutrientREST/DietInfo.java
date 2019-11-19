package com.nutrient.nutrientSpring.JsonObjects.NutrientREST;

import lombok.Data;

@Data
public class DietInfo {
    private float height;
    private float mass;
    private String gender;
    private float age;
    private String diet_type;
    private String group;
    private int workingGroup;
    private int dietRestrictions;
    private boolean pregnancy;
}
