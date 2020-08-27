package com.nutrient.nutrientSpring.Model.FoodModel.Keys;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class DietTypesTagsKey implements Serializable {
    @Column(name = "tags_id")
    private long tagsId;
    @Column(name = "diet_types_id")
    private long dietTypesId;

    public DietTypesTagsKey(long tagsId, long dietTypesId){
        this.tagsId=tagsId;
        this.dietTypesId = dietTypesId;
    }
}
