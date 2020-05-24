package com.nutrient.nutrientSpring.Model.FoodModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "NormGroup")
@AllArgsConstructor
@NoArgsConstructor
public class NormGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String gender;
    private double minAge;
    private double maxAge;

    private boolean isPregnant;
    private boolean isFeeding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodId", referencedColumnName = "id")
    private Food food;
}
