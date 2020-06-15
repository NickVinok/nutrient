package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Utils.NutrientGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "minerals")
public class Mineral implements NutrientGroup {
    @Id
    private Long id;

    private float calcium;
    private float iron;
    private float magnesium;
    private float phosphorus;
    private float potassium;
    private float sodium;
    private float zinc;
    private float copper;
    private float manganese;
    private float selen;
    private float fluorine;
    //TODO найти нормы, затем добавить в расчёты
    private float silicon;
    private float sulfur;
    private float chlorine;
    private float aluminum;
    private float bor;
    private float vanadium;
    private float iodine;
    private float cobalt;
    private float molybdenum;
    private float nickel;
    private float strontium;
    private float titanium;
    private float chrome;
    private float tin;
    private float rubidium;
    private float lithium;
    private float zirconium;
    private float bromine;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Food food;

    public void sum(Mineral m1) {
        this.calcium += m1.getCalcium();
        this.iron += m1.getIron();
        this.magnesium += m1.getMagnesium();
        this.phosphorus += m1.getPhosphorus();
        this.potassium += m1.getPotassium();
        this.sodium += m1.getSodium();
        this.zinc += m1.getZinc();
        this.copper += m1.getCopper();
        this.manganese += m1.getManganese();
        this.selen += m1.getSelen();
        this.fluorine += m1.getFluorine();
        this.silicon += m1.getSilicon();
        this.sulfur += m1.getSulfur();
        this.chlorine += m1.getChlorine();
        this.aluminum += m1.getAluminum();
        this.bor += m1.getBor();
        this.vanadium += m1.getVanadium();
        this.iodine += m1.getIodine();
        this.cobalt += m1.getCobalt();
        this.molybdenum += m1.getMolybdenum();
        this.nickel += m1.getNickel();
        this.strontium += m1.getStrontium();
        this.titanium += m1.getTitanium();
        this.chrome += m1.getChrome();
        this.tin += m1.getTin();
        this.rubidium += m1.getRubidium();
        this.lithium += m1.getLithium();
        this.zirconium += m1.getZirconium();
        this.bromine += m1.getBromine();
    }

    public void subtract(Mineral m1) {
        this.calcium -= m1.getCalcium();
        this.iron -= m1.getIron();
        this.magnesium -= m1.getMagnesium();
        this.phosphorus -= m1.getPhosphorus();
        this.potassium -= m1.getPotassium();
        this.sodium -= m1.getSodium();
        this.zinc -= m1.getZinc();
        this.copper -= m1.getCopper();
        this.manganese -= m1.getManganese();
        this.selen -= m1.getSelen();
        this.fluorine -= m1.getFluorine();
        this.silicon -= m1.getSilicon();
        this.sulfur -= m1.getSulfur();
        this.chlorine -= m1.getChlorine();
        this.aluminum -= m1.getAluminum();
        this.bor -= m1.getBor();
        this.vanadium -= m1.getVanadium();
        this.iodine -= m1.getIodine();
        this.cobalt -= m1.getCobalt();
        this.molybdenum -= m1.getMolybdenum();
        this.nickel -= m1.getNickel();
        this.strontium -= m1.getStrontium();
        this.titanium -= m1.getTitanium();
        this.chrome -= m1.getChrome();
        this.tin -= m1.getTin();
        this.rubidium -= m1.getRubidium();
        this.lithium -= m1.getLithium();
        this.zirconium -= m1.getZirconium();
        this.bromine -= m1.getBromine();
    }

    public void modify(Float c) {
        this.calcium *= c;
        this.iron *= c;
        this.magnesium *= c;
        this.phosphorus *= c;
        this.potassium *= c;
        this.sodium *= c;
        this.zinc *= c;
        this.copper *= c;
        this.manganese *= c;
        this.selen *= c;
        this.fluorine *= c;
        this.silicon *= c;
        this.sulfur *= c;
        this.chlorine *= c;
        this.aluminum *= c;
        this.bor *= c;
        this.vanadium *= c;
        this.iodine *= c;
        this.cobalt *= c;
        this.molybdenum *= c;
        this.nickel *= c;
        this.strontium *= c;
        this.titanium *= c;
        this.chrome *= c;
        this.tin *= c;
        this.rubidium *= c;
        this.lithium *= c;
        this.zirconium *= c;
        this.bromine *= c;
    }

    public boolean compare(Float numb) {
        int overflowingNutrientsValue = 3;
        for (Float nutrient : getValues()) {
            if ((nutrient / numb) > 4) overflowingNutrientsValue = 0;
            else if (nutrient > numb) overflowingNutrientsValue--;

            if (overflowingNutrientsValue == 0) return false;
        }
        return true;
    }

    public double calculateAsh() {
        return calcium + iron + magnesium + phosphorus + potassium + sodium + zinc + copper + manganese + selen + fluorine + silicon
                + sulfur + chlorine + aluminum + bor + vanadium + iodine + cobalt + molybdenum + nickel + strontium + titanium + tin + chrome +
                rubidium + lithium + zirconium + bromine;
    }

    @JsonIgnore
    public List<Float> getValues() {
        return Stream.of(this.calcium, this.iron, this.magnesium, this.phosphorus, this.potassium,
                this.sodium, this.zinc, this.copper, this.manganese,
                silicon, sulfur, chlorine, aluminum, bor, vanadium, iodine, cobalt, molybdenum, nickel, strontium, titanium, this.fluorine, chrome, tin, this.selen,
                rubidium, lithium, zirconium, bromine)
                .collect(Collectors.toList());
    }

    public List<Float> getDangerousNutrients(){
        return Stream.of(this.selen, this.potassium, this.chlorine, this.bor,
                        this.bromine, this.vanadium, this.fluorine, this.chrome)
                .collect(Collectors.toList());
    }

    //dangerousNutrients - содержание того или иного опасного нутриента
    @JsonIgnore
    public List<Float> getPoints(List<Float> dangerousNutrients) {
        List<Float> mineralPoints = Stream.of(
                //Первая группа нутриентов - важные, редко имеется достаток
                this.calcium * 300,
                this.magnesium * 300,
                this.sulfur * 300,
                this.iron * 300,
                this.iodine * 300,
                this.selen * 300,
                this.zinc * 300,
                //Вторая группа нутриентов - более менее нормальное количество
                this.potassium * 200,
                this.silicon * 200,
                this.sodium * 200,
                this.phosphorus * 200,
                this.chlorine * 200,
                this.bor * 200,
                this.bromine * 200,
                this.vanadium * 200,
                this.cobalt * 200,
                this.manganese * 200,
                this.copper * 200,
                this.molybdenum * 200,
                this.fluorine * 200,
                this.chrome * 200
        ).collect(Collectors.toList());
        //Пятая группа - токсичные минералы
        if (dangerousNutrients.get(0) > 5000) {
            mineralPoints.set(5, -5000f);
        }
        if (dangerousNutrients.get(1) > 6000) {
            mineralPoints.set(7, -5000f);
        }
        if (dangerousNutrients.get(2) > 7000) {
            mineralPoints.set(11, -5000f);
        }
        if (dangerousNutrients.get(3) > 13) {
            mineralPoints.set(12, -5000f);
        }
        if (dangerousNutrients.get(4) > 1050) {
            mineralPoints.set(13, -5000f);
        }
        if (dangerousNutrients.get(5) > 250000) {
            mineralPoints.set(14, -5000f);
        }
        if (dangerousNutrients.get(6) > 20) {
            mineralPoints.set(19, -5000f);
        }
        if (dangerousNutrients.get(7) > 200000) {
            mineralPoints.set(20, -5000f);
        }

        return mineralPoints;
    }

    public Mineral(List<Float> norms) {
        this.calcium = norms.get(0);
        this.iron = norms.get(1);
        this.magnesium = norms.get(2);
        this.phosphorus = norms.get(3);
        this.potassium = norms.get(4);
        this.sodium = norms.get(5);
        this.zinc = norms.get(6);
        this.copper = norms.get(7);
        this.manganese = norms.get(8);
        this.selen = norms.get(24);
        this.fluorine = norms.get(21);
        this.silicon = norms.get(9);
        this.sulfur = norms.get(10);
        this.chlorine = norms.get(11);
        this.aluminum = norms.get(12);
        this.bor = norms.get(13);
        this.vanadium = norms.get(14);
        this.iodine = norms.get(15);
        this.cobalt = norms.get(16);
        this.molybdenum = norms.get(17);
        this.nickel = norms.get(18);
        this.strontium = norms.get(19);
        this.titanium = norms.get(20);
        this.chrome = norms.get(22);
        this.tin = norms.get(23);
        this.rubidium = norms.get(25);
        this.lithium = norms.get(26);
        this.zirconium = norms.get(27);
        this.bromine = norms.get(28);

        this.id = -1L;
        this.food = null;
    }

    public Mineral(Mineral m, Mineral mNorm) {
        this.calcium = m.getCalcium() / mNorm.getCalcium();
        this.iron = m.getIron() / mNorm.getIron();
        this.magnesium = m.getMagnesium() / mNorm.getMagnesium();
        this.phosphorus = m.getPhosphorus() / mNorm.getPhosphorus();
        this.potassium = m.getPotassium() / mNorm.getPotassium();
        this.sodium = m.getSodium() / mNorm.getSodium();
        this.zinc = m.getZinc() / mNorm.getZinc();
        this.copper = m.getCopper() / mNorm.getCopper();
        this.manganese = m.getManganese() / mNorm.getManganese();
        this.selen = m.getSelen() / mNorm.getSelen();
        this.fluorine = m.getFluorine() / mNorm.getFluorine();
        this.silicon = m.getSilicon() / mNorm.getSilicon();
        this.sulfur = m.getSulfur() / mNorm.getSulfur();
        this.chlorine = m.getChlorine() / mNorm.getChlorine();
        this.aluminum = m.getAluminum() / mNorm.getAluminum();
        this.bor = m.getBor() / mNorm.getBor();
        this.vanadium = m.getVanadium() / mNorm.getVanadium();
        this.iodine = m.getIodine() / mNorm.getIodine();
        this.cobalt = m.getCobalt() / mNorm.getCobalt();
        this.molybdenum = m.getMolybdenum() / mNorm.getMolybdenum();
        this.nickel = m.getNickel() / mNorm.getNickel();
        this.strontium = m.getStrontium() / mNorm.getStrontium();
        this.titanium = m.getTitanium() / mNorm.getTitanium();
        this.chrome = m.getChrome() / mNorm.getChrome();
        this.tin = m.getTin() / mNorm.getTin();
        this.rubidium = m.getRubidium() / mNorm.getRubidium();
        this.lithium = m.getLithium() / mNorm.getLithium();
        this.zirconium = m.getZirconium() / mNorm.getZirconium();
        this.bromine = m.getBromine() / mNorm.getBromine();

        this.id = -1L;
        this.food = null;
    }
}
