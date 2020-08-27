package com.nutrient.nutrientSpring.Model.FoodModel;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "tags")
public class Tags {
    @Id
    private long id;
    private String name;
}
