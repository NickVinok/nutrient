package com.nutrient.nutrientSpring.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Combination{
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

    public boolean isInCombination(Food food){
        if(products.stream()
                .map(Ingredient::getFood)
                .collect(Collectors.toList()).contains(food)){
            return true;
        }
        return false;
    }

    private boolean isPossibleToAddProduct(Ingredient product){
        overallNutrientsAndEfficiency.sum(product);
        if(overallNutrientsAndEfficiency.compare(1f)){
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
            this.combinationEfficiency = (pfcOverallEfficiency+acidOverallEfficiency+
                    mineralOverallEfficiency+vitaminOverallEfficiency)/4;
            this.limitationTable = limits;
            return true;
        } else {
            return false;
        }
    }

    public void addFoodToCustomCombination(HashMap<String, Object> food){

        HashMap<String, Float> tmpPfc = new HashMap<>();
        HashMap<String, Float> tmpVit = new HashMap<>();
        HashMap<String, Float> tmpMin = new HashMap<>();
        HashMap<String, Float> tmpAcid = new HashMap<>();

        for(Map.Entry<String, Float> old : pfcEfficiency.entrySet()){
            tmpPfc.put(old.getKey(), old.getValue());
        }
        for(Map.Entry<String, Float> old : vitaminEfficiency.entrySet()){
            tmpVit.put(old.getKey(), old.getValue());
        }
        for(Map.Entry<String, Float> old : mineralEfficiency.entrySet()){
            tmpMin.put(old.getKey(), old.getValue());
        }

        for(Map.Entry<String, Float> old : acidEfficiency.entrySet()){
            tmpAcid.put(old.getKey(), old.getValue());
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("pfcEfficiency")).entrySet()) {
            if(pfcEfficiency.containsKey(map.getKey())){
                tmpPfc.put(map.getKey(), pfcEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpPfc.put(map.getKey(), (Float)map.getValue());
            }
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("mineralEfficiency")).entrySet()) {
            if(mineralEfficiency.containsKey(map.getKey())){
                tmpMin.put(map.getKey(), mineralEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpMin.put(map.getKey(), (Float)map.getValue());
            }
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("acidEfficiency")).entrySet()) {
            if(acidEfficiency.containsKey(map.getKey())){
                tmpAcid.put(map.getKey(), acidEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpAcid.put(map.getKey(), (Float)map.getValue());
            }
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("vitaminEfficiency")).entrySet()) {
            if(vitaminEfficiency.containsKey(map.getKey())){
                tmpVit.put(map.getKey(), vitaminEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpVit.put(map.getKey(), (Float)map.getValue());
            }
        }
        pfcEfficiency = tmpPfc;
        vitaminEfficiency = tmpVit;
        mineralEfficiency = tmpMin;
        acidEfficiency = tmpAcid;

        foods.add((Food)food.get("food"));

        pfcOverall.sum((Food)food.get("food"));
        vitaminOverall.sum((Vitamin)food.get("vitamin"));
        mineralOverall.sum((Mineral)food.get("mineral"));
        acidOverall.sum((Acid)food.get("acid"));

        combinationEfficiency += (Float)food.get("overallEfficiency");
        pfcOverallEfficiency += (Float)((HashMap<String, Object>)food.get("pfcEfficiency"))
                .get("overallPfcEfficiency");
        vitaminOverallEfficiency += (Float)((HashMap<String, Object>)food.get("vitaminEfficiency"))
                .get("overallVitaminEfficiency");
        mineralOverallEfficiency += (Float)((HashMap<String, Object>)food.get("mineralEfficiency"))
                .get("overallMineralEfficiency");
        acidOverallEfficiency += (Float)((HashMap<String, Object>)food.get("acidEfficiency"))
                .get("overallAcidEfficiency");
        /*try{
            Thread.sleep(10000);
        } catch (Exception e){

        }*/
    }

    public void deleteFoodFromCombination(Long foodId, HashMap<String, Object> food){
        HashMap<String, Float> tmpPfc = new HashMap<>();
        HashMap<String, Float> tmpVit = new HashMap<>();
        HashMap<String, Float> tmpMin = new HashMap<>();
        HashMap<String, Float> tmpAcid = new HashMap<>();

        for(Map.Entry<String, Float> old : pfcEfficiency.entrySet()){
            tmpPfc.put(old.getKey(), old.getValue());
        }
        for(Map.Entry<String, Float> old : vitaminEfficiency.entrySet()){
            tmpVit.put(old.getKey(), old.getValue());
        }
        for(Map.Entry<String, Float> old : mineralEfficiency.entrySet()){
            tmpMin.put(old.getKey(), old.getValue());
        }
        for(Map.Entry<String, Float> old : acidEfficiency.entrySet()){
            tmpAcid.put(old.getKey(), old.getValue());
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("pfcEfficiency")).entrySet()) {
            if(pfcEfficiency.containsKey(map.getKey())){
                tmpPfc.put(map.getKey(), pfcEfficiency.get(map.getKey())-(Float)map.getValue());
            }
            else{
                tmpPfc.put(map.getKey(), (Float)map.getValue());
            }
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("mineralEfficiency")).entrySet()) {
            if(mineralEfficiency.containsKey(map.getKey())){
                tmpMin.put(map.getKey(), mineralEfficiency.get(map.getKey())-(Float)map.getValue());
            }
            else{
                tmpMin.put(map.getKey(), (Float)map.getValue());
            }
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("acidEfficiency")).entrySet()) {
            if(acidEfficiency.containsKey(map.getKey())){
                tmpAcid.put(map.getKey(), acidEfficiency.get(map.getKey())-(Float)map.getValue());
            }
            else{
                tmpAcid.put(map.getKey(), (Float)map.getValue());
            }
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("vitaminEfficiency")).entrySet()) {
            if(vitaminEfficiency.containsKey(map.getKey())){
                tmpVit.put(map.getKey(), vitaminEfficiency.get(map.getKey())-(Float)map.getValue());
            }
            else{
                tmpVit.put(map.getKey(), (Float)map.getValue());
            }
        }

        pfcEfficiency = tmpPfc;
        vitaminEfficiency = tmpVit;
        mineralEfficiency = tmpMin;
        acidEfficiency = tmpAcid;

        foods.remove(food.get("food"));
        pfcOverall.substract((Food)food.get("food"));
        vitaminOverall.substract((Vitamin)food.get("vitamin"));
        mineralOverall.substract((Mineral)food.get("mineral"));
        acidOverall.subtract((Acid)food.get("acid"));

        combinationEfficiency -= (Float)food.get("overallEfficiency");
        pfcOverallEfficiency -= (Float)((HashMap<String, Object>)food.get("pfcEfficiency"))
                .get("overallPfcEfficiency");
        vitaminOverallEfficiency -= (Float)((HashMap<String, Object>)food.get("vitaminEfficiency"))
                .get("overallVitaminEfficiency");
        mineralOverallEfficiency -= (Float)((HashMap<String, Object>)food.get("mineralEfficiency"))
                .get("overallMineralEfficiency");
        acidOverallEfficiency -= (Float)((HashMap<String, Object>)food.get("acidEfficiency"))
                .get("overallAcidEfficiency");
    }

    public List<List<Integer>> doesCombinationHasOverflowingNutrients(){
        List<List<Integer>> tmp = Stream.of(doesPFCOverflow(), doesVitaminsOverflow(), doesMineralsOverflow(), doesAcidsOverflow())
                .collect(Collectors.toList());
        return tmp;
    }

    private List<Integer> doesPFCOverflow(){
        List<Integer> tmp = new ArrayList<>();
        List<Float> nutrientPercentages = new ArrayList<>(pfcEfficiency.values());
        for(int i =0;i< nutrientPercentages.size();i++) {
            if(nutrientPercentages.get(i) > 1){
                tmp.add(i);
            }
        }
        return tmp;
    }

    private List<Integer> doesVitaminsOverflow(){
        List<Integer> tmp = new ArrayList<>();
        List<Float> nutrientPercentages = new ArrayList<>(vitaminEfficiency.values());
        for(int i =0;i< nutrientPercentages.size();i++) {
            if(nutrientPercentages.get(i) > 1){
                tmp.add(i);
            }
        }
        return tmp;
    }

    private List<Integer> doesMineralsOverflow(){
        List<Integer> tmp = new ArrayList<>();
        List<Float> nutrientPercentages = new ArrayList<>(mineralEfficiency.values());
        for(int i =0;i< nutrientPercentages.size();i++) {
            if(nutrientPercentages.get(i) > 1){
                tmp.add(i);
            }
        }
        return tmp;
    }

    private List<Integer> doesAcidsOverflow(){
        List<Integer> tmp = new ArrayList<>();
        List<Float> nutrientPercentages = new ArrayList<>(acidEfficiency.values());
        for(int i =0;i< nutrientPercentages.size();i++) {
            if(nutrientPercentages.get(i) > 1){
                tmp.add(i);
            }
        }
        return tmp;
    }
}
