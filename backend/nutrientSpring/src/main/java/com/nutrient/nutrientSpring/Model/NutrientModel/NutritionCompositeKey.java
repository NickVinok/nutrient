package com.nutrient.nutrientSpring.Model.NutrientModel;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class NutritionCompositeKey implements Serializable {
    @Column(name = "nutrient")
    private Long nutrient;
    @Column(name = "gender")
    private Long gender;

    public NutritionCompositeKey(Long nutrient, Long gender){
        this.gender = gender;
        this.nutrient = nutrient;
    }
}
