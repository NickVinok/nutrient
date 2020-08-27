package com.nutrient.nutrientSpring.Model.FoodModel;

import com.nutrient.nutrientSpring.Model.FoodModel.Keys.DietTypesTagsKey;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "bla-bla-bla")
public class DietTypesTags {
    @EmbeddedId
    private DietTypesTagsKey dietTypesTagsKey;

    @ManyToOne(fetch= FetchType.EAGER)
    @MapsId("diet_types_id")
    @JoinColumn(name="diet_types_id", referencedColumnName = "id")
    private DietTypes dietTypes;

    @ManyToOne(fetch= FetchType.EAGER)
    @MapsId("tags_id")
    @JoinColumn(name="tags_id", referencedColumnName = "id")
    private Tags tags;
}
