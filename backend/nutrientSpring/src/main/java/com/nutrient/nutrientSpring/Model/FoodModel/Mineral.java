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

    private float calcium ;
    private float iron ;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Food food;

    public void sum(Mineral m1){
        this.calcium += m1.getCalcium();
        this.iron += m1.getIron();
        this.magnesium+= m1.getMagnesium();
        this.phosphorus += m1.getPhosphorus();
        this.potassium += m1.getPotassium();
        this.sodium += m1.getSodium();
        this.zinc += m1.getZinc();
        this.copper += m1.getCopper();
        this.manganese+= m1.getManganese();
        this.selen += m1.getSelen();
        this.fluorine +=m1.getFluorine();
    }

    public void subtract(Mineral m1){
        this.calcium -= m1.getCalcium();
        this.iron -= m1.getIron();
        this.magnesium-= m1.getMagnesium();
        this.phosphorus -= m1.getPhosphorus();
        this.potassium -= m1.getPotassium();
        this.sodium -= m1.getSodium();
        this.zinc -= m1.getZinc();
        this.copper -= m1.getCopper();
        this.manganese-= m1.getManganese();
        this.selen -= m1.getSelen();
        this.fluorine -=m1.getFluorine();
    }

    public void modify(Float c){
        this.calcium *= c;
        this.iron *= c;
        this.magnesium*= c;
        this.phosphorus *= c;
        this.potassium *= c;
        this.sodium *= c;
        this.zinc *= c;
        this.copper *= c;
        this.manganese*= c;
        this.selen *= c;
        this.fluorine *=c;
    }
    
    public boolean compare(Float numb){
        int overflowingNutrientsValue = 3;
        for(Float nutrient: getValues()){
            if((nutrient/numb)>4) overflowingNutrientsValue=0;
            else if(nutrient>numb) overflowingNutrientsValue--;

            if(overflowingNutrientsValue == 0) return false;
        }
        return true;
    }
    
    @JsonIgnore
    public List<Float> getValues(){
        return Stream.of(this.calcium, this.phosphorus, this.magnesium, this.potassium,
                this.sodium, this.iron, this.zinc, this.copper, this.manganese, this.selen, this.fluorine)
                .collect(Collectors.toList());
    }

    public Mineral(List<Float> norms){
        this.calcium = norms.get(0);
        this.phosphorus = norms.get(1);
        this.magnesium= norms.get(2);
        this.potassium = norms.get(3);
        this.sodium = norms.get(4);
        this.iron = norms.get(5);
        this.zinc = norms.get(6);
        this.copper = norms.get(7);
        this.manganese= norms.get(8);
        this.selen = norms.get(9);
        this.fluorine =norms.get(10);

        this.id = -1L;
        this.food = null;
    }

    public Mineral(Mineral m, Mineral mNorm){
        this.calcium = m.getCalcium()/mNorm.getCalcium();
        this.iron = m.getIron()/mNorm.getIron();
        this.magnesium= m.getMagnesium()/mNorm.getMagnesium();
        this.phosphorus = m.getPhosphorus()/mNorm.getPhosphorus();
        this.potassium = m.getPotassium()/mNorm.getPotassium();
        this.sodium = m.getSodium()/mNorm.getSodium();
        this.zinc = m.getZinc()/mNorm.getZinc();
        this.copper = m.getCopper()/mNorm.getCopper();
        this.manganese= m.getManganese()/mNorm.getManganese();
        this.selen = m.getSelen()/mNorm.getSelen();
        this.fluorine =m.getFluorine()/mNorm.getFluorine();

        this.id = -1L;
        this.food = null;
    }

}
