package com.nutrient.nutrientSpring.Model.JsonObjects;

import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
public class Combination{
    private List<Food> combination = new ArrayList<>();
    private Food pfcOverall = new Food(-1L, "combination", 0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f ,null);
    private Vitamin vitaminOverall = new Vitamin(-1L, 0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,null);
    private Mineral mineralOverall = new Mineral(-1L, 0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,null);
    private HashMap<String, Float> vitaminEfficiency = new HashMap<>();
    private HashMap<String, Float> mineralEfficiency = new HashMap<>();
    private HashMap<String, Float> pfcEfficiency = new HashMap<>();;

    private Float combinationEfficiency = 0f;
    private Float pfcOverallEfficiency = 0f;
    private Float vitaminOverallEfficiency = 0f;
    private Float mineralOverallEfficiency = 0f;

    public boolean addFoodToCombination(HashMap<String, Object> food){
        int count = 3;

        HashMap<String, Float> tmpPfc = new HashMap<>();
        HashMap<String, Float> tmpVit = new HashMap<>();
        HashMap<String, Float> tmpMin = new HashMap<>();

        for(Map.Entry<String, Float> old : pfcEfficiency.entrySet()){
            tmpPfc.put(old.getKey(), old.getValue());
        }
        for(Map.Entry<String, Float> old : vitaminEfficiency.entrySet()){
            tmpVit.put(old.getKey(), old.getValue());
        }
        for(Map.Entry<String, Float> old : mineralEfficiency.entrySet()){
            tmpMin.put(old.getKey(), old.getValue());
        }

        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("pfcEfficiency")).entrySet()){
            if(pfcEfficiency.containsKey(map.getKey())){
                if(pfcEfficiency.get(map.getKey())+(Float)map.getValue() > 1.1f && count >0)
                {
                    count--;
                }
                else if(pfcEfficiency.get(map.getKey())+(Float)map.getValue() > 1.1f && count ==0){
                    return false;
                }
                tmpPfc.put(map.getKey(), pfcEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpPfc.put(map.getKey(), (Float)map.getValue());
            }
        }

        count = 3;
        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("mineralEfficiency")).entrySet()){
            if(mineralEfficiency.containsKey(map.getKey())){
                if(mineralEfficiency.get(map.getKey())+(Float)map.getValue() > 1.15f && count>0){
                    count--;
                }else if(mineralEfficiency.get(map.getKey())+(Float)map.getValue() > 1.15f && count==0){
                    return false;
                }
                tmpMin.put(map.getKey(), mineralEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpMin.put(map.getKey(), (Float)map.getValue());
            }
        }

        count = 3;
        for(Map.Entry<String, Object> map : ((HashMap<String, Object>)food.get("vitaminEfficiency")).entrySet()){
            if(vitaminEfficiency.containsKey(map.getKey())){
                if(vitaminEfficiency.get(map.getKey())+(Float)map.getValue() < 1.15f && count>0){
                    count--;
                } else if(vitaminEfficiency.get(map.getKey())+(Float)map.getValue() > 1.15f && count==0){
                    return false;
                }
                tmpVit.put(map.getKey(), vitaminEfficiency.get(map.getKey())+(Float)map.getValue());
            }
            else{
                tmpVit.put(map.getKey(), (Float)map.getValue());
            }
        }

        pfcEfficiency = tmpPfc;
        vitaminEfficiency = tmpVit;
        mineralEfficiency = tmpMin;

        combination.add((Food)food.get("food"));

        pfcOverall.sum((Food)food.get("food"));
        vitaminOverall.sum((Vitamin)food.get("vitamin"));
        mineralOverall.sum((Mineral)food.get("mineral"));

        combinationEfficiency += (Float)food.get("overallEfficiency");
        pfcOverallEfficiency += (Float)((HashMap<String, Object>)food.get("pfcEfficiency"))
                .get("overallPfcEfficiency");
        vitaminOverallEfficiency += (Float)((HashMap<String, Object>)food.get("vitaminEfficiency"))
                .get("overallVitaminEfficiency");
        mineralOverallEfficiency += (Float)((HashMap<String, Object>)food.get("mineralEfficiency"))
                .get("overallMineralEfficiency");

        return true;
    }
}
