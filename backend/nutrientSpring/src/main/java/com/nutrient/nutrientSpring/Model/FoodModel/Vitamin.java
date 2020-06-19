package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Utils.NutrientGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vitamins")
public class Vitamin implements NutrientGroup {
    @Id
    private Long id;

    private float vitamin_a;
    private float beta_carotene;
    private float alpha_carotene;
    private float vitamin_d;
    private float vitamin_e;
    private float vitamin_k;
    private float vitamin_c;
    private float vitamin_b1;
    private float vitamin_b2;
    private float vitamin_b3;
    private float vitamin_b4;
    private float vitamin_b5;
    private float vitamin_b6;
    private float vitamin_b9;
    private float vitamin_b12;
    private float vitamin_h;
    private float vitamin_pp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Food food;

    public void sum(Vitamin v1){
        this.vitamin_a+=v1.getVitamin_a();
        this.beta_carotene+=v1.getBeta_carotene();
        this.alpha_carotene+=v1.getAlpha_carotene();
        this.vitamin_d+=v1.getVitamin_d();
        this.vitamin_e+=v1.getVitamin_e();
        this.vitamin_k+=v1.getVitamin_k();
        this.vitamin_c+=v1.getVitamin_c();
        this.vitamin_b1+=v1.getVitamin_b1();
        this.vitamin_b2+=v1.getVitamin_b2();
        this.vitamin_b3+=v1.getVitamin_b3();
        this.vitamin_b4+=v1.getVitamin_b4();
        this.vitamin_b5+=v1.getVitamin_b5();
        this.vitamin_b6+=v1.getVitamin_b6();
        this.vitamin_b9+=v1.getVitamin_b9();
        this.vitamin_b12+=v1.getVitamin_b12();
        this.vitamin_h+=v1.getVitamin_h();
        this.vitamin_pp+=v1.getVitamin_pp();
    }

    public void subtract(Vitamin v1){
        /*for (Float val : this.getValues()){
            if(val < 0){
                System.out.print("Из чего вычитаем: ");
                System.out.println(this);
                System.out.print("Что вычитаем: ");
                System.out.println(v1);
            }
        }*/
        this.vitamin_a-=v1.getVitamin_a();
        this.beta_carotene-=v1.getBeta_carotene();
        this.alpha_carotene-=v1.getAlpha_carotene();
        this.vitamin_d-=v1.getVitamin_d();
        this.vitamin_e-=v1.getVitamin_e();
        this.vitamin_k-=v1.getVitamin_k();
        this.vitamin_c-=v1.getVitamin_c();
        this.vitamin_b1-=v1.getVitamin_b1();
        this.vitamin_b2-=v1.getVitamin_b2();
        this.vitamin_b3-=v1.getVitamin_b3();
        this.vitamin_b4-=v1.getVitamin_b4();
        this.vitamin_b5-=v1.getVitamin_b5();
        this.vitamin_b6-=v1.getVitamin_b6();
        this.vitamin_b9-=v1.getVitamin_b9();
        this.vitamin_b12-=v1.getVitamin_b12();
        this.vitamin_h-=v1.getVitamin_h();
        this.vitamin_pp-=v1.getVitamin_pp();
    }

    public void modify(Float c){
        this.vitamin_a*=c;
        this.beta_carotene*=c;
        this.alpha_carotene*=c;
        this.vitamin_d*=c;
        this.vitamin_e*=c;
        this.vitamin_k*=c;
        this.vitamin_c*=c;
        this.vitamin_b1*=c;
        this.vitamin_b2*=c;
        this.vitamin_b3*=c;
        this.vitamin_b4*=c;
        this.vitamin_b5*=c;
        this.vitamin_b6*=c;
        this.vitamin_b9*=c;
        this.vitamin_b12*=c;
        this.vitamin_h*=c;
        this.vitamin_pp*=c;
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
        return Stream.of(vitamin_a,beta_carotene, alpha_carotene, vitamin_d, vitamin_e,vitamin_k,
                vitamin_c, vitamin_b1, vitamin_b2, vitamin_b3, vitamin_b4,vitamin_b5,
                vitamin_b6, vitamin_b9, vitamin_b12, vitamin_pp, vitamin_h)
                .collect(Collectors.toList());
    }
    @JsonIgnore
    public List<Float> getPoints(){
        List<Float> mineralPoints = Stream.of(
        //Первая группа нутриентов - важные, редко имеется достаток
        this.vitamin_b1*300,
        this.vitamin_b2*300,
        this.vitamin_b12*300,
        //Вторая группа нутриентов - более менее нормальное количество
        this.beta_carotene*200,
        this.vitamin_a*200,
        this.vitamin_h*200,
        this.vitamin_b3*200,
        this.vitamin_b4*200,
        this.vitamin_b5*200,
        this.vitamin_b6*200,
        this.vitamin_b9*200,
        this.vitamin_c*200,
        this.vitamin_e*200,
        this.vitamin_d*200,
        this.vitamin_k*200,
        //ТРетья группа- не должно превышать определённого уровня
        this.alpha_carotene*100
        //Четвёртая группа - не должно превышать
        //Пятая группа - не должно первышать
        ).collect(Collectors.toList());
        return mineralPoints;
    }

    @JsonIgnore
    public int getMostOverflowingIndex(){
        List<Float> tmp= this.getValues();
        int index = 0;
        double value = 0;

        for(int i = 0;i<tmp.size();i++){
            if(tmp.get(i)>value){
                value = tmp.get(i);
                index = i;
            }
        }
        return index;
    }

    public Vitamin(List<Float> norms){
        //Так как их норм для них пока что нет
        //this.vitamin_d2=norms.get(4);
        //this.vitamin_d3=norms.get(5);
        this.vitamin_c=norms.get(6);
        this.vitamin_b1=norms.get(7);
        this.vitamin_b2=norms.get(8);
        this.vitamin_b6=norms.get(12);
        this.vitamin_b3=norms.get(9);
        this.vitamin_b12=norms.get(14);
        this.vitamin_b9=norms.get(13);
        this.vitamin_b5=norms.get(11);
        this.alpha_carotene=norms.get(2);
        this.vitamin_a=norms.get(0);
        this.beta_carotene=norms.get(1);
        this.vitamin_e=norms.get(4);
        this.vitamin_d=norms.get(3);
        this.vitamin_k=norms.get(5);
        this.vitamin_b4=norms.get(10);
        this.vitamin_pp=norms.get(15);
        this.vitamin_h=norms.get(16);

        this.id = -1L;
        this.food = null;
    }

    public Vitamin(Vitamin v, Vitamin vNorm){
        this.vitamin_a=v.getVitamin_a()/vNorm.getVitamin_a();
        this.beta_carotene=v.getBeta_carotene()/vNorm.getBeta_carotene();
        this.alpha_carotene=v.getAlpha_carotene()/vNorm.getAlpha_carotene();
        this.vitamin_d=v.getVitamin_d()/vNorm.getVitamin_d();
        //Так как их норм для них пока что нет
        //this.vitamin_d2=norms.get(4);
        //this.vitamin_d3=norms.get(5);
        this.vitamin_e=v.getVitamin_e()/vNorm.getVitamin_e();
        this.vitamin_k=v.getVitamin_k()/vNorm.getVitamin_k();
        this.vitamin_c=v.getVitamin_c()/vNorm.getVitamin_c();
        this.vitamin_b1=v.getVitamin_b1()/vNorm.getVitamin_b1();
        this.vitamin_b2=v.getVitamin_b2()/vNorm.getVitamin_b2();
        this.vitamin_b3=v.getVitamin_b3()/vNorm.getVitamin_b3();
        this.vitamin_b4=v.getVitamin_b4()/vNorm.getVitamin_b4();
        this.vitamin_b5=v.getVitamin_b5()/vNorm.getVitamin_b5();
        this.vitamin_b6=v.getVitamin_b6()/vNorm.getVitamin_b6();
        this.vitamin_b9=v.getVitamin_b9()/vNorm.getVitamin_b9();
        this.vitamin_b12=v.getVitamin_b12()/vNorm.getVitamin_b12();
        this.vitamin_h=v.getVitamin_h()/vNorm.getVitamin_h();
        this.vitamin_pp=v.getVitamin_pp()/vNorm.getVitamin_pp();

        this.id = -1L;
        this.food = null;
    }
}
