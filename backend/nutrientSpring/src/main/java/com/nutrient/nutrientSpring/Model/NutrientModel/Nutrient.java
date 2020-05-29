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
    private boolean isToxic;
    private double lethalDose;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nutrientTypeId", referencedColumnName = "id")
    private NutrientType nutrientType;
}
