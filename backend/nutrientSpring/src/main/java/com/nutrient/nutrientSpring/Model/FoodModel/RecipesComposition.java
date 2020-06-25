package com.nutrient.nutrientSpring.Model.FoodModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "recipes_composition")
public class RecipesComposition {
    @Id
    private long id;

    @Column(name = "coef_for_men")
    private double coefMan;
    @Column(name = "coef_for_women")
    private double coefWomen;

    private double energy;
    private double fat;
    private double protein;
    private double carbohydrate;
    private double water;
    private double ash;
    private double sugares;
    private double sugars;
    private double fiber;
    private double starch;
    private double cholesterol;
    private double sfa;
    @Column(name = "organic_acid")
    private double organicAcid;
    private double glucose;
    private double fructose;
    private double phytosterol;
    //Acid
    private double tryptophan;
    private double threonine;
    private double isoleucine;
    private double leucine;
    private double lysine;
    private double methionine;
    private double cystine;
    private double phenylalanine;
    private double tyrosine;
    private double valine;
    private double arginine;
    private double histidine;
    private double alanine;
    private double aspartic_acid;
    private double glutamic_acid;
    private double glycine;
    private double proline;
    private double serine;
    @Column(name = "omega_3")
    private double omega3;
    @Column(name = "omega_6")
    private double omega6;
    @Column(name = "omega_9")
    private double omega9;
    private double saccharose;
    private double lactose;
    private double galactose;
    private double hydroxyproline;
    @Column(name = "methionine_cystine")
    private double methionineCystine;
    @Column(name = "phenylalanine_tyrosine")
    private double phenylalanineTyrosine;
    @Column(name = "cystine_hydroxyproline")
    private double cystineHydroxyproline;
    private double transFat;
    private double linoleic;
    private double linolenic;
    private double palmitoleic;
    //Mineral
    private double calcium;
    private double iron;
    private double magnesium;
    private double phosphorus;
    private double potassium;
    private double sodium;
    private double zinc;
    private double copper;
    private double manganese;
    private double selen;
    private double fluorine;
    private double silicon;
    private double sulfur;
    private double chlorine;
    private double aluminum;
    private double bor;
    private double vanadium;
    private double iodine;
    private double cobalt;
    private double molybdenum;
    private double nickel;
    private double strontium;
    private double titanium;
    private double chrome;
    private double tin;
    private double rubidium;
    private double lithium;
    private double zirconium;
    private double bromine;
    //Vitamin
    private double vitamin_a;
    private double beta_carotene;
    private double alpha_carotene;
    private double vitamin_d;
    private double vitamin_d2;
    private double vitamin_d3;
    private double vitamin_e;
    private double vitamin_k;
    private double vitamin_c;
    private double vitamin_b1;
    private double vitamin_b2;
    private double vitamin_b3;
    private double vitamin_b4;
    private double vitamin_b5;
    private double vitamin_b6;
    private double vitamin_b9;
    private double vitamin_b12;
    private double vitamin_retinol;
    private double vitamin_h;
    private double vitamin_pp;
    private double lycopene;
    @Column(name = "luthein_zeaxnthin")
    private double luteinZeaxnthin;
    @Column(name = "gamma_tocopherol")
    private double gammaTocopherol;
    @Column(name = "delta_tocopherol")
    private double deltaTocopherol;
    private double betaine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="recipe_id", referencedColumnName = "id")
    private Recipes recipes;

    @JsonIgnore
    public Food getFood(){
        return new Food( -10L, "Рецепт", 0L,
        (float)this.energy,
        (float)this.fat,
        (float)this.protein,
        (float)this.carbohydrate,
        (float)this.water,
        (float)this.ash,
        (float)this.sugars,
        (float)this.fiber,
        (float)this.starch,
        (float)this.cholesterol,
        "-5676",
        (float)this.organicAcid, 
        (float)this.sfa,
        this.glucose,
        this.fructose, null);
    }
    @JsonIgnore
    public Vitamin getVitamin(){
        return new Vitamin(-10L,
        (float)this.vitamin_a,
        (float)this.beta_carotene,
        (float)this.alpha_carotene,
        (float)this.vitamin_d,
        (float)this.vitamin_e,
        (float)this.vitamin_k,
        (float)this.vitamin_c,
        (float)this.vitamin_b1,
        (float)this.vitamin_b2,
        (float)this.vitamin_b3,
        (float)this.vitamin_b4,
        (float)this.vitamin_b5,
        (float)this.vitamin_b6,
        (float)this.vitamin_b9,
        (float)this.vitamin_b12,
        (float)this.vitamin_h,
        (float)this.vitamin_pp, null);
    }
    @JsonIgnore
    public Mineral getMineral(){
        return new Mineral(-10L,
        (float)this.calcium,
        (float)this.iron,
        (float)this.magnesium,
        (float)this.phosphorus,
        (float)this.potassium,
        (float)this.sodium,
        (float)this.zinc,
        (float)this.copper,
        (float)this.manganese,
        (float)this.selen,
        (float)this.fluorine,
        (float)this.silicon,
        (float)this.sulfur,
        (float)this.chlorine,
        (float)this.aluminum,
        (float)this.bor,
        (float)this.vanadium,
        (float)this.iodine,
        (float)this.cobalt,
        (float)this.molybdenum,
        (float)this.nickel,
        (float)this.strontium,
        (float)this.titanium,
        (float)this.chrome,
        (float)this.tin,
        (float)this.rubidium,
        (float)this.lithium,
        (float)this.zirconium,
        (float)this.bromine, null);
    }
    @JsonIgnore
    public Acid getAcid(){
        return new Acid(-10L,
        (float)this.tryptophan,
        (float)this.threonine,
        (float)this.isoleucine,
        (float)this.leucine,
        (float)this.lysine,
        (float)this.methionine,
        (float)this.cystine,
        (float)this.phenylalanine,
        (float)this.tyrosine,
        (float)this.valine,
        (float)this.arginine,
        (float)this.histidine,
        (float)this.alanine,
        (float)this.aspartic_acid,
        (float)this.glutamic_acid,
        (float)this.glycine,
        (float)this.proline,
        (float)this.serine,
        (float)this.omega3,
        (float)this.omega6,
        (float)this.omega9,
        (float)this.saccharose,
        (float)this.lactose,
        (float)this.galactose,null);
    }
}
