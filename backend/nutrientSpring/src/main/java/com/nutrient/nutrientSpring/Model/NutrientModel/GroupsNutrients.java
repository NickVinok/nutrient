package com.nutrient.nutrientSpring.Model.NutrientModel;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "GroupsNutrients")
public class GroupsNutrients {
    @EmbeddedId
    private GroupsNutrientsCompositeKey groupsNutrientsCompositeKey;
    private float value;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idGroup")
    @JoinColumn(name = "idGroup", referencedColumnName = "id")
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idNutrient")
    @JoinColumn(name = "idNutrient", referencedColumnName = "id")
    private Nutrient nutrient;
}
