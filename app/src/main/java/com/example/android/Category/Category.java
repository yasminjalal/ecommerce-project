package com.example.android.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class Category {
    private int id;
    private String name ;
    private String type;
    private String description;
    private String imagePath;
    private Boolean hasSimilarProducts;
    private int originalType;
    List<Category> subCategory = new ArrayList<>();

    public Category() {
    }

    public Category(int id, String name, String type, String description, String imagePath, boolean hasSimilarProducts, int originalType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.imagePath = imagePath;
        this.hasSimilarProducts = hasSimilarProducts;
        this.originalType = originalType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Boolean getHasSimilarProducts() {
        return hasSimilarProducts;
    }

    public void setHasSimilarProducts(Boolean hasSimilarProducts) {
        this.hasSimilarProducts = hasSimilarProducts;
    }

    public List<Category> getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(List<Category> subCategory) {
        this.subCategory = subCategory;
    }

    public int getOriginalType() {
        return originalType;
    }

    public void setOriginalType(int originalType) {
        this.originalType = originalType;
    }
}
