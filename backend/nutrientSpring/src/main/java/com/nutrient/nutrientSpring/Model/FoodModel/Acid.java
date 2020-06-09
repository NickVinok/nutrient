package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutrient.nutrientSpring.Utils.NutrientGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.*;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@Table(name = "acids")
@AllArgsConstructor
@NoArgsConstructor
public class Acid implements NutrientGroup {
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

    @Column(name = "omega_3")
    private float omega3;
    @Column(name = "omega_6")
    private float omega6;
    @Column(name = "omega_9")
    private float omega9;
/*    @Nullable
    private Float hydroxyproline;
    @Column(name = "methionine_cystine")
    @Nullable
    private Float methionineCystine;
    @Column(name = "phenylalanine_tyrosine")
    @Nullable
    private Float phenylalanineTyrosine;*/

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
        this.omega3+=a1.getOmega3();
        this.omega6+=a1.getOmega6();
        this.omega9+=a1.getOmega9();
    }

    public void subtract(Acid a1){
        this.tryptophan-=a1.getTryptophan();
        this.threonine-=a1.getThreonine();
        this.isoleucine-=a1.getIsoleucine();
        this.leucine-=a1.getLeucine();
        this.lysine-=a1.getLysine();
        this.methionine-=a1.getMethionine();
        this.cystine-=a1.getCystine();
        this.phenylalanine-=a1.getPhenylalanine();
        this.tyrosine-=a1.getTyrosine();
        this.valine-=a1.getValine();
        this.arginine-=a1.getArginine();
        this.histidine-=a1.getHistidine();
        this.alanine-=a1.getAlanine();
        this.aspartic_acid-=a1.getAspartic_acid();
        this.glutamic_acid-=a1.getGlutamic_acid();
        this.glycine-=a1.getGlycine();
        this.proline-=a1.getProline();
        this.serine-=a1.getSerine();
        this.omega3-=a1.getOmega3();
        this.omega6-=a1.getOmega6();
        this.omega9-=a1.getOmega9();
    }

    public void modify(Float c){
        this.tryptophan*=c;
        this.threonine*=c;
        this.isoleucine*=c;
        this.leucine*=c;
        this.lysine*=c;
        this.methionine*=c;
        this.cystine*=c;
        this.phenylalanine*=c;
        this.tyrosine*=c;
        this.valine*=c;
        this.arginine*=c;
        this.histidine*=c;
        this.alanine*=c;
        this.aspartic_acid*=c;
        this.glutamic_acid*=c;
        this.glycine*=c;
        this.proline*=c;
        this.serine*=c;
        this.omega3*=c;
        this.omega6*=c;
        this.omega9*=c;
    }
    
    public boolean compare(Float numb){
        int overflowingNutrientsValue = 3;
        for(Float nutrient: getValues()){
            if(nutrient/numb>4) overflowingNutrientsValue=0;
            else if(nutrient>numb) overflowingNutrientsValue--;
            
            if(overflowingNutrientsValue == 0) return false;
        }
        return true;
    }
    
    @JsonIgnore
    public List<Float> getValues(){
        return Stream.of(this.tryptophan, this.threonine, this.isoleucine, this.leucine,
                this.lysine, this.methionine, this.cystine, this.phenylalanine, this.tyrosine,
                this.valine, this.arginine, this.histidine, this.alanine, this.aspartic_acid,
                this.glutamic_acid, this.glycine, this.proline, this.serine, omega3, omega6, omega9)
                .collect(Collectors.toList());
    }

    public Acid(List<Float> norms){
        this.tryptophan=norms.get(0);
        this.threonine=norms.get(1);
        this.isoleucine=norms.get(2);
        this.leucine=norms.get(3);
        this.lysine=norms.get(4);
        this.methionine=norms.get(5);
        this.cystine=norms.get(6);
        this.phenylalanine=norms.get(7);
        this.tyrosine=norms.get(8);
        this.valine=norms.get(9);
        this.arginine=norms.get(10);
        this.histidine=norms.get(11);
        this.alanine=norms.get(12);
        this.aspartic_acid=norms.get(13);
        this.glutamic_acid=norms.get(14);
        this.glycine=norms.get(15);
        this.proline=norms.get(16);
        this.serine=norms.get(17);

        this.id = -1L;
        this.food = null;
    }
    
    public Acid(Acid a, Acid aNorm){
        this.tryptophan= a.getTryptophan()/aNorm.getTryptophan();
        this.threonine= a.getThreonine()/aNorm.getThreonine();
        this.isoleucine= a.getIsoleucine()/aNorm.getIsoleucine();
        this.leucine= a.getLeucine()/aNorm.getLeucine();
        this.lysine= a.getLysine()/aNorm.getLysine();
        this.methionine= a.getMethionine()/aNorm.getMethionine();
        this.cystine= a.getCystine()/aNorm.getCystine();
        this.phenylalanine= a.getPhenylalanine()/aNorm.getPhenylalanine();
        this.tyrosine= a.getTyrosine()/aNorm.getTyrosine();
        this.valine= a.getValine()/aNorm.getValine();
        this.arginine= a.getArginine()/aNorm.getArginine();
        this.histidine= a.getHistidine()/aNorm.getHistidine();
        this.alanine= a.getAlanine()/aNorm.getAlanine();
        this.aspartic_acid= a.getAspartic_acid()/aNorm.getAspartic_acid();
        this.glutamic_acid= a.getGlutamic_acid()/aNorm.getGlutamic_acid();
        this.glycine= a.getGlycine()/aNorm.getGlycine();
        this.proline= a.getProline()/aNorm.getProline();
        this.serine= a.getSerine()/aNorm.getSerine();
        this.omega3=a.getOmega3()/aNorm.getOmega3();
        this.omega6=a.getOmega6()/aNorm.getOmega6();
        this.omega9=a.getOmega9()/aNorm.getOmega9();

        this.id = -1L;
        this.food = null;
    }
    public void calculateAcidNormsForMass(double mass){
        this.tryptophan*=mass;
        this.threonine*=mass;
        this.isoleucine*=mass;
        this.leucine*=mass;
        this.lysine*=mass;
        this.methionine*=mass;
        this.cystine*=mass;
        this.phenylalanine*=mass;
        this.valine*=mass;
        this.histidine*=mass;
    }
}
