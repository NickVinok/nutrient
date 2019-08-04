package com.nutrient.nutrientSpring.Model.NutrientModel;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "Nutrient_has_Gender")
@Data
public class NutrientHasGender {
    @EmbeddedId
    private NutritionCompositeKey nutritionCompositeKey;

    @ManyToOne
    @MapsId("gender")
    @JoinColumn(name = "gender")
    private Gender gender;

    @ManyToOne
    @MapsId("nutrient")
    @JoinColumn(name = "nutrient")
    private Nutrient nutrient;

    private float value;
}
