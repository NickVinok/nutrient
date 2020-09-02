package com.nutrient.nutrientSpring.Model.FoodModel.Keys;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class RecipeTagKey implements Serializable {
    @Column(name = "tags_id")
    private long tagsId;
    @Column(name = "recipe_id")
    private long recipeId;

    public RecipeTagKey(long tagsId, long recipeId){
        this.tagsId=tagsId;
        this.recipeId = recipeId;
    }
}
