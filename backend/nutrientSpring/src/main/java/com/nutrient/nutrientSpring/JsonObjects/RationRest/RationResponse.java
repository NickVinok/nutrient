package com.nutrient.nutrientSpring.JsonObjects.RationRest;

import com.nutrient.nutrientSpring.JsonObjects.NutrientNorms;
import com.nutrient.nutrientSpring.Utils.Ration.Ration;
import lombok.Data;

import java.util.List;

@Data
public class RationResponse {
    NutrientNorms nutrientNorms;
    double avgRationEfficiency;
    List<Ration> ration;

    public void calculateRationAvg(){
        this.avgRationEfficiency=this.ration.stream()
                .map(Ration::getRationEfficiency)
                .reduce(0d, Double::sum)/this.ration.size();
    }
}
