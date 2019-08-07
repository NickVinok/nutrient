package com.nutrient.nutrientSpring.Services;

import com.nutrient.nutrientSpring.Model.FoodModel.Category;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Repos.FoodRepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FoodService {
    @Autowired
    private AcidsRepo acidsRepo;
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private MineralRepo mineralRepo;
    @Autowired
    private VitaminRepo vitaminRepo;

    //Возвращаем список еды, которая пододит под требования к категории
    public List<Food> getFoodWOProhibitedCategories(){
        List<String> notNeededCategories = Stream.of(
                "Крупы и злаки (рис)",
                "Лапша в сухом виде",
                "Яйца и яичные продукты",
                "Протеин порошок",
                "Рыба (сырая)",
                "Свинина (сырая)",
                "Свиные субпродукты (сырые)",
                "Говядина (сырая)",
                "Говяжьи субпродукты сырые",
                "Курица сырая",
                "Куриные субпродукты сырые",
                "Индейка сырая",
                "Мясо другое сырое ",
                "Специи",
                "Соки, нектары, морсы",
                "Животные жиры",
                "Алкогольные напитки",
                "Безалкогольные напитки",
                "Уксус",
                "Фастфуд",
                "Моллюски сырые",
                "Майонез",
                "Раки, крабы, креветки сырые",
                "Свинина (приготовленная)",
                "Свиные субпродукты (приготовленные)",
                "Говядина (приготовленная)",
                "Говяжьи субпродукты (приготовленные)",
                "Курица (приготовленная)",
                "Куриные субпродукты (приготовленные)",
                "Индейка (приготовленная)",
                "Мясо другое (приготовленное)",
                "Мясные продукты",
                "Кондитерские изделия, печенье, сладости",
                "Сахар и заменители",
                "Шоколад"
                )
                .collect(Collectors.toList());

        List<Category> notNeededCategoriesIds1 = categoryRepo.findByNameIn(notNeededCategories);

        List<Long> notNeededCategoriesIds = new ArrayList<>();
        for(Category category : notNeededCategoriesIds1) {
            notNeededCategoriesIds.add(category.getId());
        }

        System.out.println(notNeededCategoriesIds.size());

        List<Food> tmp = foodRepo.findByCategory_IdNotIn(notNeededCategoriesIds);
        System.out.println(tmp.size());
        return foodRepo.findByCategory_IdNotIn(notNeededCategoriesIds);
    }

    public List<Category> getCategories(){
        List<String> notNeededCategories = Stream.of(
                "Рис в сухом виде",
                "Лапша в сухом виде",
                "Яйца в сыром и сухом виде",
                "Протеин порошок",
                "Рыба сырая",
                "Свинина сырая",
                "Свиные субпродукты сырые",
                "Говядина сырая",
                "Говяжьи субпродукты сырые",
                "Курица сырая",
                "Куриные субпродукты сырые",
                "Индейка сырая",
                "Мясо другое сырое ",
                "Приправы",
                "Соки, нектары, морсы",
                "Животные жиры",
                "Алкогольные напитки",
                "Безалкогольные напитки",
                "Уксус",
                "Фастфуд",
                "Моллюски сырые",
                "Майонез",
                "Раки, крабы, креветки сырые",
                "Свинина (приготовленная)",
                "Свиные субпродукты приготовленные",
                "Говядина приготовленная",
                "Говяжьи субпродукты (приготовленные)",
                "Курица (приготовленная)",
                "Куриные субпродукты (приготовленные)",
                "Индейка (приготовленная)",
                "Мясо другое (приготовленное)",
                "Мясные продукты",
                "Кондитерские изделия, печенье, сладости",
                "Сахар и заменители",
                "Шоколад"
        )
                .collect(Collectors.toList());

        return categoryRepo.findByNameNotIn(notNeededCategories);
    }

    //Возвращаем список еды, с прикреплёнными значениями кислоты, минералов, витаминов и БЖУ
    public HashMap<Long, HashMap<String, Object>> getListOfFoodsNutrients(List<Food> foodList){
        HashMap<Long, HashMap<String, Object>> foodNutrients = new HashMap<Long, HashMap<String, Object>>();

        for(Food food : foodList){
            Long id = food.getId();
            HashMap<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("food", food);
            tmp.put("mineral", mineralRepo.findByFood_id(id).get());
            tmp.put("vitamin", vitaminRepo.findByFood_id(id).get());
            tmp.put("acid", acidsRepo.findByFood_id(id).get());
            foodNutrients.put(id, tmp);
        }
        return foodNutrients;
    }

    public HashMap<Long, Long> getCategoriesCounter(){
        HashMap<Long, Long> categoriesCounter = new HashMap<>();
        List<String> notNeededCategories = Stream.of(
                "Рис в сухом виде",
                "Лапша в сухом виде",
                "Яйца в сыром и сухом виде",
                "Протеин порошок",
                "Рыба сырая",
                "Свинина сырая",
                "Свиные субпродукты сырые",
                "Говядина сырая",
                "Говяжьи субпродукты сырые",
                "Курица сырая",
                "Куриные субпродукты сырые",
                "Индейка сырая",
                "Мясо другое сырое ",
                "Приправы",
                "Соки, нектары, морсы",
                "Животные жиры",
                "Алкогольные напитки",
                "Безалкогольные напитки",
                "Уксус",
                "Фастфуд",
                "Моллюски сырые",
                "Майонез",
                "Раки, крабы, креветки сырые",
                "Свинина (приготовленная)",
                "Свиные субпродукты приготовленные",
                "Говядина приготовленная",
                "Говяжьи субпродукты (приготовленные)",
                "Курица (приготовленная)",
                "Куриные субпродукты (приготовленные)",
                "Индейка (приготовленная)",
                "Мясо другое (приготовленное)",
                "Мясные продукты",
                "Кондитерские изделия, печенье, сладости",
                "Сахар и заменители",
                "Шоколад"
        )
                .collect(Collectors.toList());
        List<Long> neededCategories = categoryRepo.findByNameNotIn(notNeededCategories).stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        for(Long id : neededCategories){
            categoriesCounter.put(id, 6L);
        }

        List<String> namesOfRestrictedCategories = Stream.of(
                "Кокос и продукты из кокоса",
                "Ягоды",
                "Экзотические фрукты, ягоды и плоды",
                "Сухофрукты",
                "Соки, нектары, морсы",
                "Крупы и злаки (приготовленные)",
                "Крупы и злаки (рис)",
                "Мука, отруби, крахмал",
                "Орехи",
                "Хлеб, лепешки и другое",
                "Лапша",
                "Яйца и яичные продукты",
                "Сыры",
                "Молоко и молочные продукты",
                "Масла"
        )
                .collect(Collectors.toList());

        List<Long> categoriesWithRestrictions = categoryRepo.findByNameIn(namesOfRestrictedCategories).stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        for(Map.Entry<Long, Long> entry: categoriesCounter.entrySet()){
            if(categoriesWithRestrictions.contains(entry.getKey())){
                entry.setValue(2L);
            }
        }

        return categoriesCounter;
    }

}
