package com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST;

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
    private List<Food> foods = new ArrayList<>();
    private Food pfcOverall = new Food(-1L, "combination", 0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f, 0,null);
    private Vitamin vitaminOverall = new Vitamin(-1L, 0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,null);
    private Mineral mineralOverall = new Mineral(-1L, 0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,null);
    private Acid acidOverall = new Acid(-1L, 0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,null);
    private HashMap<String, Float> vitaminEfficiency = new HashMap<>();
    private HashMap<String, Float> mineralEfficiency = new HashMap<>();
    private HashMap<String, Float> acidEfficiency = new HashMap<>();
    private HashMap<String, Float> pfcEfficiency = new HashMap<>();;

    private Float combinationEfficiency = 0f;
    private Float pfcOverallEfficiency = 0f;
    private Float vitaminOverallEfficiency = 0f;
    private Float mineralOverallEfficiency = 0f;
    private Float acidOverallEfficiency = 0f;
    @JsonIgnore
    private int foodCounter = 5;

    public boolean addFoodToCombination(HashMap<String, Object> food){
        //Счётчик для нутриентов, которые выходят за нормы
        int acceptableNumberOfOverFlowingNutrients = 3;

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

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("pfcEfficiency")).entrySet()){
            if(pfcEfficiency.containsKey(map.getKey())){
                //Смотрим не содержит ли продукт чересчур много какого-либо из БЖУ
                if((Float)map.getValue() > 1 ){
                    if(foodCounter>0){
                        return true;
                    }else{
                        return false;
                    }
                }

                //Смотрим, не является ли сумма БЖУ комбинации чересчур большой
                if(pfcEfficiency.get(map.getKey())+(Float)map.getValue() > 1.1f && acceptableNumberOfOverFlowingNutrients >0)
                {
                    acceptableNumberOfOverFlowingNutrients--;
                }
                else if(pfcEfficiency.get(map.getKey())+(Float)map.getValue() > 1.1f && acceptableNumberOfOverFlowingNutrients == 0){
                    if(foodCounter == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }
                tmpPfc.put(map.getKey(), pfcEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpPfc.put(map.getKey(), (Float)map.getValue());
            }
        }

        acceptableNumberOfOverFlowingNutrients = 3;
        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("mineralEfficiency")).entrySet()){
            if(mineralEfficiency.containsKey(map.getKey())){
                //Смотрим не содержит ли продукт чересчур много какого-либо из минералов
                //System.out.print(((Food)food.get("food")).getName());
                //System.out.println(": "+map.getKey() + ":" + map.getValue());
                if((Float)map.getValue() > 1 ){
                    if(foodCounter>0){
                        return true;
                    }else{
                        return false;
                    }
                }
                //Смотрим, не является ли сумма минералов комбинации чересчур большой
                if(mineralEfficiency.get(map.getKey())+(Float)map.getValue() > 1.15f && acceptableNumberOfOverFlowingNutrients>0){
                    acceptableNumberOfOverFlowingNutrients--;
                }else if(mineralEfficiency.get(map.getKey())+(Float)map.getValue() > 1.15f && acceptableNumberOfOverFlowingNutrients==0){
                    if(foodCounter == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }
                tmpMin.put(map.getKey(), mineralEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpMin.put(map.getKey(), (Float)map.getValue());
            }
        }

        acceptableNumberOfOverFlowingNutrients = 3;
        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("vitaminEfficiency")).entrySet()){
            if(vitaminEfficiency.containsKey(map.getKey())){
                //Смотрим не содержит ли продукт чересчур много какого-либо из витамнов
                if((Float)map.getValue() > 1 ){
                    if(foodCounter>0){
                        return true;
                    }else{
                        return false;
                    }
                }
                //Смотрим, не является ли сумма витаминов комбинации чересчур большой
                if(vitaminEfficiency.get(map.getKey())+(Float)map.getValue() < 1.15f && acceptableNumberOfOverFlowingNutrients>0){
                    acceptableNumberOfOverFlowingNutrients--;
                } else if(vitaminEfficiency.get(map.getKey())+(Float)map.getValue() > 1.15f && acceptableNumberOfOverFlowingNutrients==0){
                    if(foodCounter == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }
                tmpVit.put(map.getKey(), vitaminEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpVit.put(map.getKey(), (Float)map.getValue());
            }
        }

        acceptableNumberOfOverFlowingNutrients = 3;
        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("acidEfficiency")).entrySet()){
            if(acidEfficiency.containsKey(map.getKey())){
                //Смотрим не содержит ли продукт чересчур много какого-либо из витамнов
                if((Float)map.getValue() > 1 ){
                    if(foodCounter>0){
                        return true;
                    }else{
                        return false;
                    }
                }
                //Смотрим, не является ли сумма витаминов комбинации чересчур большой
                if(acidEfficiency.get(map.getKey())+(Float)map.getValue() < 1.15f && acceptableNumberOfOverFlowingNutrients>0){
                    acceptableNumberOfOverFlowingNutrients--;
                } else if(acidEfficiency.get(map.getKey())+(Float)map.getValue() > 1.15f && acceptableNumberOfOverFlowingNutrients==0){
                    if(foodCounter == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }
                tmpAcid.put(map.getKey(), acidEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpAcid.put(map.getKey(), (Float)map.getValue());
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

        foodCounter--;

        return true;
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

        foods.remove((Food)food.get("food"));
        pfcOverall.substract((Food)food.get("food"));
        vitaminOverall.substract((Vitamin)food.get("vitamin"));
        mineralOverall.substract((Mineral)food.get("mineral"));
        acidOverall.substract((Acid)food.get("acid"));

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
        return Stream.of(doesPFCOverflow(), doesVitaminsOverflow(), doesMineralsOverflow(), doesAcidsOverflow())
                .collect(Collectors.toList());
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
