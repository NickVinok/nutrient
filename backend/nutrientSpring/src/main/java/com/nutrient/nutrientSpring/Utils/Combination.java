package com.nutrient.nutrientSpring.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Combination{
    private List<Ingredient> products = new ArrayList<>();
    private Ingredient overallNutrientsAndEfficiency;
    private Float combinationEfficiency = 0f;
    private Float pfcOverallEfficiency = 0f;
    private Float vitaminOverallEfficiency = 0f;
    private Float mineralOverallEfficiency = 0f;
    private Float acidOverallEfficiency = 0f;

    @JsonIgnore
    private int foodCounter = 12;
    @JsonIgnore
    private FoodAndCategoriesLimitationTable limitationTable;

    public List<Ingredient> getProducts(){
        return products;
    }

    public boolean isInCombination(Food food){
        if(products.stream()
                .map(Ingredient::getFood)
                .collect(Collectors.toList()).contains(food)){
            return true;
        }
        return false;
    }

    private boolean isPossibleToAddProduct(Ingredient product){
        if(products.size()>18){
            return false;
        }
        overallNutrientsAndEfficiency.sum(product);

        if(overallNutrientsAndEfficiency.compare(1f)){
            return true;
        }

        overallNutrientsAndEfficiency.subtract(product);
        return false;
    }

    public boolean addProductToCombination(Ingredient product, FoodAndCategoriesLimitationTable limits){
        if(isPossibleToAddProduct(product)){
            products.add(product);
            this.pfcOverallEfficiency += product.calculateOverallFoodEfficiency();
            this.acidOverallEfficiency += product.calculateOverallAcidEfficiency();
            this.mineralOverallEfficiency+=product.calculateOverallMineralEfficiency();
            this.vitaminOverallEfficiency+=product.calculateOverallVitaminEfficiency();
            this.combinationEfficiency = (pfcOverallEfficiency+acidOverallEfficiency+
                    mineralOverallEfficiency+vitaminOverallEfficiency)/4;
            this.limitationTable = limits;
            return true;
        } else {
            return false;
        }
    }

    public void addFoodToCustomCombination(Ingredient product){
        overallNutrientsAndEfficiency.sum(product);
        products.add(product);

        this.pfcOverallEfficiency += product.calculateOverallFoodEfficiency();
        this.acidOverallEfficiency += product.calculateOverallAcidEfficiency();
        this.mineralOverallEfficiency+=product.calculateOverallMineralEfficiency();
        this.vitaminOverallEfficiency+=product.calculateOverallVitaminEfficiency();
        this.combinationEfficiency = (pfcOverallEfficiency+acidOverallEfficiency+
                mineralOverallEfficiency+vitaminOverallEfficiency)/4;

        this.limitationTable.updateCategoryLimit(product.getFood().getCategory().getId(), -1);
        this.limitationTable.updateFoodLimit(product.getId(), -1);
    }

    public void deleteFoodFromCombination(Ingredient product){
        products.remove(product);
        overallNutrientsAndEfficiency.subtract(product);

        this.pfcOverallEfficiency -= product.calculateOverallFoodEfficiency();
        this.acidOverallEfficiency -= product.calculateOverallAcidEfficiency();
        this.mineralOverallEfficiency -=product.calculateOverallMineralEfficiency();
        this.vitaminOverallEfficiency -=product.calculateOverallVitaminEfficiency();
        this.combinationEfficiency = (pfcOverallEfficiency+acidOverallEfficiency+
                mineralOverallEfficiency+vitaminOverallEfficiency)/4;

        this.limitationTable.updateCategoryLimit(product.getFood().getCategory().getId(), 1);
        this.limitationTable.updateFoodLimit(product.getId(), 1);
    }

    public List<List<Integer>> doesCombinationHasOverflowingNutrients(){
        List<List<Integer>> tmp = Stream.of(doesPFCOverflow(), doesVitaminsOverflow(), doesMineralsOverflow(), doesAcidsOverflow())
                .collect(Collectors.toList());
        //System.out.println(tmp);
        return tmp;
    }

    private List<Integer> doesPFCOverflow(){
        List<Integer> tmp = new ArrayList<>();
        List<Float> nutrientPercentages = overallNutrientsAndEfficiency.getFoodEfficiency().getValues();
        for(int i =0;i< nutrientPercentages.size();i++) {
            if(nutrientPercentages.get(i) > 1){
                tmp.add(i);
            }
        }
        return tmp;
    }

    private List<Integer> doesVitaminsOverflow(){
        List<Integer> tmp = new ArrayList<>();
        List<Float> nutrientPercentages = overallNutrientsAndEfficiency.getVitaminEfficiency().getValues();
        for(int i =0;i< nutrientPercentages.size();i++) {
            if(nutrientPercentages.get(i) > 1){
                tmp.add(i);
            }
        }
        return tmp;
    }

    private List<Integer> doesMineralsOverflow(){
        List<Integer> tmp = new ArrayList<>();
        List<Float> nutrientPercentages = overallNutrientsAndEfficiency.getMineralEfficiency().getValues();
        for(int i =0;i< nutrientPercentages.size();i++) {
            if(nutrientPercentages.get(i) > 1){
                tmp.add(i);
            }
        }
        return tmp;
    }

    private List<Integer> doesAcidsOverflow(){
        List<Integer> tmp = new ArrayList<>();
        List<Float> nutrientPercentages = overallNutrientsAndEfficiency.getAcidEfficiency().getValues();
        for(int i =0;i< nutrientPercentages.size();i++) {
            if(nutrientPercentages.get(i) > 1){
                tmp.add(i);
            }
        }
        return tmp;
    }
    public Combination(){
        this.limitationTable = new FoodAndCategoriesLimitationTable();
        this.overallNutrientsAndEfficiency = new Ingredient();
    }

    @JsonIgnore
    public FoodAndCategoriesLimitationTable getLimitationTable(){
        return this.limitationTable;
    }
}
