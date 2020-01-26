package com.nutrient.nutrientSpring.CalculationLogics;

import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import lombok.Data;
import org.springframework.stereotype.Service;

import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Data
public class NutrientFoodMapper {
    private HashMap<Field, Long> mapper = new HashMap<Field, Long>();


    public NutrientFoodMapper() throws NoSuchFieldException{
        mapper.put(Vitamin.class.getDeclaredField("vitamin_c"), 0L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_b1"), 1L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_b2"), 2L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_b6"), 3L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_b3"), 4L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_b12"), 5L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_b9"), 6L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_b5"), 7L);
        //mapper.put(Vitamin.class.getDeclaredField("vitamin_b7"), 8L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_a"), 9L);
        mapper.put(Vitamin.class.getDeclaredField("beta_carotene"), 10L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_e"), 11L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_d"), 12L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_k"), 13L);
        mapper.put(Mineral.class.getDeclaredField("calcium"), 14L);
        mapper.put(Mineral.class.getDeclaredField("phosphorus"), 15L);
        mapper.put(Mineral.class.getDeclaredField("magnesium"), 16L);
        mapper.put(Mineral.class.getDeclaredField("potassium"), 17L);
        mapper.put(Mineral.class.getDeclaredField("sodium"), 18L);
        mapper.put(Mineral.class.getDeclaredField("iron"), 20L);
        mapper.put(Mineral.class.getDeclaredField("zinc"), 21L);
        mapper.put(Mineral.class.getDeclaredField("copper"), 23L);
        mapper.put(Mineral.class.getDeclaredField("manganese"), 24L);
        mapper.put(Mineral.class.getDeclaredField("selen"), 25L);
        mapper.put(Mineral.class.getDeclaredField("fluorine"), 28L);
        mapper.put(Vitamin.class.getDeclaredField("vitamin_b4"), 29L);
    }

    public Long getNutrientsId(Field field){
        return mapper.get(field);
    }

    public List<Long> getMineralsId(){
        return Stream.of(14L,15L, 16L,17L,19L, 20L, 21L, 23L, 24L, 25L,28L)
                .collect(Collectors.toList());
    }
}
