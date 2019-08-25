package com.nutrient.nutrientSpring.Services;

import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import com.nutrient.nutrientSpring.Model.JsonObjects.FoodRest.PackedJsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PackedFoodService {
    @Autowired
    FoodService foodService;

    public List<PackedJsonObject> getPackedFood(){
        HashMap<Long, HashMap<String, Object>>foodWithNutrients = foodService.getListOfFoodsNutrients(foodService.getAllFood());
        List<PackedJsonObject> listOfFood = new ArrayList<>();
        for (Map.Entry<Long, HashMap<String, Object>> foodEntry: foodWithNutrients.entrySet()) {
            PackedJsonObject packedFood = new PackedJsonObject();
            //Ставим айдишник объекту
            packedFood.setId(foodEntry.getKey());
            packedFood.setType("food");
            Food f = (Food)foodEntry.getValue().get("food");
            //Заполняем аттрибуты еды
            List<Float> nutrients = f.getValues();
            List<String> names = foodService.getPFC();
            for (int i = 0; i<nutrients.size(); i++){
                packedFood.addAttribute(names.get(i), nutrients.get(i));
            }
            packedFood.addAttribute("name", f.getName());

            //Заполняем минералы еды
            nutrients = ((Mineral)foodEntry.getValue().get("mineral")).getValues();
            names = foodService.getMineralsNames();
            HashMap<String, Float> relationshipValues = new HashMap<>();
            for(int i = 0; i<nutrients.size();i++) {
                relationshipValues.put(names.get(i), nutrients.get(i));
            }
            packedFood.addRelationship("mineral", relationshipValues);

            //Заполняем витамины еды
            nutrients = ((Vitamin)foodEntry.getValue().get("vitamin")).getValues();
            names = foodService.getVitaminNames();
            relationshipValues = new HashMap<>();
            for(int i = 0; i<nutrients.size();i++) {
                relationshipValues.put(names.get(i), nutrients.get(i));
            }
            packedFood.addRelationship("vitamin", relationshipValues);

            //Заполняем кислоты еды
            nutrients = ((Acid)foodEntry.getValue().get("acid")).getValues();
            names = foodService.getAcidNames();
            relationshipValues = new HashMap<>();
            for(int i = 0; i<nutrients.size();i++) {
                relationshipValues.put(names.get(i), nutrients.get(i));
            }
            packedFood.addRelationship("acid", relationshipValues);

            packedFood.addRelationship("category",
                    f.getCategory());

            listOfFood.add(packedFood);
        }
        return listOfFood;
    }
}
