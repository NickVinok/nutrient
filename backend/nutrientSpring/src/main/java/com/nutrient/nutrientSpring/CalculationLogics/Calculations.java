package com.nutrient.nutrientSpring.CalculationLogics;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.Model.JsonObjects.Combination;
import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNormsCalculation;
import com.nutrient.nutrientSpring.Model.FoodModel.Food;
import com.nutrient.nutrientSpring.Model.FoodModel.Mineral;
import com.nutrient.nutrientSpring.Model.FoodModel.Vitamin;
import com.nutrient.nutrientSpring.Model.JsonObjects.Combinations;
import com.nutrient.nutrientSpring.Model.NutrientModel.NutrientHasGender;
import com.nutrient.nutrientSpring.Services.FoodService;
import com.nutrient.nutrientSpring.Services.NutrientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Combinations getEfficientCombinations(String gender, String dietLimitations, float age, float weight, float height, String dietType){
        Combinations combinations = new Combinations();

        //Получаем список словарей, где ключом выступает id еды, а значениями являются объекты еды, витаминов, минералов, кислот)
        HashMap<Long, HashMap<String, Object>> foodWithNutrientsList = foodService.getListOfFoodsNutrients(foodService.getFoodWOProhibitedCategories());

        //Получаем список объектов значений нутриентов для конкретного пола
        List<NutrientHasGender> nutrientLimitationsList = nutrientService.getNutrientsValueForGender(gender);

        //Рассчитываем Нрмы БЖУ, исходя из роста, веса, пола и т.д.)
        pfcNormsCalculation = new PfcNormsCalculation(gender, age, weight, height, dietType);
        //Рассчитываем норму золы
        List<Long> mineralIds = mapper.getMineralsId();
        pfcNormsCalculation.setAsh(nutrientService.getMineralsSum(gender, mineralIds));
        //Получаем список норм БЖУ
        List<Float> pfcNorms = pfcNormsCalculation.getPfc();
        pfcNormsToController = pfcNormsCalculation.getNorms();

        //Рассчитываем эффективность каждого из продуктов (пока просто по максимуму - дальше - можно поиграться с коэффициентами и
        //записать всё в бд отдельным скриптом
        try {
            productOverallEfficiency(foodWithNutrientsList, pfcNorms, nutrientLimitationsList, gender);
        } catch (NoSuchFieldException e){

        }

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
    private void productOverallEfficiency(HashMap<Long, HashMap<String, Object>> productsNutrients, List<Float> pfcNorms, List<NutrientHasGender> nutrientNorms, String gender)
    throws NoSuchFieldException{
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
           Float overallEfficiency;

           for(Object foodNutrient : food.values()){
               if(foodNutrient instanceof Food){
                   pfcEfficiency.put("calorieEfficiency", ((Food)foodNutrient).getEnergy()/ pfcNorms.get(0));
                   pfcEfficiency.put("proteinEfficiency", ((Food)foodNutrient).getProtein()/ pfcNorms.get(1));
                   pfcEfficiency.put("fatEfficiency", ((Food)foodNutrient).getFat()/ pfcNorms.get(2));
                   pfcEfficiency.put("carbohydrateEfficiency", ((Food)foodNutrient).getCarbohydrate()/ pfcNorms.get(3));
                   pfcEfficiency.put("waterEfficiency", ((Food)foodNutrient).getWater()/ pfcNorms.get(4));
                   pfcEfficiency.put("sugarEfficiency", 1-((Food)foodNutrient).getSugares()/ pfcNorms.get(6));
                   pfcEfficiency.put("starchEfficiency", ((Food)foodNutrient).getStarch()/ pfcNorms.get(7));
                   pfcEfficiency.put("fatransEfficiency", 1-((Food)foodNutrient).getFat_trans()/ pfcNorms.get(8));
                   pfcEfficiency.put("cholesterolEfficiency", 1-((Food)foodNutrient).getFat_trans()/ pfcNorms.get(9));

               }
               else if(foodNutrient instanceof Mineral){
                   mineralEfficiency.put("calcium", ((Mineral)foodNutrient).getCalcium()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("calcium")),gender));
                   mineralEfficiency.put("iron",((Mineral)foodNutrient).getIron()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("iron")),gender));
                   mineralEfficiency.put("magnesium", ((Mineral)foodNutrient).getMagnesium()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("magnesium")),gender));
                   mineralEfficiency.put("phosphorus",((Mineral)foodNutrient).getPhosphorus()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("phosphorus")),gender));
                   mineralEfficiency.put("potassium",((Mineral)foodNutrient).getPotassium()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("potassium")),gender));
                   mineralEfficiency.put("sodium",((Mineral)foodNutrient).getSodium()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("sodium")),gender));
                   mineralEfficiency.put("zinc",((Mineral)foodNutrient).getZinc()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("zinc")),gender));
                   mineralEfficiency.put("copper",((Mineral)foodNutrient).getCopper()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("copper")),gender));
                   mineralEfficiency.put("manganese",((Mineral)foodNutrient).getManganese()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("manganese")),gender));
                   mineralEfficiency.put("selenium",((Mineral)foodNutrient).getSelenium()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("selenium")),gender));
                   mineralEfficiency.put("fluoride",((Mineral)foodNutrient).getFluoride()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("fluoride")),gender));
                   Float ash = ((((Mineral)foodNutrient).getCalcium() + ((Mineral)foodNutrient).getIron() + ((Mineral)foodNutrient).getMagnesium() +
                           ((Mineral)foodNutrient).getPhosphorus() + ((Mineral)foodNutrient).getPotassium() + ((Mineral)foodNutrient).getSodium() +
                           ((Mineral)foodNutrient).getZinc() + ((Mineral)foodNutrient).getCopper() + ((Mineral)foodNutrient).getManganese() +
                           ((Mineral)foodNutrient).getSelenium() + ((Mineral)foodNutrient).getFluoride())/11)/pfcNorms.get(5);
                   pfcEfficiency.put("ashEfficiency", ash);
               }
               else if(foodNutrient instanceof Vitamin){
                   vitaminEfficiency.put("vitamin_c", ((Vitamin)foodNutrient).getVitamin_c()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_c")),gender));
                   vitaminEfficiency.put("vitamin_b1",((Vitamin)foodNutrient).getVitamin_b1()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b1")),gender));
                   vitaminEfficiency.put("vitamin_b2", ((Vitamin)foodNutrient).getVitamin_b2()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b2")),gender));
                   vitaminEfficiency.put("vitamin_b6",((Vitamin)foodNutrient).getVitamin_b6()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b6")),gender));
                   vitaminEfficiency.put("vitamin_b3",((Vitamin)foodNutrient).getVitamin_b3()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b3")),gender));
                   vitaminEfficiency.put("vitamin_b12",((Vitamin)foodNutrient).getVitamin_b12()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b12")),gender));
                   vitaminEfficiency.put("vitamin_b9",((Vitamin)foodNutrient).getVitamin_b9()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b9")),gender));
                   vitaminEfficiency.put("vitamin_b5",((Vitamin)foodNutrient).getVitamin_b5()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b5")),gender));
                   //mineralEfficiency.put("vitamin_b7",((Vitamin)foodNutrient).getVitamin_b7()/
                          // nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b7")),gender));
                   vitaminEfficiency.put("vitamin_a",((Vitamin)foodNutrient).getVitamin_a()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_a")),gender));
                   vitaminEfficiency.put("beta_carotene",((Vitamin)foodNutrient).getBeta_carotene()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("beta_carotene")),gender));
                   vitaminEfficiency.put("vitamin_e",((Vitamin)foodNutrient).getVitamin_e()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_e")),gender));
                   vitaminEfficiency.put("vitamin_d",((Vitamin)foodNutrient).getVitamin_d()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_d")),gender));
                   vitaminEfficiency.put("vitamin_k",((Vitamin)foodNutrient).getVitamin_k()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_k")),gender));
                   vitaminEfficiency.put("vitamin_b4",((Vitamin)foodNutrient).getVitamin_b4()/
                           nutrientService.getValueOfCertainMineral(mapper.getNutrientsId(Mineral.class.getField("vitamin_b4")),gender));
               }
           }

           Float avgMineralEfficiency = 0f;
           for(Float mineralVal : mineralEfficiency.values()){
               avgMineralEfficiency+=mineralVal;
           }
           avgMineralEfficiency=avgMineralEfficiency/mineralEfficiency.size();

           Float avgVitaminEfficiency = 0f;
           for(Float vitaminVal : vitaminEfficiency.values()){
               avgVitaminEfficiency+=vitaminVal;
           }
           avgVitaminEfficiency=avgVitaminEfficiency/vitaminEfficiency.size();

           Float avgPcfEfficiency = 0f;
           for(Float pfcVal : pfcEfficiency.values()){
               avgPcfEfficiency+=pfcVal;
           }
           avgPcfEfficiency=avgPcfEfficiency/pfcEfficiency.size();

           pfcEfficiency.put("overallPfcEfficiency", avgPcfEfficiency);
           mineralEfficiency.put("overallMineralEfficiency", avgMineralEfficiency);
           vitaminEfficiency.put("overallVitaminEfficiency", avgVitaminEfficiency);
           overallEfficiency = (avgMineralEfficiency+avgPcfEfficiency+avgVitaminEfficiency)/3;

           food.put("pfcEfficiency", pfcEfficiency);
           food.put("mineralEfficiency", mineralEfficiency);
           food.put("vitaminEfficiency", vitaminEfficiency);
           food.put("overallEfficiency", overallEfficiency);

           entry.setValue(food);
       }
    }

    //Рассчитываем эфективные комбинации
    private Combinations calculateEfficientCombinations(
            HashMap<Long, Long> categoryCounter, HashMap<Long, HashMap<String, Object>> foodWithNutrientsList){
        Combinations finalCombinations = new Combinations();

        //сортируем мапу по общей эффективности продукта
        HashMap<Long, HashMap<String, Object>> sortedFoodWithNutrientsList = new HashMap<>();
        foodWithNutrientsList.entrySet().stream()
                .sorted((x, y) -> Float.compare((float)x.getValue().get("overallEfficiency"), (float)y.getValue().get("overallEfficiency")))
                .forEach(x -> sortedFoodWithNutrientsList.put(x.getKey(), x.getValue()));

        for(int i = 0; i< 12; i++) {
            Combination combinationToAdd = new Combination();

            //Должно копировать
            HashMap<Long, Long> newCounter = new HashMap<>();
            for(Map.Entry<Long, Long> old : categoryCounter.entrySet()){
                newCounter.put(old.getKey(), old.getValue());
            }

            //Составляем комбинацию из продуктов, смотря на категории
            for (Map.Entry<Long, HashMap<String, Object>> foodList : sortedFoodWithNutrientsList.entrySet()) {
                Long categoryId = ((Food)foodList.getValue().get("food")).getCategory().getId();
                if(newCounter.get(categoryId) > 0){
                    if(!combinationToAdd.addFoodToCombination(foodList.getValue())){
                        break;
                    }
                    else{
                        newCounter.put(categoryId, (Long)newCounter.get(categoryId) - 1);
                    }
                }else{
                    continue;
                }
            }
            finalCombinations.addCombination(combinationToAdd);
        }
        return finalCombinations;
    }

    public PfcNorms getPfcNorms() {
        return pfcNormsToController;
    }
}
