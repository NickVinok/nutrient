package com.nutrient.nutrientSpring.Model.FoodModel;

import lombok.Data;

import java.math.BigInteger;

@Data
public class RecipeMock {
    private BigInteger id;
    private String name;
    private String products;
    private String weight;
}
