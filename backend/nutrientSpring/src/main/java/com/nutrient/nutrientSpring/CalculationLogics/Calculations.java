package com.nutrient.nutrientSpring.CalculationLogics;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Utils.Combination;
import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNormsCalculation;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import com.nutrient.nutrientSpring.JsonObjects.NutrientREST.Combinations;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutrientHasGender;
import com.nutrient.nutrientSpring.Services.FoodService;
import com.nutrient.nutrientSpring.Services.NutrientService;
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
        HashMap<Long, Long> categoryCounter = foodService.getCategoriesCounter();

        foodWithEfficiency = foodWithEfficiency
                .stream()
                .sorted((x, y) -> Float.compare(y.calculateOverallIngredientEfficiency(), x.calculateOverallIngredientEfficiency()))
                .collect(Collectors.toList());

        //Непосредственный расчёт: передаём список допустимой еды, нормы БЖУ, нормы нутриентов
        combinations = calculateEfficientCombinations(categoryCounter, foodWithEfficiency);


        for (int i = 0; i < 100; i++) {
            combinations = optimizeCombinations(foodWithNutrientsList, combinations);
            combinations = addFoodToOptimizedCombination(combinations, foodWithNutrientsList);
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

       /*for(Map.Entry<Long, HashMap<String, Object>> entry : productsNutrients.entrySet()){
           HashMap<String, Object> food = entry.getValue();

           HashMap<String, Float> pfcEfficiency = new HashMap<>();
           HashMap<String, Float> mineralEfficiency = new HashMap<>();
           HashMap<String, Float> vitaminEfficiency = new HashMap<>();
           HashMap<String, Float> acidEfficiency = new HashMap<>();

           Float overallEfficiency;
           Float avgMineralEfficiency = 0f;
           Float avgVitaminEfficiency = 0f;
           Float avgAcidEfficiency = 0f;
           Float avgPcfEfficiency = 0f;

           for(Object foodNutrient : food.values()){
               if(foodNutrient instanceof Food){
                   Food tmpFood = (Food)foodNutrient;
                   pfcEfficiency.put("calorieEfficiency", tmpFood.getEnergy()/ pfcNorms.get(0));
                   pfcEfficiency.put("fatEfficiency", tmpFood.getFat()/ pfcNorms.get(2));
                   pfcEfficiency.put("proteinEfficiency", tmpFood.getProtein()/ pfcNorms.get(1));
                   pfcEfficiency.put("carbohydrateEfficiency", tmpFood.getCarbohydrate()/ pfcNorms.get(3));
                   pfcEfficiency.put("waterEfficiency", tmpFood.getWater()/ pfcNorms.get(4));
                   pfcEfficiency.put("ashEfficiency", 0f);
                   pfcEfficiency.put("sugarEfficiency", tmpFood.getSugares()/ pfcNorms.get(6));
                   pfcEfficiency.put("starchEfficiency", tmpFood.getStarch()/ pfcNorms.get(7));
                   pfcEfficiency.put("cholesterolEfficiency", tmpFood.getFat_trans()/ pfcNorms.get(9));
                   pfcEfficiency.put("fatransEfficiency", tmpFood.getFat_trans()/ pfcNorms.get(8));
               }
               else if(foodNutrient instanceof Mineral){
                   Mineral tmpMineral = (Mineral)foodNutrient;
                   List<Float> mineralValues = tmpMineral.getValues();
                   for(int i = 0; i<mineralValues.size(); i++){
                       mineralEfficiency.put(foodService.getMineralsNames().get(i), mineralValues.get(i)/mineralNorms.get(i).getValue());
                   }

                   for(Float mineralVal : mineralEfficiency.values()){
                       avgMineralEfficiency+=mineralVal;
                   }
                   avgMineralEfficiency=avgMineralEfficiency/mineralEfficiency.size();

                   Float ash = ((tmpMineral.getCalcium() + tmpMineral.getIron() + tmpMineral.getMagnesium() +
                           tmpMineral.getPhosphorus() + tmpMineral.getPotassium() + tmpMineral.getSodium() +
                           tmpMineral.getZinc() + tmpMineral.getCopper() + tmpMineral.getManganese() +
                           tmpMineral.getSelenium() + tmpMineral.getFluoride())/11)/pfcNorms.get(5);
                   pfcEfficiency.put("ashEfficiency", ash);
               }
               else if(foodNutrient instanceof Vitamin){
                   Vitamin tmpVitamin = (Vitamin)foodNutrient;
                   List<Float> vitaminValues = tmpVitamin.getValues();
                   for(int i = 0; i<vitaminValues.size(); i++){
                       vitaminEfficiency.put(foodService.getVitaminNames().get(i), vitaminValues.get(i)/vitaminNorms.get(i).getValue());
                   }

                   for(Float vitaminVal : vitaminEfficiency.values()){
                       avgVitaminEfficiency+=vitaminVal;
                   }
                   avgVitaminEfficiency=avgVitaminEfficiency/vitaminEfficiency.size();

               } else if(foodNutrient instanceof Acid){
                   Acid tmpAcid = (Acid)foodNutrient;
                   List<Float> acidValues = tmpAcid.getValues();
                   for(int i = 0; i<acidValues.size(); i++){
                       acidEfficiency.put(foodService.getAcidNames().get(i), acidValues.get(i)/acidNorms.get(i).getValue());
                   }

                   for(Float acidVal : acidEfficiency.values()){
                       avgAcidEfficiency+=acidVal;
                   }
                   avgAcidEfficiency=avgAcidEfficiency/acidEfficiency.size();
               }
           }

           avgPcfEfficiency = 0f;
           for(Float pfcVal : pfcEfficiency.values()){
               avgPcfEfficiency+=pfcVal;
           }
           avgPcfEfficiency=avgPcfEfficiency/pfcEfficiency.size();

           pfcEfficiency.put("overallPfcEfficiency", avgPcfEfficiency);
           mineralEfficiency.put("overallMineralEfficiency", avgMineralEfficiency);
           vitaminEfficiency.put("overallVitaminEfficiency", avgVitaminEfficiency);
           acidEfficiency.put("overallAcidEfficiency", avgAcidEfficiency);
           overallEfficiency = (avgMineralEfficiency+avgPcfEfficiency+avgVitaminEfficiency+avgAcidEfficiency)/4;

           food.put("pfcEfficiency", pfcEfficiency);
           food.put("mineralEfficiency", mineralEfficiency);
           food.put("vitaminEfficiency", vitaminEfficiency);
           food.put("acidEfficiency", acidEfficiency);
           food.put("overallEfficiency", overallEfficiency);

           entry.setValue(food);
       }*/
        return ingredients;
    }

    //Рассчитываем эфективные комбинации
    private Combinations calculateEfficientCombinations(
            HashMap<Long, Long> categoryCounter, List<Ingredient> sortedFood) {
        Combinations finalCombinations = new Combinations();

        HashMap<Long, Long> overallCounter = new HashMap<>();
        //Делаем общий счётчик категорий
        for (Map.Entry<Long, Long> oc : categoryCounter.entrySet()) {
            overallCounter.put(oc.getKey(), 3L);
        }
        //Чтобы не было повторений
        //Т.е. в течение скольких циклов составления комбинаций данный ингридиент будет игнорироваться
        HashMap<Long, Long> usedIds2 = new HashMap<>();

        for (int i = 0; i < 12; i++) {
            Combination combinationToAdd = new Combination();

            //Локальный счётчик категорий
            HashMap<Long, Long> localCounter = new HashMap<>();
            for (Map.Entry<Long, Long> old : categoryCounter.entrySet()) {
                localCounter.put(old.getKey(), old.getValue());
            }

            for (Ingredient ingredient : sortedFood) {
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

                Long categoryId = ingredient.getFood().getCategory().getId();
                if (localCounter.containsKey(categoryId) && overallCounter.containsKey(categoryId)) {
                    if (localCounter.get(categoryId) > 0 && overallCounter.get(categoryId) > 0) {
                        if (!usedIds2.containsKey(ingredient.getId())) {
                            if (combinationToAdd.addIngredientToCombination(ingredient, localCounter)) {
                                if (Math.random() > 0.5) {
                                    usedIds2.put(ingredient.getId(), (long) (1 + Math.random() * 3));
                                }
                                localCounter.put(categoryId, (Long) localCounter.get(categoryId) - 1);
                                overallCounter.put(categoryId, (Long) overallCounter.get(categoryId) - 1);
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

/*        for(
    int i = 0;
    i< 12;i++)

    {
        Combination combinationToAdd = new Combination();

        //Должно копировать
        HashMap<Long, Long> localCounter = new HashMap<>();
        for (Map.Entry<Long, Long> old : categoryCounter.entrySet()) {
            localCounter.put(old.getKey(), old.getValue());
        }
        //Составляем комбинацию из продуктов, смотря на категории
        for (Map.Entry<Long, HashMap<String, Object>> foodList : foodWithNutrientsList.entrySet()) {
            Food tmpFood = ((Food) foodList.getValue().get("food"));

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

            Long categoryId = tmpFood.getCategory().getId();
            if (newCounter.containsKey(categoryId) && overallCounter.containsKey(categoryId)) {
                if (newCounter.get(categoryId) > 0 && overallCounter.get(categoryId) > 0) {
                    if (!usedIds2.containsKey(tmpFood.getId())) {
                        //внезависимости от добавления/недобавления будет обновлён счётчик
                        if (!combinationToAdd.addFoodToCombination(foodList.getValue(), newCounter)) {
                            break;
                        } else {
                            if (Math.random() > 0.5) {
                                usedIds2.put(tmpFood.getId(), (long) (1 + Math.random() * 3));
                            }
                            newCounter.put(categoryId, (Long) newCounter.get(categoryId) - 1);
                            overallCounter.put(categoryId, (Long) overallCounter.get(categoryId) - 1);
                        }
                    }
                }
            }
        }

        finalCombinations.addCombination(combinationToAdd);
    }
        finalCombinations.setOverallCategoryCounter(overallCounter);
        return finalCombinations;
}*/

    private Combinations addFoodToOptimizedCombination(
            Combinations combs, HashMap<Long, HashMap<String, Object>> foodWithNutrientsList) {
        HashMap<Long, Long> overallCounter = combs.getOverallCategoryCounter();
        //в течение скольких циклов составления комбинаций данный ингридиент будет игнорироваться
        HashMap<Long, Long> usedIds2 = new HashMap<>();
        for (Combination comb : combs.getCombinationList()) {
            HashMap<Long, Long> localCounter = comb.getCategoryCounter();
            for (Map.Entry<Long, HashMap<String, Object>> foodList : foodWithNutrientsList.entrySet()) {
                Food tmpFood = ((Food) foodList.getValue().get("food"));

                if (comb.isInCombination(tmpFood)) {
                    continue;
                }

                //Если данный ингридиент долго не использовался в комбинациях
                //(т.е. счётчик игнорирования == 0)
                //то мы возвращаем ингридиент в пулл
                /*if(usedIds2.containsValue(0)){
                    for(Map.Entry <Long, Long> keyVal: usedIds2.entrySet()){
                        if(keyVal.getValue() == 0){
                            usedIds2.remove(keyVal.getKey());
                        }
                    }
                }*/

                Long categoryId = tmpFood.getCategory().getId();
                if (localCounter.containsKey(categoryId) && overallCounter.containsKey(categoryId)) {
                    if (localCounter.get(categoryId) > 0 && overallCounter.get(categoryId) > 0) {
                        //if(!usedIds2.containsKey(tmpFood.getId())) {
                        if (!comb.addFoodToCombination(foodList.getValue(), localCounter)) {
                            break;
                        }
                            /*else {
                                if (Math.random() > 0.5) {
                                    usedIds2.put(tmpFood.getId(), (long)(1+Math.random()*3));
                                }
                                localCounter.put(categoryId, (Long) localCounter.get(categoryId) - 1);
                                overallCounter.put(categoryId, (Long) overallCounter.get(categoryId) - 1);
                            }*/
                        //}
                    }
                }

            }
        }
        return combs;
    }

    public PfcNorms getPfcNorms() {
        return pfcNormsToController;
    }

    public Combinations optimizeCombinations(HashMap<Long, HashMap<String, Object>> foodWithNutrientsList, Combinations unOptimizedCombinations) {
        for (Combination comb : unOptimizedCombinations.getCombinationList()) {
            int counter = 5;
            List<List<Integer>> listOfOverflowingNutrients = comb.doesCombinationHasOverflowingNutrients();
            List<Long> foodIds = comb.getFoods().stream()
                    .map(Food::getId)
                    .collect(Collectors.toList());

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

                HashMap<Long, HashMap<Integer, Float>> mostOverFlowingNutrient = new HashMap<>();
                Float valueOfMostOverflowingNutrientInComb = 1f;

                if (pfcOverflow.size() > 0) {
                    mostOverFlowingNutrient = getMostOverflowingNutrient(foodWithNutrientsList, foodIds, pfcOverflow, "pfcEfficiency");
                    valueOfMostOverflowingNutrientInComb = new ArrayList<Float>(comb.getPfcEfficiency().values()).get(pfcOverflow.get(0));
                } else if (vitaminOverflow.size() > 0) {
                    mostOverFlowingNutrient = getMostOverflowingNutrient(foodWithNutrientsList, foodIds, vitaminOverflow, "vitaminEfficiency");
                    valueOfMostOverflowingNutrientInComb = new ArrayList<Float>(comb.getVitaminEfficiency().values()).get(vitaminOverflow.get(0));
                } else if (mineralOverflow.size() > 0) {
                    mostOverFlowingNutrient = getMostOverflowingNutrient(foodWithNutrientsList, foodIds, mineralOverflow, "mineralEfficiency");
                    valueOfMostOverflowingNutrientInComb = new ArrayList<Float>(comb.getMineralEfficiency().values()).get(mineralOverflow.get(0));
                } else if (acidOverflow.size() > 0) {
                    mostOverFlowingNutrient = getMostOverflowingNutrient(foodWithNutrientsList, foodIds, acidOverflow, "acidEfficiency");
                    valueOfMostOverflowingNutrientInComb = new ArrayList<Float>(comb.getAcidEfficiency().values()).get(acidOverflow.get(0));
                }

                Long idOfFoodToBeModified = mostOverFlowingNutrient.entrySet().stream().findFirst().get().getKey();
                comb.deleteFoodFromCombination(idOfFoodToBeModified, foodWithNutrientsList.get(idOfFoodToBeModified));
                Float gramFixCoef = getNutrientFixCoefficient(mostOverFlowingNutrient, valueOfMostOverflowingNutrientInComb);
                comb.addFoodToCustomCombination(modifyFoodGrams(foodWithNutrientsList.get(idOfFoodToBeModified), gramFixCoef));

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
        HashMap<Long, HashMap<String, Object>> foodWithNutrientsUnsortedList = foodService.getFoodNutrientsForCustomCombination(ids);
        //Получаем список объектов значений нутриентов для конкретного пола
        nutrientService.getNutrientsValueForGender(gender);

        acidNorms = new Acid(nutrientService.getAcidNorms().stream()
                .map(NutrientHasGender::getValue)
                .collect(Collectors.toList()));
        vitaminNorms = new Vitamin(nutrientService.getVitaminNorms().stream()
                .map(NutrientHasGender::getValue)
                .collect(Collectors.toList()));
        mineralNorms = new Mineral(nutrientService.getMineralNorms().stream()
                .map(NutrientHasGender::getValue)
                .collect(Collectors.toList()));
        //Рассчитываем Нрмы БЖУ, исходя из роста, веса, пола и т.д.)
        pfcNormsCalculation = new PfcNormsCalculation(gender, age, weight, height, dietType, workingGroup);
        //Рассчитываем норму золы
        List<Long> mineralIds = mapper.getMineralsId();
        pfcNormsCalculation.setAsh(nutrientService.getMineralsSum(gender, mineralIds));
        //Получаем список норм БЖУ
        List<Float> pfcNorms = pfcNormsCalculation.getPfc();
        pfcNormsToController = pfcNormsCalculation.getNorms();
        //Рассчитываем эффективность каждого из продуктов
        productOverallEfficiency(foodWithNutrientsUnsortedList, pfcNorms,
                nutrientService.getVitaminNorms(), nutrientService.getMineralNorms(), nutrientService.getAcidNorms());

        Combination result = new Combination();
        for (Map.Entry<Long, HashMap<String, Object>> food : foodWithNutrientsUnsortedList.entrySet()) {
            Long id = ((Food) food.getValue().get("food")).getId();
            int numberOfGrams = actualIdsGrams.get(id);
            food.setValue(modifyFoodGrams(food.getValue(), (float) numberOfGrams / 100));
            result.addFoodToCustomCombination(food.getValue());
        }

        return result;
    }

    private HashMap<Long, HashMap<Integer, Float>> getMostOverflowingNutrient(HashMap<Long, HashMap<String, Object>> foodWithNutrientsList, List<Long> foodIds,
                                                                              List<Integer> overflowingIndexes, String nutrientGroup) {
        HashMap<Long, HashMap<Integer, Float>> efficiencyOnSingleNutrient = new HashMap<>();
        Integer index = overflowingIndexes.get(0);

        for (Long id : foodIds) {
            HashMap<Integer, Float> nutrientEffectPair = new HashMap<>();
            nutrientEffectPair.put(index,
                    new ArrayList<>(((HashMap<String, Float>) foodWithNutrientsList.get(id).get(nutrientGroup)).values())
                            .get(index));

            efficiencyOnSingleNutrient.put(id, nutrientEffectPair);
        }

        Long idOfMaxOverflow = efficiencyOnSingleNutrient.entrySet().stream()
                .max((f1, f2) -> Float.compare(f1.getValue().get(index), f2.getValue().get(index))).get().getKey();
        HashMap<Integer, Float> efficiencyOfMaxOverflow = efficiencyOnSingleNutrient.entrySet().stream()
                .max((f1, f2) -> Float.compare(f1.getValue().get(index), f2.getValue().get(index))).get().getValue();

        HashMap<Long, HashMap<Integer, Float>> result = new HashMap<>();
        result.put(idOfMaxOverflow, efficiencyOfMaxOverflow);
        return result;
    }

    public Float getNutrientFixCoefficient(HashMap<Long, HashMap<Integer, Float>> mostOverFlowingNutrient,
                                           Float valueOfOverflowingNutrientInComb) {
        if (valueOfOverflowingNutrientInComb == 0f) return 0f;

        Float gramFixCoefficient;
        Float nutrientPercentOfOverflow = mostOverFlowingNutrient.entrySet().stream()
                .findFirst().get().getValue().entrySet().stream()
                .findFirst().get().getValue();

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

    public HashMap<String, Object> modifyFoodGrams(HashMap<String, Object> foodNutrients, Float gramFixCoef) {

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
        /*for(Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>)foodNutrients.get("overallEfficiency")).entrySet()){
            foodEfficiency.setValue(foodEfficiency.getValue()*gramFixCoef);
        }*/
        foodNutrients.put("overallEfficiency", (Float) foodNutrients.get("overallEfficiency") * gramFixCoef);

        return foodNutrients;
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
}
