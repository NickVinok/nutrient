package com.nutrient.nutrientSpring.Model.NutrientModel;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "NutrientType")
public class NutrientType {
    @Id
    private long id;
    private String name;
}
