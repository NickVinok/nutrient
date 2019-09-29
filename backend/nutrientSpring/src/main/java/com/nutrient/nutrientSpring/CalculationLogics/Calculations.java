package com.nutrient.nutrientSpring.CalculationLogics;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.Combination;
import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNormsCalculation;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import com.nutrient.nutrientSpring.Model.JsonObjects.NutrientREST.Combinations;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutrientHasGender;
import com.nutrient.nutrientSpring.Services.FoodService;
import com.nutrient.nutrientSpring.Services.NutrientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BinaryOperator;
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

    public Combinations getEfficientCombinations(String gender, int workingGroup, float age, float weight, float height, String dietType, int dietRestrictions){
        Combinations combinations = new Combinations();
        //Получаем список словарей, где ключом выступает id еды, а значениями являются объекты еды, витаминов, минералов, кислот)
        HashMap<Long, HashMap<String, Object>> foodWithNutrientsUnsortedList = foodService.getListOfFoodsNutrients(
                foodService.getFoodWOProhibitedCategories(dietRestrictions));

        //Получаем список объектов значений нутриентов для конкретного пола
        nutrientService.getNutrientsValueForGender(gender);
        //Рассчитываем Нрмы БЖУ, исходя из роста, веса, пола и т.д.)
        pfcNormsCalculation = new PfcNormsCalculation(gender, age, weight, height, dietType, workingGroup);
        //Рассчитываем норму золы
        List<Long> mineralIds = mapper.getMineralsId();
        pfcNormsCalculation.setAsh(nutrientService.getMineralsSum(gender, mineralIds));
        //Получаем список норм БЖУ
        List<Float> pfcNorms = pfcNormsCalculation.getPfc();
        pfcNormsToController = pfcNormsCalculation.getNorms();
        //Рассчитываем эффективность каждого из продуктов (пока просто по максимуму - дальше - можно поиграться с коэффициентами и
        //записать всё в бд отдельным скриптом
        productOverallEfficiency(foodWithNutrientsUnsortedList, pfcNorms, nutrientService.getVitaminNorms(), nutrientService.getMineralNorms(), nutrientService.getAcidNorms());
        //Получаем список категорий, превращаем в словарь, где значение - допустимое количество оставшихся использований
        //Делаем 2 списка: один локальный, другой глобальный для выполнения требований к максимальному количеству продуктов из одной группы внутри комбинации
        //и во всех комбинациях
        HashMap<Long, Long> categoryCounter = foodService.getCategoriesCounter();
        //Сортируем еду по эффективности
        HashMap<Long, HashMap<String, Object>> foodWithNutrientsList = new HashMap<>();
        foodWithNutrientsUnsortedList.entrySet().stream()
                .sorted((x, y) -> Float.compare((float)y.getValue().get("overallEfficiency"), (float)x.getValue().get("overallEfficiency")))
                .forEach(x -> foodWithNutrientsList.put(x.getKey(), x.getValue()));
        //Непосредственный расчёт: передаём список допустимой еды, нормы БЖУ, нормы нутриентов
        //Возвращаем 12 комбинации
        combinations = calculateEfficientCombinations(categoryCounter, foodWithNutrientsList);
        Combinations newCombinations = optimizeCombinations(categoryCounter, foodWithNutrientsList, combinations);

        return newCombinations;
    }

    //Добавляем к оригинальной мапе проценты эффективности по бжу и прочему говну
    private void productOverallEfficiency(HashMap<Long, HashMap<String, Object>> productsNutrients, List<Float> pfcNorms,
                                          List<NutrientHasGender> vitaminNorms, List<NutrientHasGender> mineralNorms, List<NutrientHasGender> acidNorms) {
        //Еда:объект еды, Минералы: объект минералов, Витамины:объект витаминов, Кислоты: объект кислот
        /*Здесь добавляем к этому следующую конструкцию
        {
            Эффективность БЖУ:{эффективность по калориям, белкам, жирам, углеводам,воде,золе, холестеролу,трансжирам и сахару, Общая эффективность}
            Эффективность Минералов:{}
            Эффективность Витаминов:{}
            Общая эффективность 100гр. продукта : Значение
        }
        */
       for(Map.Entry<Long, HashMap<String, Object>> entry : productsNutrients.entrySet()){
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
       }
    }

    //Рассчитываем эфективные комбинации
    private Combinations calculateEfficientCombinations(
            HashMap<Long, Long> categoryCounter, HashMap<Long, HashMap<String, Object>> foodWithNutrientsList){
        Combinations finalCombinations = new Combinations();

        HashMap<Long, Long> overallCounter = new HashMap<>();
        //Делаем общий счётчик категорий
        for(Map.Entry<Long, Long> oc : categoryCounter.entrySet()){
            overallCounter.put(oc.getKey(), 3L);
        }
        //Чтобы не было повторений
        ArrayList<Long> usedIds = new ArrayList<>();
        HashMap<Long, Long> usedIds2 = new HashMap<>();

        for(int i = 0; i< 12; i++) {
            Combination combinationToAdd = new Combination();

            //Должно копировать
            HashMap<Long, Long> newCounter = new HashMap<>();
            for(Map.Entry<Long, Long> old : categoryCounter.entrySet()){
                newCounter.put(old.getKey(), old.getValue());
            }
            //Составляем комбинацию из продуктов, смотря на категории
            for (Map.Entry<Long, HashMap<String, Object>> foodList : foodWithNutrientsList.entrySet()) {
                Food tmpFood = ((Food)foodList.getValue().get("food"));

                if(usedIds2.containsValue(0)){
                    for(Map.Entry <Long, Long> keyVal: usedIds2.entrySet()){
                        if(keyVal.getValue() == 0){
                            usedIds2.remove(keyVal.getKey());
                        }
                    }
                }

                Long categoryId = tmpFood.getCategory().getId();
                if(newCounter.containsKey(categoryId) && overallCounter.containsKey(categoryId)) {
                    if (newCounter.get(categoryId) > 0 && overallCounter.get(categoryId) > 0) {
                        if(!usedIds2.containsKey(tmpFood.getId())) {
                            if (!combinationToAdd.addFoodToCombination(foodList.getValue())) {
                                break;
                            } else {
                                if (Math.random() > 0.5) {
                                    usedIds2.put(tmpFood.getId(), (long)(1+Math.random()*3));
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
        finalCombinations.setCombinationList(
                finalCombinations.getCombinationList().stream()
                .sorted((x,y) -> Float.compare((float)y.getCombinationEfficiency(), (float)x.getCombinationEfficiency()))
                .collect(Collectors.toList())
        );
        return finalCombinations;
    }

    public PfcNorms getPfcNorms() {
        return pfcNormsToController;
    }

    public Combinations optimizeCombinations(
            HashMap<Long, Long> categoryCounter, HashMap<Long, HashMap<String, Object>> foodWithNutrientsList, Combinations unOptimizedCombinations){
        int i = 0;
        for(Combination comb: unOptimizedCombinations.getCombinationList()){
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
            while((pfcOverflow.size() != 0) || (vitaminOverflow.size() != 0) ||
                    (mineralOverflow.size() != 0) || (acidOverflow.size() != 0)){

                HashMap<Long, HashMap<Integer, Float>> mostOverFlowingNutrient =  new HashMap<>();
                Float valueOfMostOverflowingNutrientInComb = 1f;
                if(pfcOverflow.size() > 0){
                    mostOverFlowingNutrient = getMostOverflowingNutrient(foodWithNutrientsList, foodIds, pfcOverflow, "pfcEfficiency");
                    valueOfMostOverflowingNutrientInComb = new ArrayList<Float>(comb.getPfcEfficiency().values()).get(pfcOverflow.get(0));
                }
                else if(vitaminOverflow.size() > 0){
                    mostOverFlowingNutrient = getMostOverflowingNutrient(foodWithNutrientsList, foodIds, vitaminOverflow, "vitaminEfficiency");
                    valueOfMostOverflowingNutrientInComb = new ArrayList<Float>(comb.getVitaminEfficiency().values()).get(vitaminOverflow.get(0));
                }
                else if(mineralOverflow.size() > 0){
                    mostOverFlowingNutrient = getMostOverflowingNutrient(foodWithNutrientsList, foodIds, mineralOverflow, "mineralEfficiency");
                    valueOfMostOverflowingNutrientInComb = new ArrayList<Float>(comb.getMineralEfficiency().values()).get(mineralOverflow.get(0));
                }
                else if(acidOverflow.size() > 0){
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

            }
            i++;
        }
        return unOptimizedCombinations;
    }

    public Combination calculateCustomCombination(String gender, int workingGroup, float age, float weight, float height, String dietType,
                                                  int dietRestrictions, List<HashMap<String, Long>> idsWithGrams){
        List<Long> ids = new ArrayList<>();
        HashMap<Long, Integer> actualIdsGrams = new HashMap<>();
        for(HashMap<String, Long> hs: idsWithGrams){
            ids.add(hs.get("id"));
            actualIdsGrams.put(hs.get("id"), hs.get("gram").intValue());
        }
        HashMap<Long, HashMap<String, Object>> foodWithNutrientsUnsortedList = foodService.getFoodNutrientsForCustomCombination(ids);
        //Получаем список объектов значений нутриентов для конкретного пола
        nutrientService.getNutrientsValueForGender(gender);
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
        for(Map.Entry<Long, HashMap<String, Object>> food : foodWithNutrientsUnsortedList.entrySet()){
            Long id = ((Food)food.getValue().get("food")).getId();
            int numberOfGrams = actualIdsGrams.get(id);
            food.setValue(modifyFoodGrams(food.getValue(), (float)numberOfGrams/100));
            result.addFoodToCustomCombination(food.getValue());
        }

        return result;
    }

    private HashMap<Long, HashMap<Integer, Float>> getMostOverflowingNutrient(HashMap<Long, HashMap<String, Object>> foodWithNutrientsList,List<Long> foodIds,
                                                            List<Integer> overflowingIndexes,String nutrientGroup)
    {
        HashMap<Long, HashMap<Integer, Float>> efficiencyOnSingleNutrient = new HashMap<>();
        Integer index = overflowingIndexes.get(0);

        for(Long id: foodIds){
            HashMap<Integer, Float> nutrientEffectPair= new HashMap<>();
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
                                                       Float valueOfOverflowingNutrientInComb){
        if(valueOfOverflowingNutrientInComb == 0f) return 0f;

        Float gramFixCoefficient;
        Float nutrientPercentOfOverflow = mostOverFlowingNutrient.entrySet().stream()
                .findFirst().get().getValue().entrySet().stream()
                .findFirst().get().getValue();

        Float tmp = nutrientPercentOfOverflow/valueOfOverflowingNutrientInComb;

        if(tmp>=0.35 && tmp < 0.4){
            gramFixCoefficient = 0.8f;
        } else if(tmp>=0.4 && tmp<0.6){
            gramFixCoefficient = 0.65f;
        } else if(tmp>=0.6 && tmp<0.9){
            gramFixCoefficient = 0.5f;
        } else if(tmp>=0.9 && tmp<1.15){
            gramFixCoefficient = 0.25f;
        } else{
            gramFixCoefficient = 0.1f;
        }

        return gramFixCoefficient;
    }

    public HashMap<String, Object> modifyFoodGrams(HashMap<String, Object> foodNutrients, Float gramFixCoef){

        Food f = (Food)foodNutrients.get("food");
        Mineral m = (Mineral)foodNutrients.get("mineral");
        Acid a = (Acid)foodNutrients.get("acid");
        Vitamin v = (Vitamin)foodNutrients.get("vitamin");


        f.modify(gramFixCoef);
        m.modify(gramFixCoef);
        a.modify(gramFixCoef);
        v.modify(gramFixCoef);

        foodNutrients.put("food", f);
        foodNutrients.put("mineral", m);
        foodNutrients.put("acid", a);
        foodNutrients.put("vitamin", v);


        HashMap<String, Float> valueMap1 = new HashMap<>();
        for(Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>)foodNutrients.get("pfcEfficiency")).entrySet()){
            valueMap1.put(foodEfficiency.getKey(), gramFixCoef*foodEfficiency.getValue());
        }
        foodNutrients.put("pfcEfficiency", valueMap1);

        HashMap<String, Float> valueMap2 = new HashMap<>();
        for(Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>)foodNutrients.get("mineralEfficiency")).entrySet()){
            valueMap2.put(foodEfficiency.getKey(), gramFixCoef*foodEfficiency.getValue());
        }
        foodNutrients.put("mineralEfficiency", valueMap2);

        HashMap<String, Float> valueMap3 = new HashMap<>();
        for(Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>)foodNutrients.get("vitaminEfficiency")).entrySet()){
            valueMap3.put(foodEfficiency.getKey(), gramFixCoef*foodEfficiency.getValue());
        }
        foodNutrients.put("vitaminEfficiency", valueMap3);

        HashMap<String, Float> valueMap4 = new HashMap<>();
        for(Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>)foodNutrients.get("acidEfficiency")).entrySet()){
            valueMap4.put(foodEfficiency.getKey(), gramFixCoef*foodEfficiency.getValue());
        }
        foodNutrients.put("acidEfficiency", valueMap4);
        /*for(Map.Entry<String, Float> foodEfficiency : ((HashMap<String, Float>)foodNutrients.get("overallEfficiency")).entrySet()){
            foodEfficiency.setValue(foodEfficiency.getValue()*gramFixCoef);
        }*/
        foodNutrients.put("overallEfficiency", (Float)foodNutrients.get("overallEfficiency")*gramFixCoef);

        return foodNutrients;
    }
}
