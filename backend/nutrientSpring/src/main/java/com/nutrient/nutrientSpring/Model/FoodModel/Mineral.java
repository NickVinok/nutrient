package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Mineral {
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
    private float selenium;
    private float fluoride;

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
        this.selenium += m1.getSelenium();
        this.fluoride +=m1.getFluoride();
    }
    @JsonIgnore
    public List<Float> getValues(){
        return Stream.of(this.calcium, this.phosphorus, this.magnesium, this.potassium,
                this.sodium, this.iron, this.zinc, this.copper, this.manganese, this.selenium, this.fluoride)
                .collect(Collectors.toList());
    }
}
