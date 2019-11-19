package com.nutrient.nutrientSpring.JsonObjects.FoodRest;

import java.util.HashMap;


public class PackedJsonObject {
    private String type;
    private Long id;
    private HashMap<String, Object> attributes;
    private HashMap<String, Object> relationships;

    public String getType(){
        return type;
    }

    public HashMap<String, Object> getRelationships(){
        return relationships;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setRelationships(HashMap<String, Object> relationships){
        this.relationships = relationships;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PackedJsonObject(){
        this.attributes = new HashMap<>();
        this.relationships = new HashMap<>();
    }

    public void addAttribute(String name, Object value){
        this.attributes.put(name, value);
    }

    public void addRelationship(String relation, Object object){
        relationships.put(relation, object);
    }
}
