package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.Services.FoodService;
import com.nutrient.nutrientSpring.Utils.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestRecipes {
    @Autowired
    FoodService foodService;

    @GetMapping
    public List<Recipe> test(){
        return foodService.getAllRecipes();
    }
}
