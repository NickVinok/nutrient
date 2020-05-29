package com.nutrient.nutrientSpring.Model.NutrientModel;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "NormGroups")
public class Group {
    @Id
    private long id;
    private String name;
    private String gender;
    private double ageStart;
    private double ageEnd;
    private boolean isFeeding;
    private boolean isPregnant;
}
