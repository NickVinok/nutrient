package com.nutrient.nutrientSpring.Model.FoodModel;

import com.nutrient.nutrientSpring.Model.FoodModel.Keys.RecipeTagKey;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "recipe_tag")
public class RecipeTag {
    @EmbeddedId
    private RecipeTagKey recipeTagKey;

    @ManyToOne(fetch= FetchType.EAGER)
    @MapsId("tag_id")
    @JoinColumn(name="tag_id", referencedColumnName = "id")
    private Tags tags;

    @ManyToOne(fetch= FetchType.EAGER)
    @MapsId("recipe_id")
    @JoinColumn(name="recipe_id", referencedColumnName = "id")
    private Recipes recipe;
}
