package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.*;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@Table(name = "food")
@AllArgsConstructor
@NoArgsConstructor
public class Food {
    @Id
    private Long id;
    private String name;
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
    private float fat_trans;
    @JsonIgnore
    private int img;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category", nullable = false)
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
        this.fat_trans+=f1.getFat_trans();
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
        this.fat_trans-=f1.getFat_trans();
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
        this.fat_trans*=c;
    }
    
    public boolean compare(Float numb){
        int overflowingNutrientsValue = 1;
        for(Float nutrient: getValues()){
            if(nutrient<numb) overflowingNutrientsValue--;
            if(overflowingNutrientsValue == 0) return false;
        }
        return true;
        /*return this.energy<=numb &&
        this.fat <= numb &&
        this.protein<=numb &&
        this.carbohydrate<=numb &&
        this.water<=numb &&
        this.ash<=numb &&
        this.sugares<=numb &&
        this.fiber<=numb &&
        this.starch<=numb &&
        this.cholesterol<=numb &&
        this.fat_trans<=numb;*/
    }

    @JsonIgnore
    public List<Float> getValues(){
        return Stream.of(energy, fat, protein, carbohydrate, water, ash, sugares, starch, cholesterol, fat_trans)
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
        this.fiber=f.get(10);
        this.starch=f.get(7);
        this.cholesterol=f.get(9);
        this.fat_trans=f.get(8);
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
        this.fat_trans=f.getFat_trans()/foodNorm.getFat_trans();
    }
}
