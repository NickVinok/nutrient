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

    private final List<String> MineralsNames = Stream.of("calcium", "phosphorus", "magnesium", "potassium",
            "sodium", "iron", "zinc", "copper", "manganese", "selenium", "fluoride")
            .collect(Collectors.toList());
    private final List<String> VitaminNames = Stream.of("vitamin_c", "vitamin_b1", "vitamin_b2", "vitamin_b6",
            "vitamin_b3","vitamin_b12", "vitamin_b9", "vitamin_b5", "alpha-carotin",
            "vitamin_a", "beta-carotin", "vitamin_e", "vitamin_d", "vitamin_k", "vitamin_b4")
            .collect(Collectors.toList());
    private final List<String> AcidNames = Stream.of("tryptophan","threonine","isoleucine","leucine","lysine",
            "methionine", "cystine", "phenylalanine","tyrosine","valine","arginine","histidine",
            "alanine","aspartic_acid","glutamic_acid","glycine","proline","serine")
            .collect(Collectors.toList());
    private final List<String> PFC = Stream.of("energy", "fat", "protein", "carbohydrate",
            "water","ash", "sugares", "fiber", "starch", "cholesterol", "fat_trans")
            .collect(Collectors.toList());

    //Возвращаем список еды, которая пододит под требования к категории
    public List<Food> getFoodWOProhibitedCategories(int dietRestrictions){
        List<String> notNeededCategories = new ArrayList<>();
        List<Category> notNeededCategoriesIds1 = new ArrayList<>();
        List<Food> foodLList = new ArrayList<>();
        switch (dietRestrictions){
            case 1: {
                notNeededCategories = Stream.of(
                        "Крупы и злаки (рис)",
                        "Лапша",
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
                notNeededCategoriesIds1 = categoryRepo.findByNameIn(notNeededCategories);
                List<Long> notNeededCategoriesIds = new ArrayList<>();
                for(Category category : notNeededCategoriesIds1) {
                    notNeededCategoriesIds.add(category.getId());
                }
                foodLList = foodRepo.findByCategory_IdNotIn(notNeededCategoriesIds);
                break;
            }
            case 2: {
                notNeededCategories = Stream.of(
                        "Овощи (клубнеплоды)",
                        "Овощи (конеплоды, коренья)",
                        "Овощи (луковичные)",
                        "Овощи (паслёновые)",
                        "Овощи (бахчевые)",
                        "Овощи (бобовые)",
                        "Овощи (бобовые - соя и соевые продукты)",
                        "Овощи (капустные)",
                        "Овощи (салатные)",
                        "Овощи (разные)",
                        "Мука, отруби, крахмал",
                        "Орехи",
                        "Хлеб, лепёшки и др.",
                        "Лапша",
                        "Яйца и яичные продукты",
                        "Сыры",
                        "Молоко и молочные продукты (кроме сыров)",
                        "Масла",
                        "Безалкогольные напитки",
                        "Майонез",
                        "Морские водоросли",
                        "Зелень, травы, листья, пряные овощи",
                        "Семена",
                        "Специи",
                        "Проростки",
                        "Грибы",
                        "Фрукты",
                        "Ягоды",
                        "Экзотические фрукты, ягоды и плоды",
                        "Кокос и продукты из кокоса",
                        "Сухофрукты",
                        "Соки, нектары, морсы",
                        "Крупы и злаки (в сухом виде)",
                        "Крупы и злаки (приготовленные)",
                        "Крупы и злаки (рис)"

                ).collect(Collectors.toList());
                notNeededCategoriesIds1 = categoryRepo.findByNameIn(notNeededCategories);
                List<Long> categorieIds = new ArrayList<>();
                for(Category category : notNeededCategoriesIds1) {
                    categorieIds.add(category.getId());
                }
                foodLList = foodRepo.findByCategory_IdIn(categorieIds);
                break;
            }
            case 3: {
                notNeededCategories = Stream.of(
                        "Овощи (клубнеплоды)",
                        "Овощи (конеплоды, коренья)",
                        "Овощи (луковичные)",
                        "Овощи (паслёновые)",
                        "Овощи (бахчевые)",
                        "Овощи (бобовые)",
                        "Овощи (бобовые - соя и соевые продукты)",
                        "Овощи (капустные)",
                        "Овощи (салатные)",
                        "Овощи (разные)",
                        "Мука, отруби, крахмал",
                        "Орехи",
                        "Хлеб, лепёшки и др.",
                        "Лапша",
                        "Масла",
                        "Безалкогольные напитки",
                        "Майонез",
                        "Морские водоросли",
                        "Зелень, травы, листья, пряные овощи",
                        "Семена",
                        "Специи",
                        "Проростки",
                        "Грибы",
                        "Фрукты",
                        "Ягоды",
                        "Экзотические фрукты, ягоды и плоды",
                        "Кокос и продукты из кокоса",
                        "Сухофрукты",
                        "Соки, нектары, морсы",
                        "Крупы и злаки (в сухом виде)",
                        "Крупы и злаки (приготовленные)",
                        "Крупы и злаки (рис)"

                ).collect(Collectors.toList());
                notNeededCategoriesIds1 = categoryRepo.findByNameIn(notNeededCategories);
                List<Long> categorieIds = new ArrayList<>();
                for(Category category : notNeededCategoriesIds1) {
                    categorieIds.add(category.getId());
                }
                foodLList = foodRepo.findByCategory_IdIn(categorieIds);
                break;
            }
            case 4: {
                notNeededCategories = Stream.of(
                        "Овощи (клубнеплоды)",
                        "Овощи (конеплоды, коренья)",
                        "Овощи (луковичные)",
                        "Овощи (паслёновые)",
                        "Овощи (бахчевые)",
                        "Овощи (бобовые)",
                        "Овощи (бобовые - соя и соевые продукты)",
                        "Овощи (капустные)",
                        "Овощи (салатные)",
                        "Овощи (разные)",
                        "Мука, отруби, крахмал",
                        "Орехи",
                        "Масла",
                        "Морские водоросли",
                        "Зелень, травы, листья, пряные овощи",
                        "Семена",
                        "Специи",
                        "Проростки",
                        "Грибы",
                        "Фрукты",
                        "Ягоды",
                        "Экзотические фрукты, ягоды и плоды",
                        "Кокос и продукты из кокоса",
                        "Сухофрукты",
                        "Соки, нектары, морсы",
                        "Крупы и злаки (в сухом виде)",
                        "Крупы и злаки (рис)"

                ).collect(Collectors.toList());
                notNeededCategoriesIds1 = categoryRepo.findByNameIn(notNeededCategories);
                List<Long> categorieIds = new ArrayList<>();
                for(Category category : notNeededCategoriesIds1) {
                    categorieIds.add(category.getId());
                }
                foodLList = foodRepo.findByCategory_IdIn(categorieIds);
                break;
            }
            case 5: {
                notNeededCategories = Stream.of(
                        "Овощи (клубнеплоды)",
                        "Овощи (конеплоды, коренья)",
                        "Овощи (паслёновые)",
                        "Овощи (бахчевые)",
                        "Овощи (бобовые - соя и соевые продукты)",
                        "Фрукты",
                        "Ягоды",
                        "Экзотические фрукты, ягоды и плоды"
                ).collect(Collectors.toList());
                notNeededCategoriesIds1 = categoryRepo.findByNameIn(notNeededCategories);
                List<Long> categorieIds = new ArrayList<>();
                for(Category category : notNeededCategoriesIds1) {
                    categorieIds.add(category.getId());
                }
                foodLList = foodRepo.findByCategory_IdIn(categorieIds);
                break;
            }
        }
        return foodLList;
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
                "Свинина (сырая)",
                "Свиные субпродукты (сырые)",
                "Говядина (сырая)",
                "Говяжьи субпродукты (сырые)",
                "Курица (сырая)",
                "Куриные субпродукты (сырые)",
                "Индейка (сырая)",
                "Мясо другое (сырое)",
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
                "Молоко и молочные продукты (кроме сыров)",
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

    public List<Food> getAllFood(){
        return foodRepo.findAll();
    }

    public HashMap<Long, HashMap<String, Object>> getFoodNutrientsForCustomCombination(List<Long> ids){
        List<Food> foods = foodRepo.findByIdIn(ids);
        HashMap<Long, HashMap<String, Object>> foodNutrientsList = new HashMap<>();
        for(Food f: foods){
            Long id = f.getId();
            HashMap<String, Object> tmp = new HashMap<String, Object>();
            tmp.put("food", f);
            tmp.put("mineral", mineralRepo.findByFood_id(id).get());
            tmp.put("vitamin", vitaminRepo.findByFood_id(id).get());
            tmp.put("acid", acidsRepo.findByFood_id(id).get());
            foodNutrientsList.put(id, tmp);
        }
        return  foodNutrientsList;
    }

    public List<String> getMineralsNames() {
        return MineralsNames;
    }

    public List<String> getVitaminNames() {
        return VitaminNames;
    }

    public List<String> getAcidNames() {
        return AcidNames;
    }

    public List<String> getPFC() {
        return PFC;
    }
}
