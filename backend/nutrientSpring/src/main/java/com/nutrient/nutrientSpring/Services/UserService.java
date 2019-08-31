package com.nutrient.nutrientSpring.Services;

import com.nutrient.nutrientSpring.Model.FoodModel.User;

public interface UserService {
    User getUser(String login);
}
