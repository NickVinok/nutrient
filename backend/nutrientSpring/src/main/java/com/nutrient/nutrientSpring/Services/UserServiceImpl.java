package com.nutrient.nutrientSpring.Services;

import org.springframework.stereotype.Service;
import com.nutrient.nutrientSpring.Model.FoodModel.User;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(String login){
        User user = new User();
        user.setLogin(login);
        user.setPassword("af41e68e1309fa29a5044cbdc36b90a3821d8807e68c7675a6c495112bc8a55f");
        return  user;
    }
}
