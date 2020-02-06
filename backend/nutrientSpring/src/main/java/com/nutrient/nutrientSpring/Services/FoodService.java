package com.nutrient.nutrientSpring.Services;

import com.nutrient.nutrientSpring.Model.FoodModel.*;
import com.nutrient.nutrientSpring.Repos.FoodRepository.*;
import com.nutrient.nutrientSpring.Utils.FoodAndCategoriesLimitationTable;
import com.nutrient.nutrientSpring.Utils.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @Autowired
    private EnabledCategoriesRepo enabledCategoriesRepo;
    @Autowired
    private EnabledProductsRepo enabledProductsRepo;
    @Autowired
    private DietTypesRepo dietTypesRepo;
    @Autowired
    private CategoryLimitRepo categoryLimitRepo;
    @Autowired
    private FoodLimitRepo foodLimitRepo;

    private FoodAndCategoriesLimitationTable limitationTable;

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
        List<Food> foodList;

        List<EnabledCategories> tmp1 = enabledCategoriesRepo.findByDietAndEnable(dietTypesRepo.getOne((long)dietRestrictions), true);
        List<EnabledProducts> tmp2 = enabledProductsRepo.findByDietAndEnable(dietTypesRepo.getOne((long)dietRestrictions), true);
        List<Long> enabledC = tmp1
                .stream()
                .map(EnabledCategories::getCategory)
                .map(Category::getId)
                .collect(Collectors.toList());
        List<Long> enabledF = tmp2
                .stream()
                .map(EnabledProducts::getFood)
                .map(Food::getId)
                .collect(Collectors.toList());

        foodList = foodRepo.findByIdInAndCategory_IdIn(enabledF, enabledC);


        List<CategoryLimit> categoryLimits = categoryLimitRepo.findByCategory_IdIn(enabledC);
        List<FoodLimit> foodLimits = foodLimitRepo.findByFood_IdIn(enabledF);
        limitationTable = new FoodAndCategoriesLimitationTable(foodLimits, categoryLimits);

        return foodList;
    }

    public List<Ingredient> getListOfIngredients(List<Food> foodList){
        List<Ingredient> ingredients = new ArrayList<>();

        for(Food food : foodList){
            Long id = food.getId();
            if(food.getGeneral().equals(null) || food.getGeneral()==0){
                continue;
            }
            //вот здесь пытаемся вытащить категорию еды, но её назуй
            //потом норм сделаю
            Category cat = food.getCategory();
            Pattern p = Pattern.compile("\\((.*)\\)");
            Matcher match = p.matcher(cat.getName());
            String status = "-";
            if(match.find()){
                status = match.group();
                cat.setStatus(status.split("[\\(||//)]")[1]);
            } else{
                cat.setStatus(status);
            }
            food.setCategory(cat);

            Ingredient ingredient = new Ingredient(food,
                    vitaminRepo.findByFood_id(id).get(),
                    mineralRepo.findByFood_id(id).get(),
                    acidsRepo.findByFood_id(id).get());
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    //Возвращаем список еды, с прикреплёнными значениями кислоты, минералов, витаминов и БЖУ
    public HashMap<Long, HashMap<String, Object>> getListOfFoodsNutrients(List<Food> foodList){
        HashMap<Long, HashMap<String, Object>> foodNutrients = new HashMap<Long, HashMap<String, Object>>();

        for(Food food : foodList){
            Long id = food.getId();
            HashMap<String, Object> tmp = new HashMap<String, Object>();

            Category cat = food.getCategory();
            Pattern p = Pattern.compile("\\((.*)\\)");
            Matcher match = p.matcher(cat.getName());
            String status = "-";
            if(match.find()){
                status = match.group();
                cat.setStatus(status.split("[\\(||//)]")[1]);
            } else{
                cat.setStatus(status);
            }
            food.setCategory(cat);

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
                "Рыба (сырая)",
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
            categoriesCounter.put(id, 10L);
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
                entry.setValue(6L);
            }
        }

        return categoriesCounter;
    }

    public List<Food> getAllFood(){
        return foodRepo.findAll();
    }

    public FoodAndCategoriesLimitationTable getLimitations(){
        return this.limitationTable;
    }

    //переделать
    public List<Ingredient> getProductsForCustomCombination(List<Long> ids){
        List<Food> foods = foodRepo.findByIdIn(ids);
        List<FoodLimit> foodLimits = foodLimitRepo.findByFood_IdIn(ids);
        List<CategoryLimit> categoryLimits = categoryLimitRepo.findByCategory_IdIn(
                foods.stream()
                        .map(Food::getCategory)
                        .map(Category::getId)
                        .collect(Collectors.toList()));

        List<Ingredient> products = new ArrayList<>();
        for(Food f: foods){
            if(f.getGeneral().equals(null) || f.getGeneral()==0){
                continue;
            }
            Category cat = f.getCategory();
            Pattern p = Pattern.compile("\\((.*)\\)");
            Matcher match = p.matcher(cat.getName());
            String status = "-";
            if(match.find()){
                status = match.group();

                cat.setStatus(status.split("[(||//)]")[1]);
            } else{
                cat.setStatus(status);
            }
            f.setCategory(cat);

            Ingredient ingredient = new Ingredient(f,
                    vitaminRepo.findByFood_id(f.getId()).get(),
                    mineralRepo.findByFood_id(f.getId()).get(),
                    acidsRepo.findByFood_id(f.getId()).get());
            products.add(ingredient);
        }
        limitationTable = new FoodAndCategoriesLimitationTable(foodLimits, categoryLimits);
        return  products;
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
