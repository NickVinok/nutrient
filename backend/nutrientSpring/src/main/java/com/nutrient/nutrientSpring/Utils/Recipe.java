package com.nutrient.nutrientSpring.Utils;

import com.nutrient.nutrientSpring.Model.FoodModel.Recipes;
import lombok.Data;

@Data
public class Recipe extends Combination{
    private long id;
    private String name;
    private long dishType;

    public Recipe(Ingredient overallOfRecipe){
        super();
        this.setRecipeList(null);
        this.setOverallNutrientsAndEfficiency(overallOfRecipe);
        this.setIsRecipe(true);
        this.setPfcOverallEfficiency(overallOfRecipe.calculateOverallFoodEfficiency());
        this.setVitaminOverallEfficiency(overallOfRecipe.calculateOverallVitaminEfficiency());
        this.setMineralOverallEfficiency(overallOfRecipe.calculateOverallMineralEfficiency());
        this.setAcidOverallEfficiency(overallOfRecipe.calculateOverallAcidEfficiency());
    }

    public void setRecipeInfo(Recipes recipeInfo){
        this.name = recipeInfo.getName();
        this.id = recipeInfo.getId();
        this.dishType = recipeInfo.getDishType();
    }
}
