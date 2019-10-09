package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Vitamin {
    @Id
    private Long id;

    private float vitamin_a;
    private float beta_carotene;
    private float alpha_carotene;
    private float vitamin_d;
    private float vitamin_d2;
    private float vitamin_d3;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Food food;

    public void sum(Vitamin v1){
        this.vitamin_a+=v1.getVitamin_a();
        this.beta_carotene+=v1.getBeta_carotene();
        this.alpha_carotene+=v1.getAlpha_carotene();
        this.vitamin_d+=v1.getVitamin_d();
        this.vitamin_d2+=v1.getVitamin_d2();
        this.vitamin_d3+=v1.getVitamin_d3();
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
    }

    public void substract(Vitamin v1){
        this.vitamin_a-=v1.getVitamin_a();
        this.beta_carotene-=v1.getBeta_carotene();
        this.alpha_carotene-=v1.getAlpha_carotene();
        this.vitamin_d-=v1.getVitamin_d();
        this.vitamin_d2-=v1.getVitamin_d2();
        this.vitamin_d3-=v1.getVitamin_d3();
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
    }

    public void modify(Float c){
        this.vitamin_a*=c;
        this.beta_carotene*=c;
        this.alpha_carotene*=c;
        this.vitamin_d*=c;
        this.vitamin_d2*=c;
        this.vitamin_d3*=c;
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
    }
    
    @JsonIgnore
    public List<Float> getValues(){
        return Stream.of(vitamin_c, vitamin_b1, vitamin_b2, vitamin_b6, vitamin_b3,
                vitamin_b12, vitamin_b9, vitamin_b5, alpha_carotene, vitamin_a, beta_carotene,
                vitamin_e, vitamin_d, vitamin_k, vitamin_b4)
                .collect(Collectors.toList());
    }
    
    public Vitamin(List<Float> norms){
        this.vitamin_a=norms.get(0);
        this.beta_carotene=norms.get(1);
        this.alpha_carotene=norms.get(2);
        this.vitamin_d=norms.get(3);
        //Так как их норм для них пока что нет
        //this.vitamin_d2=norms.get(4);
        //this.vitamin_d3=norms.get(5);
        this.vitamin_e=norms.get(4);
        this.vitamin_k=norms.get(5);
        this.vitamin_c=norms.get(6);
        this.vitamin_b1=norms.get(7);
        this.vitamin_b2=norms.get(8);
        this.vitamin_b3=norms.get(9);
        this.vitamin_b4=norms.get(10);
        this.vitamin_b5=norms.get(11);
        this.vitamin_b6=norms.get(12);
        this.vitamin_b9=norms.get(13);
        this.vitamin_b12=norms.get(14);

        this.id = -1L;
        this.food = null;
    }
}
