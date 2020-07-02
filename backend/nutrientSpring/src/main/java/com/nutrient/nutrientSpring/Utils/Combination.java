package com.nutrient.nutrientSpring.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Recipes;
import lombok.Data;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Combination{
    private boolean isRecipe=false;
    private List<Recipe> recipeList = null;
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

    public boolean isProductInCombination(Ingredient ingredient){
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
        this.combinationEfficiency -= product.calculateOverallIngredientEfficiency();

        this.limitationTable.updateCategoryLimit(product.getFood().getCategory().getId(), 1);
        this.limitationTable.updateFoodLimit(product.getId(), 1);
    }

    public Combination(){
        this.isRecipe=false;
        this.recipeList=new ArrayList<>();
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
        /*int foodIndex = this.overallNutrientsAndEfficiency.getFoodEfficiency().getMostOverflowingIndex(); //foodEfficiency.getMostOverflowing; ВОЗМОЖНО СТОИТ ВОЗВРАЩАТЬ МАССИВ
        int vitaminIndex = this.overallNutrientsAndEfficiency.getVitaminEfficiency().getMostOverflowingIndex();
        int mineralIndex=this.overallNutrientsAndEfficiency.getMineralEfficiency().getMostOverflowingIndex();
        int acidIndex=this.overallNutrientsAndEfficiency.getAcidEfficiency().getMostOverflowingIndex();*/
        //Если нет оверфлоувящих продуктов
         /*tmp = this.products.stream()
                .max(Comparator.comparingDouble(x -> x.getMostOverFlowingNutrient(foodIndex, mineralIndex, vitaminIndex, acidIndex))).get();
        if(tmp.getMostOverFlowingNutrient(foodIndex, mineralIndex, vitaminIndex, acidIndex) < 1.05){
            return null;
        }*/
        List<Ingredient> tmp = this.products.stream()
                .sorted((x,y)-> Float.compare(y.calculateOverallIngredientEfficiency(), x.calculateOverallIngredientEfficiency()))
                .collect(Collectors.toList());
        if(tmp.size()==0){
            return null;
        }
        if(tmp.get(0).compare(1.05f)){
            return null;
        }
        return tmp.get(0);
    }

    //Mutable, удаляет продукты, которые запихнули в приём пищи из себя
    @JsonIgnore
    public Combination getCombinationForMeal(double ratio){
        Combination comb = new Combination();
        comb.setLimitationTable(this.limitationTable);

        while(recipeList.size()!=0) {
            Recipe toAddToMeal = this.recipeList.get(0);
            //В случае, если попадается тип блюда "первое"
            if(ratio==0.2){
                toAddToMeal = this.recipeList.stream()
                        .filter(x->x.getDishType()==3).collect(Collectors.toList())
                        .get(0);
                comb.addRecipe(toAddToMeal);
                this.removeRecipe(toAddToMeal);
                return comb;
            }else if(toAddToMeal.getDishType()==1){
                comb.addRecipe(toAddToMeal);
                this.removeRecipe(toAddToMeal);
                toAddToMeal = this.recipeList.stream()
                        .filter(x->x.getDishType()==2).collect(Collectors.toList())
                        .get(0);
                comb.addRecipe(toAddToMeal);
                this.removeRecipe(toAddToMeal);
                return comb;
            }
            //В случае, если попадается тип блюда "второе"
            else if(toAddToMeal.getDishType()==2){
                comb.addRecipe(toAddToMeal);
                this.removeRecipe(toAddToMeal);
                toAddToMeal = this.recipeList.stream()
                        .filter(x->x.getDishType()==1).collect(Collectors.toList())
                        .get(0);
                comb.addRecipe(toAddToMeal);
                this.removeRecipe(toAddToMeal);
                return comb;
            }
            else{
                comb.addRecipe(toAddToMeal);
                this.removeRecipe(toAddToMeal);
                return comb;
            }
        }
        return comb;
    }

    public void setIsRecipe(boolean isRecipe) {
        this.isRecipe=isRecipe;
    }

    public void addRecipe(Recipe r){
        this.recipeList.add(r);
        overallNutrientsAndEfficiency.sum(r.getOverallNutrientsAndEfficiency());

        this.pfcOverallEfficiency += r.getPfcOverallEfficiency();
        this.acidOverallEfficiency += r.getAcidOverallEfficiency();
        this.mineralOverallEfficiency+=r.getMineralOverallEfficiency();
        this.vitaminOverallEfficiency+=r.getVitaminOverallEfficiency();
        this.combinationEfficiency += r.getCombinationEfficiency();
    }

    public void removeRecipe(Recipe r){
        this.recipeList.remove(r);
        overallNutrientsAndEfficiency.subtract(r.getOverallNutrientsAndEfficiency());

        this.pfcOverallEfficiency -= r.getPfcOverallEfficiency();
        this.acidOverallEfficiency -= r.getAcidOverallEfficiency();
        this.mineralOverallEfficiency-=r.getMineralOverallEfficiency();
        this.vitaminOverallEfficiency-=r.getVitaminOverallEfficiency();
        this.combinationEfficiency -= r.getCombinationEfficiency();
    }
}
