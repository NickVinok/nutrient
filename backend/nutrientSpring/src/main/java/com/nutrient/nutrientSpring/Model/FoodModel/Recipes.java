package com.nutrient.nutrientSpring.Model.FoodModel;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "recipes")
public class Recipes{
    @Id
    private long id;
    private String name;
    private String instruction;
    @Column(name = "dish_type")
    private long dishType;
    @Column(name = "public_date")
    private Timestamp publicDate;
    @Column(name = "source_link")
    private String sourceLink;
}
