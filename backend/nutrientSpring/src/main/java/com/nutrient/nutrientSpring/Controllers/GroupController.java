package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.Model.FoodModel.Category;
import com.nutrient.nutrientSpring.Services.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/getGroups")
public class GroupController {
    @Autowired
    FoodService foodService;

    @GetMapping
    public List<Category> getCategories(){
        List<Category> tmp = foodService.getCategories();
        return tmp;
    }
}
