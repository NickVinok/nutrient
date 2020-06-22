package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Utils.NutrientGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.*;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;
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
    private float sugars;
    private float fiber;
    private float starch;
    private float cholesterol;
    @Column(name = "img_id")
    private String img;
    //TODO ДОБАВИТЬ НОРМЫ  И В РАСЧЁТЫ
    @Column(name = "organic_acid")
    @Nullable
    private Float organicAcid;
    private Float sfa;

    private double glucose;
    private double fructose;
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
        this.sugars +=f1.getSugars();
        this.fiber+=f1.getFiber();
        this.starch+=f1.getStarch();
        this.cholesterol+=f1.getCholesterol();
        this.sfa +=  f1.getSfa();
        this.glucose += f1.getGlucose();
        this.fructose+=f1.getFructose();
    }

    public void subtract(Food f1){
        this.energy-=f1.getEnergy();
        this.fat-=f1.getFat();
        this.protein-=f1.getProtein();
        this.carbohydrate-=f1.getCarbohydrate();
        this.water-=f1.getWater();
        this.ash-=f1.getAsh();
        this.sugars -=f1.getSugars();
        this.fiber-=f1.getFiber();
        this.starch-=f1.getStarch();
        this.cholesterol-=f1.getCholesterol();
        this.sfa -= f1.getSfa();
        this.glucose -= f1.getGlucose();
        this.fructose-=f1.getFructose();
    }

    public void modify(Float c){
        this.energy*=c;
        this.fat*=c;
        this.protein*=c;
        this.carbohydrate*=c;
        this.water*=c;
        this.ash*=c;
        this.sugars *=c;
        this.fiber*=c;
        this.starch*=c;
        this.cholesterol*=c;
        this.sfa*=c;
        this.glucose *=c;
        this.fructose*=c;
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
        return Stream.of(energy, fat, protein, carbohydrate, water, ash, sugars, starch, cholesterol, sfa)
                .collect(Collectors.toList());
    }
    @JsonIgnore
    public List<Float> getPoints(){
        //Первая группа нутриентов - важные, редко имеется достаток
        //Вторая группа нутриентов - более менее нормальное количество
        //ТРетья группа- не должно превышать определённого уровня
        //Четвёртая группа - не должно превышать
        //Пятая группа - не должно первышать
        List<Float> foodPoints = Stream.of(
        this.fiber*300).collect(Collectors.toList());
        List<Float> inverseNutrients = Stream.of(this.sfa, this.cholesterol, this.sugars).collect(Collectors.toList());
        for(Float invNutrient: inverseNutrients){
            if(invNutrient<=0.25){
                foodPoints.add(100f);
            }
            else if(invNutrient>0.25 && invNutrient<=0.91){
                foodPoints.add(100f-((int)(invNutrient*100)-25)*1.5f);
            }
            else if(invNutrient>0.91 && invNutrient<1){
                foodPoints.add(0f);
            } else if(invNutrient>1 && invNutrient<1.5){
                foodPoints.add(0f-((int)(invNutrient*100)-100)*10f);
            } else{
                foodPoints.add(-500f-((int)(invNutrient*100)-150)*20f);
            }
        }
        List<Float> fourthNutrientGroup = Stream.of
                (this.protein, this.starch, this.carbohydrate, this.fat)
                .collect(Collectors.toList());
        for(Float n: fourthNutrientGroup){
            if((int)(n*100)>115){
                foodPoints.add(3*115f-((int)(n*100)-115)*10f);
            }else{
                foodPoints.add(n*300);
            }
        }
        return foodPoints;
    }

    @JsonIgnore
    public int getMostOverflowingIndex(){
        List<Float> tmp= this.getValues();
        int index = 0;
        double value = 0;
        for(int i = 0;i<tmp.size();i++){
            if(tmp.get(i)>value){
                value = tmp.get(i);
                index = i;
            }
        }
        return index;
    }

    @JsonIgnore
    public int getLeastOverflowingNutrient(){
        List<Float> tmp= this.getValues();
        int index = 0;
        double value = tmp.get(0);
        for(int i=0;i<tmp.size();i++){
            if(tmp.get(i)<value){
                value = tmp.get(i);
                index = i;
            }
        }
        return index;
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
        this.sugars =f.get(6);
        this.fiber=f.get(9);
        this.starch=f.get(7);
        this.cholesterol=f.get(8);
        this.sfa = f.get(10);
        this.glucose=0;
        this.fructose=0;
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
        this.sugars =f.getSugars()/foodNorm.getSugars();
        this.fiber=f.getFiber()/foodNorm.getFiber();
        this.starch=f.getStarch()/foodNorm.getStarch();
        this.cholesterol=f.getCholesterol()/foodNorm.getCholesterol();
        this.sfa = f.getSfa()/foodNorm.getSfa();
    }
}
