package com.nutrient.nutrientSpring.Model.NutrientModel;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class GroupsNutrientsCompositeKey implements Serializable {
    @Column(name = "idGroup")
    private long groupId;
    @Column(name = "idNutrient")
    private long nutrientId;

    public GroupsNutrientsCompositeKey(long groupId, long nutrientId){
        this.groupId=groupId;
        this.nutrientId = nutrientId;
    }
}
