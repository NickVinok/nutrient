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

    private float CaloriesCoefficient = 1.4f;

    public Combinations getEfficientCombinations(String gender, int workingGroup, float age, float weight, float height, String dietType, int dietRestrictions){
        Combinations combinations = new Combinations();
        //Получаем список словарей, где ключом выступает id еды, а значениями являются объекты еды, витаминов, минералов, кислот)
        HashMap<Long, HashMap<String, Object>> foodWithNutrientsUnsortedList = foodService.getListOfFoodsNutrients(
                foodService.getFoodWOProhibitedCategories(dietRestrictions));
        HashMap<Long, HashMap<String, Object>> foodWithNutrientsList = new HashMap<>();
        
        //Сортируем еду по эффективности
        foodWithNutrientsUnsortedList.entrySet().stream()
                .sorted((x, y) -> Float.compare((float)y.getValue().get("overallEfficiency"), (float)x.getValue().get("overallEfficiency")))
                .forEach(x -> foodWithNutrientsList.put(x.getKey(), x.getValue()));

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
        productOverallEfficiency(foodWithNutrientsList, pfcNorms, nutrientService.getVitaminNorms(), nutrientService.getMineralNorms(), nutrientService.getAcidNorms());
        //Получаем список категорий, превращаем в словарь, где значение - допустимое количество оставшихся использований
        //Делаем 2 списка: один локальный, другой глобальный для выполнения требований к максимальному количеству продуктов из одной группы внутри комбинации
        //и во всех комбинациях
        HashMap<Long, Long> categoryCounter = foodService.getCategoriesCounter();
        //Непосредственный расчёт: передаём список допустимой еды, нормы БЖУ, нормы нутриентов
        //Возвращаем 3 комбинации
        combinations = calculateEfficientCombinations(categoryCounter, foodWithNutrientsList);

        return combinations;
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
                   pfcEfficiency.put("proteinEfficiency", tmpFood.getProtein()/ pfcNorms.get(1));
                   pfcEfficiency.put("fatEfficiency", tmpFood.getFat()/ pfcNorms.get(2));
                   pfcEfficiency.put("carbohydrateEfficiency", tmpFood.getCarbohydrate()/ pfcNorms.get(3));
                   pfcEfficiency.put("waterEfficiency", tmpFood.getWater()/ pfcNorms.get(4));
                   pfcEfficiency.put("sugarEfficiency", 1-tmpFood.getSugares()/ pfcNorms.get(6));
                   pfcEfficiency.put("starchEfficiency", tmpFood.getStarch()/ pfcNorms.get(7));
                   pfcEfficiency.put("fatransEfficiency", tmpFood.getFat_trans()/ pfcNorms.get(8));
                   pfcEfficiency.put("cholesterolEfficiency", tmpFood.getFat_trans()/ pfcNorms.get(9));

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
        for(Combination comb: unOptimizedCombinations.getCombinationList()){

        }
        return null;
    }
}
