package com.nutrient.nutrientSpring.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import lombok.Data;

@Data
public class Ingredient {
    private Long id;
    private int gram;
    private Food food;
    private Vitamin vitamin;
    private Mineral mineral;
    private Acid acid;

    private Food foodEfficiency;
    private Vitamin vitaminEfficiency;
    private Mineral mineralEfficiency;
    private Acid acidEfficiency;


    public Ingredient(Food f, Vitamin v, Mineral m, Acid a, int g){
        this.food = f;
        this.vitamin = v;
        this.mineral = m;
        this.acid = a;
        this.id = f.getId();
        this.gram = g;
    }

    //конструктор по умолчанию
    public Ingredient(Food f, Vitamin v, Mineral m, Acid a){
        this(f,v,m,a,100);
    }

    @JsonIgnore
    public void setEfficiency(Food fE, Vitamin vE, Mineral mE, Acid aE){
        this.foodEfficiency = fE;
        this.mineralEfficiency = mE;
        this.vitaminEfficiency = vE;
        this.acidEfficiency = aE;
    }

    public Float calculateOverallFoodEfficiency(){
        Float sum=this.foodEfficiency.getValues()
                .stream()
                .reduce(0f, Float::sum)
        ;
        int count = this.foodEfficiency.getValues()
                .size();
        return sum/count;
    }

    public Float calculateOverallAcidEfficiency(){
        Float sum=this.acidEfficiency.getValues()
                .stream()
                .reduce(0f, Float::sum)
                ;
        int count = this.acidEfficiency.getValues()
                .size();
        return sum/count;
    }

    public Float calculateOverallMineralEfficiency(){
        Float sum=this.mineralEfficiency.getValues()
                .stream()
                .reduce(0f, Float::sum)
                ;
        int count = this.mineralEfficiency.getValues()
                .size();
        return sum/count;
    }

    public Float calculateOverallVitaminEfficiency(){
        Float sum=this.vitaminEfficiency.getValues()
                .stream()
                .reduce(0f, Float::sum)
                ;
        int count = this.vitaminEfficiency.getValues()
                .size();
        return sum/count;
    }

    public Float calculateOverallIngredientEfficiency(){
        return (calculateOverallFoodEfficiency()+calculateOverallAcidEfficiency()+calculateOverallMineralEfficiency()
                +calculateOverallVitaminEfficiency())/4;
    }

    public void sum(Ingredient i1){
        this.gram+= i1.gram;
        
        this.food.sum(i1.food);
        this.foodEfficiency.sum(i1.foodEfficiency);
        
        this.acid.sum(i1.acid);
        this.acidEfficiency.sum(i1.acidEfficiency);
        
        this.mineral.sum(i1.mineral);
        this.mineralEfficiency.sum(i1.mineralEfficiency);
        
        this.vitamin.sum(i1.vitamin);
        this.vitaminEfficiency.sum(i1.vitaminEfficiency);
    }
    
    public void subtract(Ingredient i1){
        this.gram-= i1.gram;

        this.food.subtract(i1.food);
        this.foodEfficiency.subtract(i1.foodEfficiency);

        this.acid.subtract(i1.acid);
        this.acidEfficiency.subtract(i1.acidEfficiency);

        this.mineral.subtract(i1.mineral);
        this.mineralEfficiency.subtract(i1.mineralEfficiency);

        this.vitamin.subtract(i1.vitamin);
        this.vitaminEfficiency.subtract(i1.vitaminEfficiency);
    }
    
    public void multiply(Float coef){
        this.gram*=coef;

        this.food.modify(coef);
        this.foodEfficiency.modify(coef);

        this.acid.modify(coef);
        this.acidEfficiency.modify(coef);

        this.mineral.modify(coef);
        this.mineralEfficiency.modify(coef);

        this.vitamin.modify(coef);
        this.vitaminEfficiency.modify(coef);
    }

    public boolean compare(Float numb){
        return food.compare(numb) && acid.compare(numb) && mineral.compare(numb) && vitamin.compare(numb);
    }
}
