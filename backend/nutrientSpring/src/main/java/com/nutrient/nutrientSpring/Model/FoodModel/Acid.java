package com.nutrient.nutrientSpring.Model.FoodModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.*;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table(name = "acids")
@AllArgsConstructor
@NoArgsConstructor
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

    public void sum(Acid a1){
        this.tryptophan+=a1.getTryptophan();
        this.threonine+=a1.getThreonine();
        this.isoleucine+=a1.getIsoleucine();
        this.leucine+=a1.getLeucine();
        this.lysine+=a1.getLysine();
        this.methionine+=a1.getMethionine();
        this.cystine+=a1.getCystine();
        this.phenylalanine+=a1.getPhenylalanine();
        this.tyrosine+=a1.getTyrosine();
        this.valine+=a1.getValine();
        this.arginine+=a1.getArginine();
        this.histidine+=a1.getHistidine();
        this.alanine+=a1.getAlanine();
        this.aspartic_acid+=a1.getAspartic_acid();
        this.glutamic_acid+=a1.getGlutamic_acid();
        this.glycine+=a1.getGlycine();
        this.proline+=a1.getProline();
        this.serine+=a1.getSerine();
    }
}
