package com.nutrient.nutrientSpring.Model.FoodModel;

import lombok.Data;
import javax.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table(name = "acids")
public class Acid {
    @Id
    private Long id;

    private float tryptophan;
    private float threonine;
    private float isoleucine;
    private float leucine;
    private float lysine;
    private float methionine;
    private float cystine;
    private float phenylalanine;
    private float tyrosine;
    private float valine;
    private float arginine;
    private float histidine;
    private float alanine;
    private float aspartic_acid;
    private float glutamic_acid;
    private float glycine;
    private float proline;
    private float serine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Food food;
}
