package com.nutrient.nutrientSpring.CalculationLogics;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNormsCalculation;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.Combinations;
import com.nutrient.nutrientSpring.Model.FoodModel.*;
import com.nutrient.nutrientSpring.Services.FoodService;
import com.nutrient.nutrientSpring.Services.NutrientService;
import com.nutrient.nutrientSpring.Utils.Combination;
import com.nutrient.nutrientSpring.Utils.FoodAndCategoriesLimitationTable;
import com.nutrient.nutrientSpring.Utils.Ingredient;
import com.nutrient.nutrientSpring.Utils.Ration.Ration;
import com.nutrient.nutrientSpring.Utils.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class Calculations {
    @Autowired
    private NutrientService nutrientService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private NutrientFoodMapper mapper;

    private PfcNormsCalculation pfcNormsCalculation;
    private PfcNorms pfcNormsToController;
    private Food foodNorms;
    private Acid acidNorms;
    private Vitamin vitaminNorms;
    private Mineral mineralNorms;

    public Combinations getEfficientCombinations(
            String gender, int workingGroup, float age, float weight, float height, String dietType, int dietRestrictions, boolean pregnancy) {
        Combinations combinations = new Combinations();
        //Получаем список словарей, где ключом выступает id еды, а значениями являются объекты еды, витаминов, минералов, кислот)
        List<Ingredient> ingredients = foodService.getListOfIngredients(foodService.getFoodWOProhibitedCategories(dietRestrictions));

        //HashMap<Long, HashMap<String, Object>> foodWithNutrientsUnsortedList = foodService.getListOfFoodsNutrients(
        //       foodService.getFoodWOProhibitedCategories(dietRestrictions));

        //Получаем список объектов значений нутриентов для конкретного пола
        this.calculateNormsForPerson(gender, workingGroup, age, weight, height, dietType, dietRestrictions, pregnancy);

        //Рассчитываем эффективность каждого из продуктов
        List<Ingredient> foodWithEfficiency = productOverallEfficiency(ingredients, foodNorms, vitaminNorms,
                mineralNorms, acidNorms, false);
        //Получаем список категорий, превращаем в словарь, где значение - допустимое количество оставшихся использований
        //Делаем 2 списка: один локальный, другой глобальный для выполнения требований к максимальному количеству продуктов из одной группы внутри комбинации
        //и во всех комбинациях
        FoodAndCategoriesLimitationTable limitationTable = foodService.getLimitations();

        //Непосредственный расчёт: передаём список допустимой еды, нормы БЖУ, нормы нутриентов
        combinations = calculateEfficientCombinations(limitationTable, foodWithEfficiency, null);

        combinations.setCombinationList(
                combinations.getCombinationList().stream()
                        .filter(x -> x.getProducts().size() != 0)
                        .collect(Collectors.toList())
        );

        for (int i = 0; i < 10; i++) {
            combinations = optimizeCombinations(ingredients, combinations);
            combinations = addFoodToOptimizedCombination(combinations, ingredients);
        }

        combinations.setCombinationList(
                combinations.getCombinationList().stream()
                        .sorted((x, y) -> Float.compare(y.getCombinationEfficiency(), x.getCombinationEfficiency()))
                        .collect(Collectors.toList())
        );
        return combinations;
    }

    private List<Ingredient> productOverallEfficiency(List<Ingredient> ingredients, Food pfcNorms,
                                                      Vitamin vitaminNorms, Mineral mineralNorms, Acid acidNorms, boolean isCustom) {
        List<Ingredient> tmp = new ArrayList<>();
        if (isCustom) {
            for (Ingredient in : ingredients) {
                Mineral m = in.getMineral();
                Acid a = in.getAcid();
                Vitamin v = in.getVitamin();
                Food f = in.getFood();

                Mineral mEf = new Mineral(m, mineralNorms);
                Acid aEf = new Acid(a, acidNorms);
                Vitamin vEf = new Vitamin(v, vitaminNorms);
                Food fEf = new Food(f, pfcNorms);

                in.setEfficiency(fEf, vEf, mEf, aEf);
                tmp.add(in);
            }
            return tmp;
        }

        List<Ingredient> productsWithNegativePoints = new ArrayList<>();
        for (Ingredient in : ingredients) {
            Mineral m = in.getMineral();
            Acid a = in.getAcid();
            Vitamin v = in.getVitamin();
            Food f = in.getFood();

            Mineral mEf = new Mineral(m, mineralNorms);
            Acid aEf = new Acid(a, acidNorms);
            Vitamin vEf = new Vitamin(v, vitaminNorms);
            Food fEf = new Food(f, pfcNorms);

            in.setEfficiency(fEf, vEf, mEf, aEf);
            if (in.calculateOverallMineralEfficiency() < 2 && in.calculateOverallVitaminEfficiency() < 2 &&
                    in.calculateOverallAcidEfficiency() < 2 && in.calculateOverallFoodEfficiency() < 2 && in.compare(1f)) {
                //Если у нас получились неотрицательные баллы добавляем в список продуктов
                if (in.calculateOverallIngredientEfficiency() > 0) {
                    tmp.add(in);
                }
                //Если получились отрицательные баллы добавляем в список,
                //который потом будем корректировать, уменьшая граммовку
                else {
                    productsWithNegativePoints.add(in);
                }
            } else {
                continue;
            }
        }
        //сортируем полученный список элементов
        List<Double> tmp2 = tmp.stream()
                .map(Ingredient::calculateOverallIngredientEfficiency)
                .sorted((x, y) -> Float.compare(y, x))
                .map(Float::doubleValue)
                .collect(Collectors.toList());
        //Смотрим на самый ээфективный продукт на 100г. и на самый неэффективный на 100г.
        double mostEffective = tmp2.get(0);
        double leastEffective = tmp2.get(tmp.size() - 1);
        //Делим интервал между ними на 6 частей
        //И в зависмости от нахождения продукта в интервале
        //умножаем на тот или иной коэффициент
        //Добавляем в итоговый массив
        double interval = (mostEffective - leastEffective) / 5;
        List<Ingredient> listOfAdjustedIngredients = new ArrayList<>();
        for (Ingredient i : tmp) {
            double overall = i.calculateOverallIngredientEfficiency();
            if (overall < interval) {
                i.multiply(3f);
            } else if (overall > interval && overall < 2 * interval) {
                i.multiply(2.5f);
            } else if (overall > 2 * interval && overall < 3 * interval) {
                i.multiply(2f);
            } else if (overall > 3 * interval && overall < 4 * interval) {
                i.multiply(1.5f);
            } else if (overall > 4 * interval && overall < 5 * interval) {
                i.multiply(1f);
            } else {
                i.multiply(0.5f);
            }
            if (i.calculateOverallMineralEfficiency() < 2 && i.calculateOverallVitaminEfficiency() < 2 &&
                    i.calculateOverallAcidEfficiency() < 2 && i.calculateOverallFoodEfficiency() < 2 && i.compare(1f)) {
                if (i.calculateOverallIngredientEfficiency() > 0) {
                    listOfAdjustedIngredients.add(i);
                } else {
                    productsWithNegativePoints.add(i);
                }
            } else {
                continue;
            }
        }
        //Проходим по продуктам с отрицательными баллами
        //Уменьшаем их вес
        //Если после двойного уменьшения веса, баллы всё ещё отрицательные,
        //то не добавляем в итоговый массив
        for (Ingredient i : productsWithNegativePoints) {
            i.multiply(0.5f);
            if (i.calculateOverallIngredientEfficiency() > 0) {
                listOfAdjustedIngredients.add(i);
            } else {
                i.multiply(0.5f);
                if (i.calculateOverallIngredientEfficiency() > 0) {
                    listOfAdjustedIngredients.add(i);
                }
            }
        }
        //Возвращаем отсортированный массив, от большего к меньшему
        return listOfAdjustedIngredients.stream()
                .sorted((i1, i2) -> Float.compare(
                        i2.calculateOverallIngredientEfficiency(),
                        i1.calculateOverallIngredientEfficiency()
                ))
                .collect(Collectors.toList());
    }


    private Combinations calculateEfficientCombinations(
            FoodAndCategoriesLimitationTable limits, List<Ingredient> sortedFood, List<Combination> combinationList) {
        Combinations finalCombinations = new Combinations();
        //Чтобы не было повторений
        //Т.е. в течение скольких циклов составления комбинаций данный ингридиент будет игнорироваться
        HashMap<Long, Long> usedIds2 = new HashMap<>();
        if (combinationList != null) {
            Combinations result = new Combinations();
            for (Combination comb : combinationList) {
                comb.setLimitationTable(limits);
                for (Ingredient ingredient : sortedFood) {
                    Long productId = ingredient.getId();
                    //Если данный ингридиент долго не использовался в комбинациях
                    //(т.е. счётчик игнорирования == 0)
                    //то мы возвращаем ингридиент в пулл
                    if (usedIds2.containsValue(0)) {
                        for (Map.Entry<Long, Long> keyVal : usedIds2.entrySet()) {
                            if (keyVal.getValue() == 0) {
                                usedIds2.remove(keyVal.getKey());
                            }
                        }
                    }
                    //System.out.println(limits.getSingleTable());
                    Long categoryId = ingredient.getFood().getCategory().getId();


                    if (limits.isCategoryAllowed(categoryId)) {
                        if (limits.getCategoryLimitInAllCombs(categoryId) > 0 && limits.getCategoryLimitInComb(categoryId) > 0) {
                            if (!usedIds2.containsKey(ingredient.getId())) {
                                if (limits.getFoodLimit(productId) > 0) {
                                    if (comb.addProductToCombination(ingredient, limits)) {
                                        if (Math.random() > 0.5) {
                                            usedIds2.put(ingredient.getId(), (long) (1 + Math.random() * 3));
                                        }
                                        limits.updateCategoryLimit(categoryId, -1);
                                        limits.updateFoodLimit(productId, -1);
                                    }

                                } else {
                                    continue;
                                }
                            }

                        }
                    }
                }
            }
            result.setCombinationList(combinationList);
            return result;
        }
        for (int i = 0; i < 12; i++) {
            Combination combinationToAdd = new Combination();

            for (Ingredient ingredient : sortedFood) {
                Long productId = ingredient.getId();
                //Если данный ингридиент долго не использовался в комбинациях
                //(т.е. счётчик игнорирования == 0)
                //то мы возвращаем ингридиент в пулл
                if (usedIds2.containsValue(0)) {
                    for (Map.Entry<Long, Long> keyVal : usedIds2.entrySet()) {
                        if (keyVal.getValue() == 0) {
                            usedIds2.remove(keyVal.getKey());
                        }
                    }
                }
                //System.out.println(limits.getSingleTable());
                Long categoryId = ingredient.getFood().getCategory().getId();


                if (limits.isCategoryAllowed(categoryId)) {
                    if (limits.getCategoryLimitInAllCombs(categoryId) > 0 && limits.getCategoryLimitInComb(categoryId) > 0) {
                        if (!usedIds2.containsKey(ingredient.getId())) {
                            if (limits.getFoodLimit(productId) > 0) {
                                if (combinationToAdd.addProductToCombination(ingredient, limits)) {
                                    if (Math.random() > 0.5) {
                                        usedIds2.put(ingredient.getId(), (long) (1 + Math.random() * 3));
                                    }
                                    limits.updateCategoryLimit(categoryId, -1);
                                    limits.updateFoodLimit(productId, -1);
                                }

                            } else {
                                continue;
                            }
                        }

                    }
                }
            }
            finalCombinations.addCombination(combinationToAdd);
        }
        return finalCombinations;
    }


    private Combinations addFoodToOptimizedCombination(
            Combinations combs, List<Ingredient> products) {
        List<Ingredient> localProducts;
        HashMap<Long, Long> usedIds2 = new HashMap<>();
        for (Combination c : combs.getCombinationList()) {
            Ingredient combinationsOverall = c.getOverallNutrientsAndEfficiency();
            localProducts = products;
            if (localProducts == null) {
                break;
            }

            FoodAndCategoriesLimitationTable limits = c.getLimitationTable();

            for (Ingredient i : localProducts) {
                if (c.isProductInCombination(i)) {
                    continue;
                }

                if (usedIds2.containsValue(0)) {
                    for (Map.Entry<Long, Long> keyVal : usedIds2.entrySet()) {
                        if (keyVal.getValue() == 0) {
                            usedIds2.remove(keyVal.getKey());
                        }
                    }
                }

                Long productId = i.getId();
                Long categoryId = i.getFood().getCategory().getId();

                if (limits.isCategoryAllowed(categoryId)) {
                    if (limits.getCategoryLimitInAllCombs(categoryId) > 0 && limits.getCategoryLimitInComb(categoryId) > 0) {
                        if (!usedIds2.containsKey(productId) && limits.getFoodLimit(productId) > 0) {
                            if (c.addProductToCombination(i, limits)) {
                                if (Math.random() > 0.7) {
                                    usedIds2.put(i.getId(), (long) (1 + Math.random() * 3));
                                }
                                limits.updateCategoryLimit(categoryId, -1);
                                limits.updateFoodLimit(productId, -1);
                                c.setLimitationTable(limits);
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
        return combs;
    }

    public PfcNorms getPfcNorms() {
        return pfcNormsToController;
    }

    public Combinations optimizeCombinations(List<Ingredient> products, Combinations unOptimizedCombinations) {
        for (Combination comb : unOptimizedCombinations.getCombinationList()) {
            int counter = 5;
            for (int i = 0; i < counter; i++) {
                Ingredient productTobeModified = comb.getMostOverflowingProduct();
                if (productTobeModified == null) {
                    break;
                }
                comb.deleteFoodFromCombination(productTobeModified);
                Float gramFixCoef = 0.5f;
                productTobeModified.multiply(gramFixCoef);
                //Если продукта, который мы уменьшали сатло слишком мало - не возвращаем его в комбинацию
                if (productTobeModified.getGram() < 10f) {
                    break;
                }
                comb.addFoodToCustomCombination(productTobeModified);
            }
        }
        return unOptimizedCombinations;
    }

    public Combination calculateCustomCombination(String gender, int workingGroup, float age, float weight, float height, String dietType,
                                                  int dietRestrictions, boolean pregnancy, List<HashMap<String, Long>> idsWithGrams) {

        List<Long> ids = new ArrayList<>();
        HashMap<Long, Integer> actualIdsGrams = new HashMap<>();
        for (HashMap<String, Long> hs : idsWithGrams) {
            ids.add(hs.get("id"));
            actualIdsGrams.put(hs.get("id"), hs.get("gram").intValue());
        }
        List<Ingredient> products = foodService.getProductsForCustomCombination(ids);
        //Получаем список объектов значений нутриентов для конкретного пола
        this.calculateNormsForPerson(gender, workingGroup, age, weight, height, dietType, dietRestrictions, pregnancy);

        FoodAndCategoriesLimitationTable limits = foodService.getLimitations();

        //Рассчитываем эффективность каждого из продуктов
        productOverallEfficiency(products, foodNorms,
                vitaminNorms, mineralNorms, acidNorms, true);

        Combination result = new Combination();
        result.setLimitationTable(limits);
        for (Ingredient product : products) {
            Long id = product.getId();
            int numberOfGrams = actualIdsGrams.get(id);
            product.multiply(numberOfGrams / 100f);
            result.addFoodToCustomCombination(product);
        }

        return result;
    }

    public Acid getAcidNorms() {
        return acidNorms;
    }

    public Vitamin getVitaminNorms() {
        return vitaminNorms;
    }

    public Mineral getMineralNorms() {
        return mineralNorms;
    }

    public void calculateNormsForPerson(
            String gender, int workingGroup, float age, float weight, float height, String dietType, int dietRestrictions, boolean pregnancy) {
        nutrientService.getNutrientsValueForGender(weight, gender, age, pregnancy, false);
        //Рассчитываем Нрмы БЖУ, исходя из роста, веса, пола и т.д.)
        pfcNormsCalculation = new PfcNormsCalculation(gender, age, weight, height, dietType, workingGroup);
        //Рассчитываем норму золы
        acidNorms = nutrientService.getAcidNorms();
        vitaminNorms = nutrientService.getVitaminNorms();
        mineralNorms = nutrientService.getMineralNorms();
        List<Long> mineralIds = mapper.getMineralsId();
        pfcNormsCalculation.setAsh(nutrientService.getAshNorm());
        //Получаем список норм БЖУ
        foodNorms = new Food(pfcNormsCalculation.getPfc());
        pfcNormsToController = pfcNormsCalculation.getNorms();
        //Из-за того, что норма для кислот рассчитывается в разделе БЖУ
        //А сами кислоты в кислотах)
        acidNorms.setOmega3(pfcNormsCalculation.getOmega3());
        acidNorms.setOmega6(pfcNormsCalculation.getOmega6());
        acidNorms.setOmega9(pfcNormsCalculation.getOmega9());
    }

    public List<Ration> calculateRationForPerson(String gender, int workingGroup, float age, float weight, float height,
                                                 String dietType, int dietRestrictions, boolean pregnancy,
                                                 int days, int meals) {
        this.calculateNormsForPerson(gender, workingGroup, age, weight, height, dietType, dietRestrictions, pregnancy);
        //Считаем рецепты
        //Внтури рецептов считаются нормы
        List<Combination> combinations = this.actionsWithRecipes(gender, workingGroup, age, weight, height,
                dietType, dietRestrictions, pregnancy);
        //К рецептам докидываются продукты
        combinations = addProductsToCombinationsWithRecipes(dietRestrictions, combinations);
        //Из рецептов+продуктов составляются рационы
        List<Ration> results = new ArrayList<>();
        int j = 0; //Счётчик, с помощью которого мы достаём комбинации из списка
        for (int i = 0; i < days; i++) {
            if (j > combinations.size() - 1) {
                j = 0;
            }
            Combination combWhichWillBePartitionedIntoMeals = new Combination(combinations.get(j));

            Ration newRation = new Ration(i);
            newRation.combinationPartitioning(combWhichWillBePartitionedIntoMeals, meals);
            results.add(newRation);
            j++;
        }
        return results;
    }

    public List<Combination> actionsWithRecipes(String gender, int workingGroup, float age, float weight, float height,
                                                String dietType, int dietRestrictions, boolean pregnancy) {
        this.calculateNormsForPerson(gender, workingGroup, age, weight, height, dietType, dietRestrictions, pregnancy);

        int first = 1;
        int second = 2;
        int lunch = 3;
        int salad = 4;
        List<Ingredient> sortedFirsts = productOverallEfficiency(this.foodService.getRecipesOfCertainType(first),
                this.foodNorms, this.vitaminNorms, this.mineralNorms, this.acidNorms, true).stream()
                .filter(x -> x.calculateOverallIngredientEfficiency() < 0.20f).collect(Collectors.toList());
        List<Ingredient> sortedSeconds = productOverallEfficiency(this.foodService.getRecipesOfCertainType(second),
                this.foodNorms, this.vitaminNorms, this.mineralNorms, this.acidNorms, true).stream()
                .filter(x -> x.calculateOverallIngredientEfficiency() < 0.20f).collect(Collectors.toList());
        List<Ingredient> sortedLunches = productOverallEfficiency(this.foodService.getRecipesOfCertainType(lunch),
                this.foodNorms, this.vitaminNorms, this.mineralNorms, this.acidNorms, true).stream()
                .filter(x -> x.calculateOverallIngredientEfficiency() < 0.20f).collect(Collectors.toList());
        List<Ingredient> sortedSalads = productOverallEfficiency(this.foodService.getRecipesOfCertainType(salad),
                this.foodNorms, this.vitaminNorms, this.mineralNorms, this.acidNorms, true).stream()
                .filter(x -> x.calculateOverallIngredientEfficiency() < 0.20f).collect(Collectors.toList());

        List<Long> recipesIds = new ArrayList<>();
        recipesIds.addAll(sortedFirsts.stream().map(Ingredient::getId).collect(Collectors.toList()));
        recipesIds.addAll(sortedSeconds.stream().map(Ingredient::getId).collect(Collectors.toList()));
        recipesIds.addAll(sortedLunches.stream().map(Ingredient::getId).collect(Collectors.toList()));
        recipesIds.addAll(sortedSalads.stream().map(Ingredient::getId).collect(Collectors.toList()));
        Map<Long, Recipes> recipesList = this.foodService.getRecipeObjects(recipesIds);

        List<List<Ingredient>> formedRecipes = new ArrayList<>();
        //Формирууем по 4 "рациона" за цикл
        for (int i = 0; i < 4; i++) {
            formedRecipes.addAll(getNearOptimalCombinationsOfRecipes(sortedFirsts, sortedSeconds, sortedLunches, sortedSalads));
        }

        //Проходим по всем комбинациям из рецептов
        //Превращаем их в реальные комбинации из рецептов
        List<Combination> combinationList = new ArrayList<>();
        for (int i = 0; i < formedRecipes.size(); i++) {
            Combination combination = new Combination();
            for (int j = 0; j < formedRecipes.get(i).size(); j++) {
                Recipe recipe = new Recipe(formedRecipes.get(i).get(j));
                recipe.setRecipeInfo(recipesList.get(formedRecipes.get(i).get(j).getId()));
                combination.addRecipe(recipe);
            }
            combinationList.add(combination);
        }
        return combinationList;
    }

    public List<List<Ingredient>> getNearOptimalCombinationsOfRecipes(List<Ingredient> sortedFirsts, List<Ingredient> sortedSeconds,
                                                                      List<Ingredient> sortedLunches, List<Ingredient> sortedSalads) {
        Ingredient mostEfficientFirst, mostEfficientSecond, mostEfficientLunch, mostEfficientSalad;
        List<List<Ingredient>> rations = new ArrayList<>();

        mostEfficientFirst = sortedFirsts.get(0);
        sortedFirsts.remove(0);
        List<Ingredient> mostEfficientWithFirst = getEfficientCombinationOfRecipes(Stream.of(
                sortedSeconds, sortedLunches, sortedSalads
        ).collect(Collectors.toList()), mostEfficientFirst);
        //Не забыть удалить использованные объекты из sorted массивов

        mostEfficientSecond = sortedSeconds.get(0);
        sortedSeconds.remove(0);
        List<Ingredient> mostEfficientWithSecond = getEfficientCombinationOfRecipes(Stream.of(
                sortedFirsts, sortedLunches, sortedSalads
        ).collect(Collectors.toList()), mostEfficientSecond);

        mostEfficientLunch = sortedLunches.get(0);
        List<Ingredient> mostEfficientWithLunch = getEfficientCombinationOfRecipes(Stream.of(
                sortedFirsts, sortedSeconds, sortedSalads
        ).collect(Collectors.toList()), mostEfficientLunch);

        mostEfficientSalad = sortedSalads.get(0);
        List<Ingredient> mostEfficientWithSalad = getEfficientCombinationOfRecipes(Stream.of(
                sortedFirsts, sortedSeconds, sortedLunches
        ).collect(Collectors.toList()), mostEfficientSalad);

        rations.add(mostEfficientWithFirst);
        rations.add(mostEfficientWithSecond);
        rations.add(mostEfficientWithLunch);
        rations.add(mostEfficientWithSalad);

        return rations;
    }

    private List<Ingredient> getEfficientCombinationOfRecipes(List<List<Ingredient>> sorted, Ingredient mostEfficient) {
        List<Ingredient> mostEfficientList = new ArrayList<>();

        Ingredient sum = new Ingredient();
        mostEfficientList.add(mostEfficient);
        sum.sum(mostEfficient);

        List<Ingredient> list;
        for (List<Ingredient> sortedOfType : sorted) {
            list = sortedOfType;
            mostEfficientList.add(list.get(0));
            sum.sum(list.get(0));
            sortedOfType.remove(0);
        }
        return mostEfficientList;
    }

    private List<Combination> addProductsToCombinationsWithRecipes(int dietRestrictions, List<Combination> combsWithRecipes) {
        List<Ingredient> ingredients = foodService.getListOfIngredients(foodService.getFoodWOProhibitedCategories(dietRestrictions));
        List<Ingredient> foodWithEfficiency = productOverallEfficiency(ingredients, foodNorms, vitaminNorms,
                mineralNorms, acidNorms, false);
        foodWithEfficiency = foodWithEfficiency.stream()
                .filter(x->x.calculateOverallIngredientEfficiency()<0.15)
                .collect(Collectors.toList());
        //Получаем список категорий, превращаем в словарь, где значение - допустимое количество оставшихся использований
        //Делаем 2 списка: один локальный, другой глобальный для выполнения требований к максимальному количеству продуктов из одной группы внутри комбинации
        //и во всех комбинациях
        FoodAndCategoriesLimitationTable limitationTable = foodService.getLimitations();

        //Непосредственный расчёт: передаём список допустимой еды, нормы БЖУ, нормы нутриентов
        combsWithRecipes = calculateEfficientCombinations(limitationTable, foodWithEfficiency, combsWithRecipes).getCombinationList();

        /*combsWithRecipes = combsWithRecipes.stream()
                .filter(x -> x.getProducts().size() == 0)
                .collect(Collectors.toList()
                );*/

        Combinations cs = new Combinations();
        cs.setCombinationList(combsWithRecipes);
        for (int i = 0; i < 10; i++) {
            cs = optimizeCombinations(ingredients, cs);
            cs = addFoodToOptimizedCombination(cs, ingredients);
        }
        combsWithRecipes = cs.getCombinationList();

        combsWithRecipes = combsWithRecipes.stream()
                        .sorted((x, y) -> Float.compare(y.getCombinationEfficiency(), x.getCombinationEfficiency()))
                        .collect(Collectors.toList()
        );
        return combsWithRecipes;
    }
}
