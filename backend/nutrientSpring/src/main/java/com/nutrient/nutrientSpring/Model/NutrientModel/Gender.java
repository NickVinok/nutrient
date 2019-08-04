package com.nutrient.nutrientSpring.Model.NutrientModel;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Gender")
public class Gender {
    @Id
    @Column(name = "id")
    private Long id;
    private String name;
}
