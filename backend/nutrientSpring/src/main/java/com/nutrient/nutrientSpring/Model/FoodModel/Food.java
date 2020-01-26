package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nutrient.nutrientSpring.Utils.NutrientGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.*;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@Table(name = "food")
@AllArgsConstructor
@NoArgsConstructor
public class Food implements NutrientGroup {
    @Id
    private Long id;
    private String name;
    //Шо це за залупа, типа генерал еды или чё
    @Nullable
    private Long general;
    private float energy;
    private float fat;
    private float protein;
    private float carbohydrate;
    private float water;
    private float ash;
    private float sugares;
    private float fiber;
    private float starch;
    private float cholesterol;
    @Column(name = "img_id")
    private String img;
    //TODO ДОБАВИТЬ НОРМЫ  И В РАСЧЁТЫ
    @Column(name = "organic_acid")
    @Nullable
    private Float organicAcid;
    @Nullable
    private Float sfa;
    @Column(name="category_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    public void sum(Food f1){
        this.energy+=f1.getEnergy();
        this.fat+=f1.getFat();
        this.protein+=f1.getProtein();
        this.carbohydrate+=f1.getCarbohydrate();
        this.water+=f1.getWater();
        this.ash+=f1.getAsh();
        this.sugares+=f1.getSugares();
        this.fiber+=f1.getFiber();
        this.starch+=f1.getStarch();
        this.cholesterol+=f1.getCholesterol();
    }

    public void subtract(Food f1){
        this.energy-=f1.getEnergy();
        this.fat-=f1.getFat();
        this.protein-=f1.getProtein();
        this.carbohydrate-=f1.getCarbohydrate();
        this.water-=f1.getWater();
        this.ash-=f1.getAsh();
        this.sugares-=f1.getSugares();
        this.fiber-=f1.getFiber();
        this.starch-=f1.getStarch();
        this.cholesterol-=f1.getCholesterol();
    }

    public void modify(Float c){
        this.energy*=c;
        this.fat*=c;
        this.protein*=c;
        this.carbohydrate*=c;
        this.water*=c;
        this.ash*=c;
        this.sugares*=c;
        this.fiber*=c;
        this.starch*=c;
        this.cholesterol*=c;
    }
    
    public boolean compare(Float numb){
        int overflowingNutrientsValue = 2;
        for(Float nutrient: getValues()){
            if((nutrient/numb)>4) overflowingNutrientsValue=0;
            else if(nutrient>numb) overflowingNutrientsValue--;

            if(overflowingNutrientsValue == 0) return false;
        }
        return true;
    }

    @JsonIgnore
    public List<Float> getValues(){
        return Stream.of(energy, fat, protein, carbohydrate, water, ash, sugares, starch, cholesterol)
                .collect(Collectors.toList());
    }

    public Food(List<Float> f){
        this.id = -1L;
        this.name = "Норма БЖУ";
        this.energy=f.get(0);
        this.fat=f.get(2);
        this.protein=f.get(1);
        this.carbohydrate=f.get(3);
        this.water=f.get(4);
        this.ash=f.get(5);
        this.sugares=f.get(6);
        this.fiber=f.get(9);
        this.starch=f.get(7);
        this.cholesterol=f.get(8);
    }

    public Food(Food f, Food foodNorm){
        this.id = -1L;
        this.name = "";

        this.energy=f.getEnergy()/foodNorm.getEnergy();
        this.fat=f.getFat()/foodNorm.getFat();
        this.protein=f.getProtein()/foodNorm.getProtein();
        this.carbohydrate=f.getCarbohydrate()/foodNorm.getCarbohydrate();
        this.water=f.getWater()/foodNorm.getWater();
        this.ash=f.getAsh()/foodNorm.getAsh();
        this.sugares=f.getSugares()/foodNorm.getSugares();
        this.fiber=f.getFiber()/foodNorm.getFiber();
        this.starch=f.getStarch()/foodNorm.getStarch();
        this.cholesterol=f.getCholesterol()/foodNorm.getCholesterol();
    }
}
