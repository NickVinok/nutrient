package com.nutrient.nutrientSpring.Model.NutrientModel;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Nutrient")
public class Nutrient {
    @Id
    @Column(name = "id")
    private Long id;
    private String name;
}
