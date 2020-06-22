package com.nutrient.nutrientSpring.JsonObjects.RationRest;

import com.nutrient.nutrientSpring.JsonObjects.DietInfo;
import lombok.Data;

@Data
public class RationRequest {
    DietInfo dietInfo;
    int days;
    int meals;
}
