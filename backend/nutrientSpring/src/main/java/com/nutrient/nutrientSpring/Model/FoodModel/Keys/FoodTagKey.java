package com.nutrient.nutrientSpring.Model.FoodModel.Keys;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class FoodTagKey implements Serializable {
    @Column(name = "food_id")
    private long foodId;
    @Column(name = "tag_id")
    private long tagId;

    public FoodTagKey(long foodId, long tagId){
        this.foodId = foodId;
        this.tagId = tagId;
    }
}
