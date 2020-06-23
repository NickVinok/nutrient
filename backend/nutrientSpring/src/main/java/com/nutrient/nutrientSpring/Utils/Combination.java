package com.nutrient.nutrientSpring.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Combination{
    private String name="Общая комбинация";
    private long id=-1L;
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

    public boolean isInCombination(Ingredient ingredient){
        if(products.contains(ingredient)){
            return true;
        }
        return false;
    }

    private boolean isPossibleToAddProduct(Ingredient product){
        if(products.size()>12){
            return false;
        }

        overallNutrientsAndEfficiency.sum(product);

        if(overallNutrientsAndEfficiency.compare(1.05f)){
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
            this.combinationEfficiency += product.calculateOverallIngredientEfficiency();
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
        this.combinationEfficiency += product.calculateOverallIngredientEfficiency();

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

    public Combination(){
        this.limitationTable = new FoodAndCategoriesLimitationTable();
        this.overallNutrientsAndEfficiency = new Ingredient();
    }

    @JsonIgnore
    public FoodAndCategoriesLimitationTable getLimitationTable(){
        return this.limitationTable;
    }

    @JsonIgnore
    public Ingredient getMostOverflowingProduct(){
        //получаем здесь самые оверфлоувящие нутриенты (точнее их индексы из getValue)
        int foodIndex = this.overallNutrientsAndEfficiency.getFoodEfficiency().getMostOverflowingIndex(); //foodEfficiency.getMostOverflowing; ВОЗМОЖНО СТОИТ ВОЗВРАЩАТЬ МАССИВ
        int vitaminIndex = this.overallNutrientsAndEfficiency.getVitaminEfficiency().getMostOverflowingIndex();
        int mineralIndex=this.overallNutrientsAndEfficiency.getMineralEfficiency().getMostOverflowingIndex();
        int acidIndex=this.overallNutrientsAndEfficiency.getAcidEfficiency().getMostOverflowingIndex();
        //Если нет оверфлоувящих продуктов
        Ingredient tmp = this.products.stream()
                .max(Comparator.comparingDouble(x -> x.getMostOverFlowingNutrient(foodIndex, mineralIndex, vitaminIndex, acidIndex))).get();
        if(tmp.getMostOverFlowingNutrient(foodIndex, mineralIndex, vitaminIndex, acidIndex) < 1.05){
            return null;
        }
        return tmp;
    }

    //Mutable, удаляет продукты, которые запихнули в приём пищи из себя
    @JsonIgnore
    public Combination getCombinationForMeal(double ratio){
        Combination comb = new Combination();
        comb.setLimitationTable(this.limitationTable);
        if(ratio==1){
            return this;
        } else if(ratio==-1){
            comb.addFoodToCustomCombination(this.products.get(0));
            this.deleteFoodFromCombination(this.products.get(0));
            return comb;
        }
        
        float energy = this.overallNutrientsAndEfficiency.getFoodEfficiency().getEnergy();
        while(true) {
            List<Ingredient> tmp = this.products.stream()
                    .sorted((x, y) -> Float.compare(
                            Math.abs(x.getFoodEfficiency().getEnergy() / energy - (float) ratio),
                            Math.abs(y.getFoodEfficiency().getEnergy() / energy - (float) ratio)
                    )).collect(Collectors.toList());

            Ingredient inMeal = tmp.get(0);
            comb.addFoodToCustomCombination(inMeal);
            this.deleteFoodFromCombination(inMeal);

            if (Math.abs(comb.getOverallNutrientsAndEfficiency().getFoodEfficiency().getEnergy() / energy - ratio)<0.05 || this.products.size()==1) {
                break;
            }
        }
        return comb;
    }
}
