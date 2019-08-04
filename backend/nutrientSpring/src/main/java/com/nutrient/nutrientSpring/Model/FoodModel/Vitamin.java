package com.nutrient.nutrientSpring.Model.FoodModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

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
}
