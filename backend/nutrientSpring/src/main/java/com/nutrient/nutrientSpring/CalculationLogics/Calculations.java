package com.nutrient.nutrientSpring.CalculationLogics;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNormsCalculation;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.Combinations;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import com.nutrient.nutrientSpring.Services.FoodService;
import com.nutrient.nutrientSpring.Services.NutrientService;
import com.nutrient.nutrientSpring.Utils.Combination;
import com.nutrient.nutrientSpring.Utils.FoodAndCategoriesLimitationTable;
import com.nutrient.nutrientSpring.Utils.Ingredient;
import com.nutrient.nutrientSpring.Utils.Ration.Ration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Food pfcNorms = new Food(pfcNormsCalculation.getPfc());
        pfcNormsToController = pfcNormsCalculation.getNorms();
        //Из-за того, что норма для кислот рассчитывается в разделе БЖУ
        //А сами кислоты в кислотах)
        acidNorms.setOmega3(pfcNormsCalculation.getOmega3());
        acidNorms.setOmega6(pfcNormsCalculation.getOmega6());
        acidNorms.setOmega9(pfcNormsCalculation.getOmega9());

        //Рассчитываем эффективность каждого из продуктов
        List<Ingredient> foodWithEfficiency = productOverallEfficiency(ingredients, pfcNorms, vitaminNorms,
                mineralNorms, acidNorms);
        //Получаем список категорий, превращаем в словарь, где значение - допустимое количество оставшихся использований
        //Делаем 2 списка: один локальный, другой глобальный для выполнения требований к максимальному количеству продуктов из одной группы внутри комбинации
        //и во всех комбинациях
        FoodAndCategoriesLimitationTable limitationTable = foodService.getLimitations();

        //Непосредственный расчёт: передаём список допустимой еды, нормы БЖУ, нормы нутриентов
        combinations = calculateEfficientCombinations(limitationTable, foodWithEfficiency);

        combinations.setCombinationList(
                combinations.getCombinationList().stream()
                        .filter(x -> x.getProducts().size() != 0)
                        .collect(Collectors.toList())
        );

        for (int i = 0; i < 250; i++) {
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
                                                      Vitamin vitaminNorms, Mineral mineralNorms, Acid acidNorms) {
        List<Ingredient> tmp = new ArrayList<>();
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
                i.multiply(6f);
            } else if (overall > interval && overall < 2 * interval) {
                i.multiply(4f);
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
            FoodAndCategoriesLimitationTable limits, List<Ingredient> sortedFood) {


        Combinations finalCombinations = new Combinations();

        //Чтобы не было повторений
        //Т.е. в течение скольких циклов составления комбинаций данный ингридиент будет игнорироваться
        HashMap<Long, Long> usedIds2 = new HashMap<>();

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
            int leastAcid = combinationsOverall.getAcidEfficiency().getLeastOverflowingNutrient();
            int leastFood = combinationsOverall.getFoodEfficiency().getLeastOverflowingNutrient();
            int leastMineral = combinationsOverall.getMineralEfficiency().getLeastOverflowingNutrient();
            int leastVitamin = combinationsOverall.getVitaminEfficiency().getLeastOverflowingNutrient();

            double aValue = combinationsOverall.getAcidEfficiency().getValues().get(leastAcid);
            double fValue = combinationsOverall.getFoodEfficiency().getValues().get(leastFood);
            double mValue = combinationsOverall.getMineralEfficiency().getValues().get(leastMineral);
            double vValue = combinationsOverall.getVitaminEfficiency().getValues().get(leastVitamin);
            if (aValue > fValue && aValue > mValue && aValue > vValue) {
                localProducts = products.stream()
                        .sorted((x, y) -> Float.compare(
                                y.getAcidEfficiency().getValues().get(leastAcid),
                                x.getAcidEfficiency().getValues().get(leastAcid)
                        ))
                        .collect(Collectors.toList());
            } else if (fValue > aValue && fValue > mValue && fValue > vValue) {
                localProducts = products.stream()
                        .sorted((x, y) -> Float.compare(
                                y.getFoodEfficiency().getValues().get(leastFood),
                                x.getFoodEfficiency().getValues().get(leastFood)
                        ))
                        .collect(Collectors.toList());
            } else if (mValue > aValue && mValue > fValue && mValue > vValue) {
                localProducts = products.stream()
                        .sorted((x, y) -> Float.compare(
                                y.getMineralEfficiency().getValues().get(leastMineral),
                                x.getMineralEfficiency().getValues().get(leastMineral)
                        ))
                        .collect(Collectors.toList());
            } else if (vValue > aValue && vValue > fValue && vValue > mValue) {
                localProducts = products.stream()
                        .sorted((x, y) -> Float.compare(
                                y.getVitaminEfficiency().getValues().get(leastVitamin),
                                x.getVitaminEfficiency().getValues().get(leastVitamin)
                        ))
                        .collect(Collectors.toList());
            } else {
                break;
            }

            FoodAndCategoriesLimitationTable limits = c.getLimitationTable();

            for (Ingredient i : localProducts) {
                if (c.isInCombination(i)) {
                    continue;
                }

                Long productId = i.getId();
                Long categoryId = i.getFood().getCategory().getId();

                if (limits.isCategoryAllowed(categoryId)) {
                    if (limits.getCategoryLimitInAllCombs(categoryId) > 0 && limits.getCategoryLimitInComb(categoryId) > 0) {
                        if (!usedIds2.containsKey(productId) && limits.getFoodLimit(productId) > 0) {
                            if (c.addProductToCombination(i, limits)) {
                                if (Math.random() > 0.5) {
                                    usedIds2.put(i.getId(), (long) (1 + Math.random() * 3));
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
                if(productTobeModified==null){
                    break;
                }
                comb.deleteFoodFromCombination(productTobeModified);
                Float gramFixCoef = 0.5f;
                productTobeModified.multiply(gramFixCoef);
                //Если продукта, который мы уменьшали сатло слишком мало - не возвращаем его в комбинацию
                if (productTobeModified.getGram() <  10f) {
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
        nutrientService.getNutrientsValueForGender(weight, gender, age, pregnancy, false);

        acidNorms = nutrientService.getAcidNorms();
        vitaminNorms = nutrientService.getVitaminNorms();
        mineralNorms = nutrientService.getMineralNorms();
        //Рассчитываем Нрмы БЖУ, исходя из роста, веса, пола и т.д.)
        pfcNormsCalculation = new PfcNormsCalculation(gender, age, weight, height, dietType, workingGroup);
        //Рассчитываем норму золы
        List<Long> mineralIds = mapper.getMineralsId();
        pfcNormsCalculation.setAsh(nutrientService.getAshNorm());
        //Получаем список норм БЖУ
        Food pfcNorms = new Food(pfcNormsCalculation.getPfc());
        pfcNormsToController = pfcNormsCalculation.getNorms();
        acidNorms.setOmega3(pfcNormsCalculation.getOmega3());
        acidNorms.setOmega6(pfcNormsCalculation.getOmega6());
        acidNorms.setOmega9(pfcNormsCalculation.getOmega9());

        FoodAndCategoriesLimitationTable limits = foodService.getLimitations();

        //Рассчитываем эффективность каждого из продуктов
        productOverallEfficiency(products, pfcNorms,
                vitaminNorms, mineralNorms, acidNorms);

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
        Food pfcNorms = new Food(pfcNormsCalculation.getPfc());
        pfcNormsToController = pfcNormsCalculation.getNorms();
        //Из-за того, что норма для кислот рассчитывается в разделе БЖУ
        //А сами кислоты в кислотах)
        acidNorms.setOmega3(pfcNormsCalculation.getOmega3());
        acidNorms.setOmega6(pfcNormsCalculation.getOmega6());
        acidNorms.setOmega9(pfcNormsCalculation.getOmega9());
    }

    public List<Ration> calculateRationForPerson(String gender, int workingGroup, float age, float weight, float height,
                                                 String dietType, int dietRestrictions, boolean pregnancy,
                                                 int days, int meals){
        Combinations combinations = this.getEfficientCombinations(gender, workingGroup, age, weight, height,
                dietType, dietRestrictions, pregnancy);
        List<Ration> results = new ArrayList<>();
        int j=0; //Счётчик, с помощью которого мы достаём комбинации из списка
        for(int i = 0; i<days;i++){
            if(j>combinations.getCombinationList().size()-1){
                j=0;
            }
            Combination combWhichWillBePartitionedIntoMeals = combinations.getCombinationList().get(j);

            Ration newRation=new Ration(i);

            newRation.combinationPartitioning(combWhichWillBePartitionedIntoMeals, meals);
            results.add(newRation);
            j++;
        }
        return results;
    }
}
