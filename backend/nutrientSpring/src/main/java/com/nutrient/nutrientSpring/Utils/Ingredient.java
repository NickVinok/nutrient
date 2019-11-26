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
}
