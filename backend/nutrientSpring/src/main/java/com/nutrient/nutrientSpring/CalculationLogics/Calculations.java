package com.nutrient.nutrientSpring.CalculationLogics;

import com.nutrient.nutrientSpring.CalculationLogics.Pfc.PfcNorms;
import com.nutrient.nutrientSpring.Model.FoodModel.Acid;
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
        HashMap<Long, HashMap<String, Object>> foodWithNutrientsList = foodService.getListOfFoodsNutrients(foodService.getFoodWOProhibitedCategories(dietRestrictions));
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
        List<String> tmpMineralsNames = Stream.of("calcium ", "phosphorus", "magnesium ", "potassium",
                "sodium", "iron", "zinc ", "copper", "manganese", "selenium", "fluoride")
                .collect(Collectors.toList());
        List<String> tmpVitaminNames = Stream.of("vitamin_c", "vitamin_b1", "vitamin_b2", "vitamin_b6",
                "vitamin_b3","vitamin_b12", "vitamin_b9", "vitamin_b5", "alpha-carotin",
                "vitamin_a", "beta-carotin", "vitamin_e", "vitamin_d", "vitamin_k", "vitamin_b4")
                .collect(Collectors.toList());
        List<String> tmpAcidNames = Stream.of("tryptophan","threonine","isoleucine","leucine","lysine",
                "methionine", "cystine", "phenylalanine","tyrosine","valine","arginine","histidine",
                "alanine","aspartic_acid","glutamic_acid","glycine","proline","serine")
                .collect(Collectors.toList());
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
                       mineralEfficiency.put(tmpMineralsNames.get(i), mineralValues.get(i)/mineralNorms.get(i).getValue());
                   }

                   for(Float mineralVal : mineralEfficiency.values()){
                       avgMineralEfficiency+=mineralVal;
                   }
                   avgMineralEfficiency=avgMineralEfficiency/mineralEfficiency.size();

                   /*mineralEfficiency.put("calcium", tmpMineral.getCalcium()/
                           nutrientService.getValueOfCertainNutrient("calcium", gender));
                   mineralEfficiency.put("phosphorus",tmpMineral.getPhosphorus()/
                           nutrientService.getValueOfCertainNutrient("phosphorus",gender));
                   mineralEfficiency.put("magnesium", tmpMineral.getMagnesium()/
                           nutrientService.getValueOfCertainNutrient("magnesum",gender));
                   mineralEfficiency.put("potassium",tmpMineral.getPotassium()/
                           nutrientService.getValueOfCertainNutrient("kalium",gender));
                   mineralEfficiency.put("sodium",tmpMineral.getSodium()/
                           nutrientService.getValueOfCertainNutrient("natrium",gender));
                   mineralEfficiency.put("iron",tmpMineral.getIron()/
                           nutrientService.getValueOfCertainNutrient("ferrum" ,gender));
                   mineralEfficiency.put("zinc",tmpMineral.getZinc()/
                           nutrientService.getValueOfCertainNutrient("zincum",gender));
                   mineralEfficiency.put("copper",tmpMineral.getCopper()/
                           nutrientService.getValueOfCertainNutrient("cuprum",gender));
                   mineralEfficiency.put("manganese",tmpMineral.getManganese()/
                           nutrientService.getValueOfCertainNutrient("manganum",gender));
                   mineralEfficiency.put("selenium",tmpMineral.getSelenium()/
                           nutrientService.getValueOfCertainNutrient("selenum",gender));
                   mineralEfficiency.put("fluoride",tmpMineral.getFluoride()/
                           nutrientService.getValueOfCertainNutrient("fluorum",gender));*/

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
                       vitaminEfficiency.put(tmpVitaminNames.get(i), vitaminValues.get(i)/vitaminNorms.get(i).getValue());
                   }

                   for(Float vitaminVal : vitaminEfficiency.values()){
                       avgVitaminEfficiency+=vitaminVal;
                   }
                   avgVitaminEfficiency=avgVitaminEfficiency/vitaminEfficiency.size();

                   /*vitaminEfficiency.put("vitamin_c", tmpVitamin.getVitamin_c()/
                           nutrientService.getValueOfCertainNutrient("c",gender));
                   vitaminEfficiency.put("vitamin_b1",tmpVitamin.getVitamin_b1()/
                           nutrientService.getValueOfCertainNutrient("b1",gender));
                   vitaminEfficiency.put("vitamin_b2", tmpVitamin.getVitamin_b2()/
                           nutrientService.getValueOfCertainNutrient("b2",gender));
                   vitaminEfficiency.put("vitamin_b6",tmpVitamin.getVitamin_b6()/
                           nutrientService.getValueOfCertainNutrient("b6",gender));
                   vitaminEfficiency.put("vitamin_b3",tmpVitamin.getVitamin_b3()/
                           nutrientService.getValueOfCertainNutrient("b3",gender));
                   vitaminEfficiency.put("vitamin_b12",tmpVitamin.getVitamin_b12()/
                           nutrientService.getValueOfCertainNutrient("b12",gender));
                   vitaminEfficiency.put("vitamin_b9",tmpVitamin.getVitamin_b9()/
                           nutrientService.getValueOfCertainNutrient("b9",gender));
                   vitaminEfficiency.put("vitamin_b5",tmpVitamin.getVitamin_b5()/
                           nutrientService.getValueOfCertainNutrient("b5",gender));
                   vitaminEfficiency.put("alpha-carotin",tmpVitamin.getAlpha_carotene()/
                          nutrientService.getValueOfCertainNutrient("alpha-carotin",gender));
                   vitaminEfficiency.put("vitamin_a",tmpVitamin.getVitamin_a()/
                           nutrientService.getValueOfCertainNutrient("a",gender));
                   vitaminEfficiency.put("beta_carotene",tmpVitamin.getBeta_carotene()/
                           nutrientService.getValueOfCertainNutrient("beta-carotin",gender));
                   vitaminEfficiency.put("vitamin_e",tmpVitamin.getVitamin_e()/
                           nutrientService.getValueOfCertainNutrient("e",gender));
                   vitaminEfficiency.put("vitamin_d",tmpVitamin.getVitamin_d()/
                           nutrientService.getValueOfCertainNutrient("d",gender));
                   vitaminEfficiency.put("vitamin_k",tmpVitamin.getVitamin_k()/
                           nutrientService.getValueOfCertainNutrient("k",gender));
                   vitaminEfficiency.put("vitamin_b4",tmpVitamin.getVitamin_b4()/
                           nutrientService.getValueOfCertainNutrient("b4",gender));*/
               } else if(foodNutrient instanceof Acid){
                   Acid tmpAcid = (Acid)foodNutrient;
                   List<Float> acidValues = tmpAcid.getValues();
                   for(int i = 0; i<acidValues.size(); i++){
                       acidEfficiency.put(tmpAcidNames.get(i), acidValues.get(i)/acidNorms.get(i).getValue());
                   }

                   for(Float acidVal : acidEfficiency.values()){
                       avgAcidEfficiency+=acidVal;
                   }
                   avgAcidEfficiency=avgAcidEfficiency/acidEfficiency.size();

                   /*acidEfficiency.put("tryptophan", tmpAcid.getTryptophan()/
                           nutrientService.getValueOfCertainNutrient("tryptophan", gender));
                   acidEfficiency.put("threonine", tmpAcid.getThreonine()/
                           nutrientService.getValueOfCertainNutrient("threonine", gender));
                   acidEfficiency.put("isoleucine", tmpAcid.getIsoleucine()/
                           nutrientService.getValueOfCertainNutrient("isoleucine", gender));
                   acidEfficiency.put("leucine", tmpAcid.getLeucine()/
                           nutrientService.getValueOfCertainNutrient("leucine", gender));
                   acidEfficiency.put("lysine", tmpAcid.getLysine()/
                           nutrientService.getValueOfCertainNutrient("lysine", gender));
                   acidEfficiency.put("methionine", tmpAcid.getMethionine()/
                           nutrientService.getValueOfCertainNutrient("methionine", gender));
                   acidEfficiency.put("cystine", tmpAcid.getCystine()/
                           nutrientService.getValueOfCertainNutrient("cystine", gender));
                   acidEfficiency.put("phenylalanine", tmpAcid.getPhenylalanine()/
                           nutrientService.getValueOfCertainNutrient("phenylalanine", gender));
                   acidEfficiency.put("tyrosine", tmpAcid.getTyrosine()/
                           nutrientService.getValueOfCertainNutrient("tyrosine", gender));
                   acidEfficiency.put("valine", tmpAcid.getValine()/
                           nutrientService.getValueOfCertainNutrient("valine", gender));
                   acidEfficiency.put("arginine", tmpAcid.getArginine()/
                           nutrientService.getValueOfCertainNutrient("arginine", gender));
                   acidEfficiency.put("histidine", tmpAcid.getHistidine()/
                           nutrientService.getValueOfCertainNutrient("histidine", gender));
                   acidEfficiency.put("alanine", tmpAcid.getAlanine()/
                           nutrientService.getValueOfCertainNutrient("alanine", gender));
                   acidEfficiency.put("aspartic_acid", tmpAcid.getAspartic_acid()/
                           nutrientService.getValueOfCertainNutrient("aspartic_acid", gender));
                   acidEfficiency.put("glutamic_acid", tmpAcid.getGlutamic_acid()/
                           nutrientService.getValueOfCertainNutrient("glutamic_acid", gender));
                   acidEfficiency.put("glycine", tmpAcid.getGlycine()/
                           nutrientService.getValueOfCertainNutrient("glycine", gender));
                   acidEfficiency.put("proline", tmpAcid.getProline()/
                           nutrientService.getValueOfCertainNutrient("proline", gender));
                   acidEfficiency.put("serine", tmpAcid.getSerine()/
                           nutrientService.getValueOfCertainNutrient("serine", gender));*/
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

        //сортируем мапу по общей эффективности продукта
        HashMap<Long, HashMap<String, Object>> sortedFoodWithNutrientsList = new HashMap<>();
        foodWithNutrientsList.entrySet().stream()
                .sorted((x, y) -> Float.compare((float)y.getValue().get("overallEfficiency"), (float)x.getValue().get("overallEfficiency")))
                .forEach(x -> sortedFoodWithNutrientsList.put(x.getKey(), x.getValue()));

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
            for (Map.Entry<Long, HashMap<String, Object>> foodList : sortedFoodWithNutrientsList.entrySet()) {
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
}
