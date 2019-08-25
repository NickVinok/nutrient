package com.nutrient.nutrientSpring.Model.FoodModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.*;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
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

    public List<Float> getValues(){
        return Stream.of(energy, fat, protein, carbohydrate, water, ash, sugares, fiber, starch, cholesterol, fat_trans)
                .collect(Collectors.toList());
    }
}
