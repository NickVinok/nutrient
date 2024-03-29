package com.nutrient.nutrientSpring.Utils.Ration;

import com.nutrient.nutrientSpring.Utils.Combination;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Ration {
    private int day;
    private List<Meal> meals; //За инночку и двор стреляю плазмой в упор
    private Combination additionalFood;
    private double rationEfficiency;

    public Ration(int day) {
        this.day = day;
        this.meals = new ArrayList<>();
    }

    public void combinationPartitioning(Combination toBePartitioned, int numberOfMilfs) {
        if (numberOfMilfs >= toBePartitioned.getRecipeList().size()) {
            for (int i = 0; i < toBePartitioned.getProducts().size(); i++) {
                Meal meal = new Meal();
                meal.setName("Приём пищи №" + i);
                meal.setCombination(toBePartitioned.getCombinationForMeal(-1));
                meals.add(meal);
            }
        } else if (numberOfMilfs == 3) {
            List<String> names = Stream.of("Завтрак", "Обед", "Ужин").collect(Collectors.toList());
            List<Double> ratios = Stream.of(0.2, 0.5, 1d).collect(Collectors.toList());
            for (int i = 0; i < numberOfMilfs; i++) {
                Meal meal = new Meal();
                meal.setName(names.get(i));
                meal.setCombination(toBePartitioned.getCombinationForMeal(ratios.get(i)));
                meals.add(meal);
            }
        }
        this.additionalFood = toBePartitioned;
        rationEfficiency = this.meals.stream()
                .map(Meal::getCombination)
                .map(Combination::getCombinationEfficiency)
                .reduce(0f, Float::sum);
        if(this.additionalFood.getProducts().size()!=0){
            rationEfficiency+= this.additionalFood.getCombinationEfficiency();
        }
    }
}
