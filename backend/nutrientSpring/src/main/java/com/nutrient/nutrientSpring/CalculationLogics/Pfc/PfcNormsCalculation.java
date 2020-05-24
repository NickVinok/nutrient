package com.nutrient.nutrientSpring.CalculationLogics.Pfc;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
@Data
public class PfcNormsCalculation extends PfcNorms {
    private float[] coefs= {1.4f, 1.6f, 1.9f, 2.2f, 2.5f};

    public PfcNormsCalculation(String gender, float age, float weight, float height, String dietType, int workingGroup){
        float energyLoss = this.energyLossCalculation(gender,age, weight, height, dietType, workingGroup);
        calculatePfc(dietType, energyLoss);
    }

    private float energyLossCalculation(String gender, float age, float weight, float height, String dietType, int workingGroup){
        float mainEnergyExchange = 0;
        float CaloriesCoefficient = coefs[workingGroup-1];
        /*
        При расчёте калорий используются 2 алгоритма:
        алгоритм Харриса-Бенедикта используется при похудении
        алгоритм Миффлина-Сан Жеора используется в остальных случаях
         */
        if(gender.equals("Male")){
            if(dietType.equals("loss")){
                mainEnergyExchange = 66+3.7f*weight+6*height-6.8f*age;
            } else {
                mainEnergyExchange = 10*weight+6.25f*height-5*age+5;
            }
            this.water = weight*35/2;

        } else{
            if(dietType.equals("loss")){
                mainEnergyExchange = 65 + 9.6f*weight + 1.8f*height - 4.7f*age;
            } else {
                mainEnergyExchange = 10*weight + 6.25f*height - 5*age -161;
            }
            this.water = weight*31/2;

        }

        return mainEnergyExchange*CaloriesCoefficient;
    }

    private void calculatePfc(String dietType, float energyLoss){
        if(dietType.equals("loss")){
            this.calories = energyLoss - energyLoss*0.22f;
            this.proteins = energyLoss*0.18f/4;
            this.fats = energyLoss*0.32f/9;
            this.carbohydrates = (this.calories-(this.proteins*4+this.fats*9))/4;
        }
        else if(dietType.equals("save")){
            this.calories = energyLoss;
            this.proteins = this.calories*0.18f/4;
            this.carbohydrates = this.calories*0.5f/4;
            this.fats = this.calories*0.32f/9;
        }
        else {
            this.calories = energyLoss + energyLoss*0.25f;
            this.proteins = this.calories*0.2f/4;
            this.carbohydrates = this.calories*0.5f/4;
            this.fats = this.calories*0.3f/9;
        }

        this.sugar = 50;
        this.cholesterol = 300;
        this.starch = carbohydrates-this.sugar;
        this.fiber = 38;
        this.omega3 = calories/1000;
        this.omega6 = calories/1000*4;
        this.omega9 = calories/10;

    }

    public PfcNorms getNorms(){
        PfcNorms norms = new PfcNorms();
        norms.setCalories(calories);
        norms.setProteins(proteins);
        norms.setFats(fats);
        norms.setCarbohydrates(carbohydrates);
        norms.setWater(water);
        norms.setAsh(ash);
        norms.setSugar(sugar);
        norms.setCholesterol(cholesterol);
        norms.setStarch (starch);
        norms.setOmega3(omega3);
        norms.setOmega6(omega6);
        norms.setOmega9(omega9);
        norms.setSfa(18.7f);
        return norms;
    }

    public List<Float> getPfc(){
        return Stream.of(calories, proteins, fats, carbohydrates, water, ash, sugar, starch, cholesterol, fiber, sfa)
                .collect(Collectors.toList());
    }
}
