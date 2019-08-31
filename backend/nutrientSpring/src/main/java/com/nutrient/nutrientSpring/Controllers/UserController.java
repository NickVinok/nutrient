package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.Model.FoodModel.User;
import com.nutrient.nutrientSpring.Model.JsonObjects.UserInfo;
import com.nutrient.nutrientSpring.Repos.FoodRepository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepo userRepo;

    @PostMapping("/auth")
    public User auth(@RequestBody UserInfo usrInfo, Principal principal){
        User user = new User();
        user.setPassword(usrInfo.getPassword());
        user.setLogin(usrInfo.getLogin());
        String encoded=new BCryptPasswordEncoder().encode("test");
        System.out.println(encoded);
        System.out.println("in  auth");
        return user;
    }
    @GetMapping("/")
    List<User> findAll() {
        return userRepo.findAll();
    }

    @GetMapping("{id}")
    Optional<User> findOne(@PathVariable Integer id) {
        return userRepo.findById(id);
    }

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
}
