package com.nutrient.nutrientSpring.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HomeController {
    @GetMapping("/")
    public String index(){
        return "index";
    }
}
