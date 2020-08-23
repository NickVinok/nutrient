package com.nutrient.nutrientSpring.JsonObjects.NutrientREST;

import com.nutrient.nutrientSpring.Utils.Combination;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Data
public class Combinations {
    private List<Combination> combinationList = new ArrayList<>();
    public void addCombination(Combination c){
        combinationList.add(c);
    }
    private HashMap<Long, Long> overallCategoryCounter;

    public List<Combination> getCombinationsInCpfcCorridor(){
        List<Combination> combinationsWithinCorridor = new ArrayList<>();
        /*for (Combination c : combinationList){
            if(c.isCombinationCpfcInCorridor()){
                combinationsWithinCorridor.add(c);
                combinationList.remove(c);
            }
        }*/
        Iterator<Combination> i = this.combinationList.iterator();
        while(i.hasNext()){
            Combination c = i.next();
            if(c.isCombinationCpfcInCorridor()){
                combinationsWithinCorridor.add(c);
                i.remove();
            }
        }
        return combinationsWithinCorridor;
    }
}
