package com.nutrient.nutrientSpring.Model.FoodModel;

import com.nutrient.nutrientSpring.Model.FoodModel.Keys.FoodTagKey;
import lombok.Data;
import org.springframework.stereotype.Repository;

import javax.persistence.*;

@Data
@Entity
@Table(name = "food_tag")
public class FoodTag {
    @EmbeddedId
    private FoodTagKey foodTagKey;

    @ManyToOne(fetch= FetchType.EAGER)
    @MapsId("food_id")
    @JoinColumn(name="food_id", referencedColumnName = "id")
    private Food food;

    @ManyToOne(fetch= FetchType.EAGER)
    @MapsId("tag_id")
    @JoinColumn(name="tag_id", referencedColumnName = "id")
    private Tags tags;
}
