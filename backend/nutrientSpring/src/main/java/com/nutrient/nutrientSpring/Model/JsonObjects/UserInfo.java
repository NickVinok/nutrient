package com.nutrient.nutrientSpring.Model.JsonObjects;

import lombok.Data;

@Data
public class UserInfo {
    private Integer id;
    private String login;
    private String password;
    private String name;
    private String email;
}