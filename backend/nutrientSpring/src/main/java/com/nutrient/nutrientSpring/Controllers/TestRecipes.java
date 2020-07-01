package com.nutrient.nutrientSpring.Controllers;

import com.nutrient.nutrientSpring.CalculationLogics.Calculations;
import com.nutrient.nutrientSpring.JsonObjects.DietInfo;
import com.nutrient.nutrientSpring.JsonObjects.NutrientNorms;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.CalculationResponse;
import com.nutrient.nutrientSpring.Services.FoodService;
import com.nutrient.nutrientSpring.Utils.TEST_FILE_FOR_GETTING_ALL_RECIPES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/test")
public class TestRecipes {
    @Autowired
    Calculations calculations;

    @PostMapping
    public CalculationResponse testRecipe(@RequestBody DietInfo dietInfo){
        CalculationResponse r = new CalculationResponse();
        r.setCombinations(calculations.actionsWithRecipes(
                dietInfo.getGender(),
                dietInfo.getWorkingGroup(),
                dietInfo.getAge(),
                dietInfo.getMass(),
                dietInfo.getHeight(),
                dietInfo.getDiet_type(),
                dietInfo.getDietRestrictions(),
                dietInfo.isPregnancy()
        ));

        r.setNorms(new NutrientNorms(calculations.getPfcNorms(),
                calculations.getVitaminNorms(), calculations.getAcidNorms(), calculations.getMineralNorms()));
        return r;
    }
    @GetMapping
    public void testMutability(){
        List<Integer> st = Stream.of(1,2,3,5).collect(Collectors.toList());
        List<Integer> stt = Stream.of(1,2,4,5).collect(Collectors.toList());
        List<Integer> sts = Stream.of(1,2,3,4).collect(Collectors.toList());
        List<Integer> stss = new ArrayList<>(sts);
        System.out.println(sts.size());
        mutate(Stream.of(st, stt, sts).collect(Collectors.toList()));
        System.out.println(sts.size());
    }

    private void mutate(List<List<Integer>> st){
        for(List<Integer> stt: st){
            stt.remove(3);
        }
    }

    private List<Integer> anotherMutate(List<Integer> st){
        return new ArrayList<>(st);
    }
}
