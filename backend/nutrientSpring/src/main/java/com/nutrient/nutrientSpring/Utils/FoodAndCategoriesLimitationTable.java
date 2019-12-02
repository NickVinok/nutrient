package com.nutrient.nutrientSpring.Utils;

import com.nutrient.nutrientSpring.Model.FoodModel.CategoryLimit;
import com.nutrient.nutrientSpring.Model.FoodModel.FoodLimit;

import java.util.List;

public class FoodAndCategoriesLimitationTable {
    private List<FoodLimit> foodLimit;
    private List<CategoryLimit> categoryLimit;

    public FoodAndCategoriesLimitationTable(List<FoodLimit> foodLimit, List<CategoryLimit> categoryLimit){
        this.foodLimit = foodLimit;
        this.categoryLimit = categoryLimit;
    }

    public void addFoodLimit(FoodLimit fl){
        foodLimit.add(fl);
    }
    public void addCategoryLimit(CategoryLimit cl){
        categoryLimit.add(cl);
    }

    public void updateFoodLimit(int id, int value){
        FoodLimit tmp = foodLimit.get(id-1);
        tmp.setWholeCombLimit(tmp.getWholeCombLimit()+value);
        foodLimit.set(id-1, tmp);
    }
    public void updateCategoryLimit(int id, int value){
        CategoryLimit tmp = categoryLimit.get(id-1);
        tmp.setSingleCombLimit(tmp.getSingleCombLimit()+value);
        tmp.setWholeCombLimit(tmp.getWholeCombLimit()+value);
        categoryLimit.set(id-1, tmp);
    }

    public int getFoodLimit(int id){
        return foodLimit.get(id-1).getWholeCombLimit();
    }
    public int getCategoryLimitInAllCombs(int id){
        return categoryLimit.get(id-1).getWholeCombLimit();
    }
    public int getCategoryLimitInComb(int id){
        return categoryLimit.get(id-1).getSingleCombLimit();
    }
}
