package com.nutrient.nutrientSpring.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Model.FoodModel.CategoryLimit;
import com.nutrient.nutrientSpring.Model.FoodModel.FoodLimit;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class FoodAndCategoriesLimitationTable {
    private HashMap<Long, Integer> wholeFoodLimit;
    private HashMap<Long, Integer> wholeCategoryLimit;
    private HashMap<Long, Integer> singleCategoryLimit;


    public FoodAndCategoriesLimitationTable(List<FoodLimit> foodLimit, List<CategoryLimit> categoryLimit){
        wholeCategoryLimit = new HashMap<>();
        singleCategoryLimit = new HashMap<>();
        wholeFoodLimit = new HashMap<>();

        for(FoodLimit f : foodLimit){
            this.wholeFoodLimit.put(f.getFood().getId(), f.getWholeCombLimit());
        }

        for(CategoryLimit c: categoryLimit){
            this.wholeCategoryLimit.put(c.getCategory().getId(), c.getWholeCombLimit());
            this.singleCategoryLimit.put(c.getCategory().getId(), c.getSingleCombLimit());
        }
    }

    public void updateFoodLimit(Long id, int value){
        wholeFoodLimit.put(id, wholeFoodLimit.get(id)+value);
    }
    public void updateCategoryLimit(Long id, int value){
        wholeCategoryLimit.put(id, wholeCategoryLimit.get(id)+value);
        singleCategoryLimit.put(id, singleCategoryLimit.get(id)+value);
    }
    @JsonIgnore
    public int getFoodLimit(Long id){
        return  wholeFoodLimit.get(id);
    }
    @JsonIgnore
    public int getCategoryLimitInAllCombs(Long id){
        return wholeCategoryLimit.get(id);
    }
    @JsonIgnore
    public int getCategoryLimitInComb(Long id){
        return singleCategoryLimit.get(id);
    }
    public boolean isCategoryAllowed(Long id){
        return singleCategoryLimit.containsKey(id);
    }
    public boolean isProductAllowed(Long id){
        return wholeFoodLimit.containsKey(id);
    }
    public HashMap<Long, Integer> getSingleTable(){
        return this.singleCategoryLimit;
    }

    public FoodAndCategoriesLimitationTable(){

    }
}
