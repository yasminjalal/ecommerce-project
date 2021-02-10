package com.example.android.ProductRecycleView;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class Product {
    private int id;
    private String name;
    private String description;
    private Double weight;
    private Double price;
    private String oldPrice;
    private int quantity;
    private String imagePath;
    private String productBadge;
    private Boolean isDeliveryFree;
    private int catId;
    private boolean isFavorite;

    public Product() {

    }

    public Product(int id, String name, String description, Double weight, Double price, String oldPrice, int quantity, String imagePath, String productBadge, boolean isDeliveryFree, int catId, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.price = price;
        this.oldPrice = oldPrice;
        this.quantity = quantity;
        this.imagePath = imagePath;
        this.productBadge = productBadge;
        this.isDeliveryFree = isDeliveryFree;
        this.catId = catId;
        this.isFavorite = isFavorite;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getProductBadge() {
        return productBadge;
    }

    public void setProductBadge(String productBadge) {
        this.productBadge = productBadge;
    }

    public Boolean getDeliveryFree() {
        return isDeliveryFree;
    }

    public void setDeliveryFree(Boolean deliveryFree) {
        isDeliveryFree = deliveryFree;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
