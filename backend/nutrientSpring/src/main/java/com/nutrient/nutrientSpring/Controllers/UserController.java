package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.Model.FoodModel.User;
import com.nutrient.nutrientSpring.Model.JsonObjects.UserInfo;
import com.nutrient.nutrientSpring.Repos.FoodRepository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/users")
public class UserController {
    @Autowired()
    private UserRepo userRepo;

    @PostMapping("create")
    public @ResponseBody String create(@RequestBody UserInfo usrInfo){
        User user = new User();
        user.setPassword(usrInfo.getPassword());
        user.setEmail(usrInfo.getEmail());
        user.setLogin(usrInfo.getLogin());
        user.setName(usrInfo.getName());
        userRepo.save(user);
        return user.getPassword();
    }

    @GetMapping("all")
    public @ResponseBody Iterable<User> getAllUsers() {
        System.out.println("user");
        return userRepo.findAll();
    }
}
