package com.nutrient.nutrientSpring.CalculationLogics;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Utils.Combination;
import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNormsCalculation;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.Combinations;
import com.nutrient.nutrientSpring.Services.FoodService;
import com.nutrient.nutrientSpring.Services.NutrientService;
import com.nutrient.nutrientSpring.Utils.FoodAndCategoriesLimitationTable;
import com.nutrient.nutrientSpring.Utils.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
        nutrientService.getNutrientsValueForGender(gender);

        //Рассчитываем Нрмы БЖУ, исходя из роста, веса, пола и т.д.)
        pfcNormsCalculation = new PfcNormsCalculation(gender, age, weight, height, dietType, workingGroup);
        //Рассчитываем норму золы
        List<Long> mineralIds = mapper.getMineralsId();
        pfcNormsCalculation.setAsh(nutrientService.getMineralsSum(gender, mineralIds));
        //Получаем список норм БЖУ
        Food pfcNorms = new Food(pfcNormsCalculation.getPfc());
        pfcNormsToController = pfcNormsCalculation.getNorms();

        //Рассчитываем эффективность каждого из продуктов (пока просто по максимуму - дальше - можно поиграться с коэффициентами и
        //записать всё в бд отдельным скриптом
        List<Ingredient> foodWithEfficiency = productOverallEfficiency(ingredients, pfcNorms, nutrientService.getVitaminNorms(), nutrientService.getMineralNorms(), nutrientService.getAcidNorms());
        //Получаем список категорий, превращаем в словарь, где значение - допустимое количество оставшихся использований
        //Делаем 2 списка: один локальный, другой глобальный для выполнения требований к максимальному количеству продуктов из одной группы внутри комбинации
        //и во всех комбинациях
        FoodAndCategoriesLimitationTable limitationTable = foodService.getLimitations();

        foodWithEfficiency = foodWithEfficiency
                .stream()
                .sorted((x, y) -> Float.compare(y.calculateOverallIngredientEfficiency(), x.calculateOverallIngredientEfficiency()))
                .collect(Collectors.toList());

        //Непосредственный расчёт: передаём список допустимой еды, нормы БЖУ, нормы нутриентов
        combinations = calculateEfficientCombinations(limitationTable, foodWithEfficiency);


        for (int i = 0; i < 100; i++) {
            combinations = optimizeCombinations(ingredients, combinations);
            combinations = addFoodToOptimizedCombination(combinations, ingredients);
        }

        combinations.setCombinationList(
                combinations.getCombinationList().stream()
                        .sorted((x, y) -> Float.compare((float) y.getCombinationEfficiency(), (float) x.getCombinationEfficiency()))
                        .collect(Collectors.toList())
        );
        return combinations;
    }

    //Добавляем к оригинальной мапе проценты эффективности по бжу и прочему говну
    private List<Ingredient> productOverallEfficiency(List<Ingredient> ingredients, Food pfcNorms,
                                                      Vitamin vitaminNorms, Mineral mineralNorms, Acid acidNorms) {
        //Еда:объект еды, Минералы: объект минералов, Витамины:объект витаминов, Кислоты: объект кислот
        /*Здесь добавляем к этому следующую конструкцию
        {
            Эффективность БЖУ:{эффективность по калориям, белкам, жирам, углеводам,воде,золе, холестеролу,трансжирам и сахару, Общая эффективность}
            Эффективность Минералов:{}
            Эффективность Витаминов:{}
            Общая эффективность 100гр. продукта : Значение
        }
        */

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
        }

        return ingredients;
    }

    //Рассчитываем эфективные комбинации
    private Combinations calculateEfficientCombinations(
            FoodAndCategoriesLimitationTable limits, List<Ingredient> sortedFood) {
        Combinations finalCombinations = new Combinations();

        HashMap<Long, Long> overallCounter = new HashMap<>();
        //Чтобы не было повторений
        //Т.е. в течение скольких циклов составления комбинаций данный ингридиент будет игнорироваться
        HashMap<Long, Long> usedIds2 = new HashMap<>();

        for (int i = 0; i < 12; i++) {
            Combination combinationToAdd = new Combination();

            for (Ingredient ingredient : sortedFood) {
                int productId = ingredient.getId().intValue();
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

                int categoryId = ingredient.getFood().getCategory().getId().intValue();

                if (limits.getCategoryLimitInAllCombs(categoryId)>0 && limits.getCategoryLimitInComb(categoryId)>0) {
                    if (!usedIds2.containsKey(ingredient.getId())) {
                        if (limits.getFoodLimit(productId)>0) {

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
            finalCombinations.addCombination(combinationToAdd);
        }
        finalCombinations.setOverallCategoryCounter(overallCounter);
        return finalCombinations;
    }

    private Combinations addFoodToOptimizedCombination(
            Combinations combs, List<Ingredient> products) {

        HashMap<Long, Long> usedIds2 = new HashMap<>();
        for (Combination comb : combs.getCombinationList()) {
            FoodAndCategoriesLimitationTable limits = comb.getLimitationTable();
            for (Ingredient ingredient: products) {
                Food tmpFood = ingredient.getFood();

                if (comb.isInCombination(tmpFood)) {
                    continue;
                }

                int categoryId = ingredient.getFood().getCategory().getId().intValue();
                if (limits.getCategoryLimitInAllCombs(categoryId)>0 && limits.getCategoryLimitInComb(categoryId)>0) {
                    if (!usedIds2.containsKey(ingredient.getId())) {
                        if (limits.getFoodLimit(ingredient.getId().intValue())>0) {
                            if (comb.addProductToCombination(ingredient, limits)) {
                                if (Math.random() > 0.5) {
                                    usedIds2.put(ingredient.getId(), (long) (1 + Math.random() * 3));
                                }
                                limits.updateCategoryLimit(categoryId, -1);
                                limits.updateFoodLimit(ingredient.getId().intValue(), -1);
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
            List<List<Integer>> listOfOverflowingNutrients = comb.doesCombinationHasOverflowingNutrients();
            List<Long> foodIds = comb.getProducts().stream()
                    .map((Ingredient::getFood))
                    .map(Food::getId)
                    .collect(Collectors.toList())
                    ;

            //Получаем айди тех нутриентов, которые в избытке
            List<Integer> pfcOverflow, vitaminOverflow, mineralOverflow, acidOverflow;
            pfcOverflow = listOfOverflowingNutrients.get(0);
            vitaminOverflow = listOfOverflowingNutrients.get(1);
            mineralOverflow = listOfOverflowingNutrients.get(2);
            acidOverflow = listOfOverflowingNutrients.get(3);

            //Пока вообще не будет категорий с избыточными нутриентами
            while ((pfcOverflow.size() != 0) || (vitaminOverflow.size() != 0) ||
                    (mineralOverflow.size() != 0) || (acidOverflow.size() != 0)) {
                List<List<Integer>> tmp = new ArrayList<>(listOfOverflowingNutrients);

                //какой нутриент в каком из продуктов больше всего
                HashMap<Long, HashMap<Integer, Float>> mostOverFlowingNutrient = new HashMap<>();
                //Значение эффективности(сколько процентов суточной нормы удоволетворяет ДАННЫЙ продукт)
                //по самому удоволетворяемому нутриенту
                Float percentOfMostOverflowingNutrientInComb = 1f;
                Ingredient nutrientsAndEfficiency = comb.getOverallNutrientsAndEfficiency();

                //Находим самый избыточный компонент и его значение
                if (pfcOverflow.size() > 0) {
                    mostOverFlowingNutrient = getMostOverflowingNutrient(products, foodIds,
                            pfcOverflow, "food");
                    percentOfMostOverflowingNutrientInComb = nutrientsAndEfficiency.getFoodEfficiency()
                            .getValues().get(pfcOverflow.get(0));
                } else if (vitaminOverflow.size() > 0) {
                    mostOverFlowingNutrient = getMostOverflowingNutrient(products, foodIds,
                            vitaminOverflow, "vitamin");
                    percentOfMostOverflowingNutrientInComb = nutrientsAndEfficiency.getVitaminEfficiency()
                            .getValues().get(pfcOverflow.get(0));
                } else if (mineralOverflow.size() > 0) {
                    mostOverFlowingNutrient = getMostOverflowingNutrient(products, foodIds,
                            mineralOverflow, "mineral");
                    percentOfMostOverflowingNutrientInComb = nutrientsAndEfficiency.getMineralEfficiency()
                            .getValues().get(pfcOverflow.get(0));
                } else if (acidOverflow.size() > 0) {
                    mostOverFlowingNutrient = getMostOverflowingNutrient(products, foodIds,
                            acidOverflow, "acid");
                    percentOfMostOverflowingNutrientInComb =  nutrientsAndEfficiency.getAcidEfficiency()
                            .getValues().get(pfcOverflow.get(0));
                }

                Long idOfFoodToBeModified = mostOverFlowingNutrient.entrySet()
                        .stream()
                        .findFirst()
                        .get()
                        .getKey()
                        ;

                Ingredient productTobeModified = products.stream()
                        .filter(ingredient -> ingredient.getId().equals(idOfFoodToBeModified))
                        .findFirst()
                        .get()
                        ;

                comb.deleteFoodFromCombination(productTobeModified);
                Float gramFixCoef = getNutrientFixCoefficient(mostOverFlowingNutrient, percentOfMostOverflowingNutrientInComb);
                productTobeModified.multiply(gramFixCoef);
                comb.addFoodToCustomCombination(productTobeModified);

                listOfOverflowingNutrients = comb.doesCombinationHasOverflowingNutrients();
                pfcOverflow = listOfOverflowingNutrients.get(0);
                vitaminOverflow = listOfOverflowingNutrients.get(1);
                mineralOverflow = listOfOverflowingNutrients.get(2);
                acidOverflow = listOfOverflowingNutrients.get(3);

                boolean isCycled = true;
                for (int i = 0; i < listOfOverflowingNutrients.size(); i++) {
                    if (!tmp.get(i).equals(listOfOverflowingNutrients.get(i))) {
                        isCycled = false;
                        break;
                    }
                }
                if (isCycled) counter--;
                if (counter == 0) break;
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
        nutrientService.getNutrientsValueForGender(gender);

        acidNorms = nutrientService.getAcidNorms();
        vitaminNorms = nutrientService.getVitaminNorms();
        mineralNorms = nutrientService.getMineralNorms();
        //Рассчитываем Нрмы БЖУ, исходя из роста, веса, пола и т.д.)
        pfcNormsCalculation = new PfcNormsCalculation(gender, age, weight, height, dietType, workingGroup);
        //Рассчитываем норму золы
        List<Long> mineralIds = mapper.getMineralsId();
        pfcNormsCalculation.setAsh(nutrientService.getMineralsSum(gender, mineralIds));
        //Получаем список норм БЖУ
        Food pfcNorms = new Food(pfcNormsCalculation.getPfc());
        pfcNormsToController = pfcNormsCalculation.getNorms();
        //Рассчитываем эффективность каждого из продуктов
        productOverallEfficiency(products, pfcNorms,
                nutrientService.getVitaminNorms(), nutrientService.getMineralNorms(), nutrientService.getAcidNorms());

        Combination result = new Combination();
        for (Ingredient product: products) {
            Long id = product.getId();
            int numberOfGrams = actualIdsGrams.get(id);
            product.multiply(numberOfGrams/100f);
            result.addFoodToCustomCombination(product);
        }

        return result;
    }

    private HashMap<Long, HashMap<Integer, Float>> getMostOverflowingNutrient(List<Ingredient> products, List<Long> foodIds,
                                                                              List<Integer> overflowingIndexes,
                                                                              String nutrient) {

        HashMap<Long, HashMap<Integer, Float>> efficiencyOnSingleNutrient = new HashMap<>();
        Integer index = overflowingIndexes.get(0);

        for (Ingredient ingredient: products) {
            HashMap<Integer, Float> nutrientEffectPair = new HashMap<>();
            Float effect = 0f;

            if(nutrient.equals("food")){
                effect = ingredient.getFoodEfficiency().getValues().get(index);
            } else if(nutrient.equals("mineral")){
                effect = ingredient.getMineralEfficiency().getValues().get(index);
            } else if(nutrient.equals("vitamin")){
                effect = ingredient.getVitaminEfficiency().getValues().get(index);
            } else {
                effect = ingredient.getAcidEfficiency().getValues().get(index);
            }
            nutrientEffectPair.put(index, effect);

            efficiencyOnSingleNutrient.put(ingredient.getId(), nutrientEffectPair);
        }

        Long idOfMaxOverflow = efficiencyOnSingleNutrient.entrySet().stream()
                .max((f1, f2) -> Float.compare(f1.getValue().get(index), f2.getValue().get(index))).get().getKey();
        HashMap<Integer, Float> efficiencyOfMaxOverflow = efficiencyOnSingleNutrient.entrySet()
                .stream()
                .max((f1, f2) -> Float.compare(f1.getValue().get(index), f2.getValue().get(index)))
                .get()
                .getValue()
                ;

        HashMap<Long, HashMap<Integer, Float>> result = new HashMap<>();
        result.put(idOfMaxOverflow, efficiencyOfMaxOverflow);
        return result;
    }

    public Float getNutrientFixCoefficient(HashMap<Long, HashMap<Integer, Float>> mostOverFlowingNutrient,
                                           Float valueOfOverflowingNutrientInComb) {
        if (valueOfOverflowingNutrientInComb == 0f) return 0f;

        Float gramFixCoefficient;
        Float nutrientPercentOfOverflow = mostOverFlowingNutrient.entrySet().stream()
                .findFirst()
                .get()
                .getValue()
                .entrySet()
                .stream()
                .findFirst()
                .get()
                .getValue()
                ;

        Float tmp = nutrientPercentOfOverflow / valueOfOverflowingNutrientInComb;

        if (tmp >= 0.35 && tmp < 0.4) {
            gramFixCoefficient = 0.8f;
        } else if (tmp >= 0.4 && tmp < 0.6) {
            gramFixCoefficient = 0.65f;
        } else if (tmp >= 0.6 && tmp < 0.9) {
            gramFixCoefficient = 0.5f;
        } else if (tmp >= 0.9 && tmp < 1.15) {
            gramFixCoefficient = 0.25f;
        } else {
            gramFixCoefficient = 0.1f;
        }

        return gramFixCoefficient;
    }

  /*  public HashMap<String, Object> modifyFoodGrams(HashMap<String, Object> foodNutrients, Float gramFixCoef) {

        Food f = (Food) foodNutrients.get("food");
        Mineral m = (Mineral) foodNutrients.get("mineral");
        Acid a = (Acid) foodNutrients.get("acid");
        Vitamin v = (Vitamin) foodNutrients.get("vitamin");


        f.modify(gramFixCoef);
        m.modify(gramFixCoef);
        a.modify(gramFixCoef);
        v.modify(gramFixCoef);

        foodNutrients.put("food", f);
        foodNutrients.put("mineral", m);
        foodNutrients.put("acid", a);
        foodNutrients.put("vitamin", v);


        HashMap<String, Float> valueMap1 = new HashMap<>();
        for (Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>) foodNutrients.get("pfcEfficiency")).entrySet()) {
            valueMap1.put(foodEfficiency.getKey(), gramFixCoef * foodEfficiency.getValue());
        }
        foodNutrients.put("pfcEfficiency", valueMap1);

        HashMap<String, Float> valueMap2 = new HashMap<>();
        for (Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>) foodNutrients.get("mineralEfficiency")).entrySet()) {
            valueMap2.put(foodEfficiency.getKey(), gramFixCoef * foodEfficiency.getValue());
        }
        foodNutrients.put("mineralEfficiency", valueMap2);

        HashMap<String, Float> valueMap3 = new HashMap<>();
        for (Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>) foodNutrients.get("vitaminEfficiency")).entrySet()) {
            valueMap3.put(foodEfficiency.getKey(), gramFixCoef * foodEfficiency.getValue());
        }
        foodNutrients.put("vitaminEfficiency", valueMap3);

        HashMap<String, Float> valueMap4 = new HashMap<>();
        for (Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>) foodNutrients.get("acidEfficiency")).entrySet()) {
            valueMap4.put(foodEfficiency.getKey(), gramFixCoef * foodEfficiency.getValue());
        }
        foodNutrients.put("acidEfficiency", valueMap4);
        *//*for(Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>)foodNutrients.get("overallEfficiency")).entrySet()){
            foodEfficiency.setValue(foodEfficiency.getValue()*gramFixCoef);
        }*//*
        foodNutrients.put("overallEfficiency", (Float) foodNutrients.get("overallEfficiency") * gramFixCoef);

        return foodNutrients;
    }*/

    public Acid getAcidNorms() {
        return acidNorms;
    }

    public Vitamin getVitaminNorms() {
        return vitaminNorms;
    }

    public Mineral getMineralNorms() {
        return mineralNorms;
    }
}
