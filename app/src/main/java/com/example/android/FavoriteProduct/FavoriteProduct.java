package com.example.android.FavoriteProduct;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class FavoriteProduct {
    private int id;
    private String name;
    private String description;
    private Double weight;
    private Double price;
    private String oldPrice;
    private int quantity;
    private String imagePath;
    private String productBadge;
    private int catId;
    private boolean isFavorite;
    private Boolean isDeliveryFree;


    public FavoriteProduct() {

    }

    public FavoriteProduct(int id, String name, String description, Double weight, Double price, String oldPrice, int quantity, String imagePath, String productBadge, int catId, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.price = price;
        this.oldPrice = oldPrice;
        this.quantity = quantity;
        this.imagePath = imagePath;
        this.productBadge = productBadge;
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

    public Boolean getDeliveryFree() {
        return isDeliveryFree;
    }

    public void setDeliveryFree(Boolean deliveryFree) {
        isDeliveryFree = deliveryFree;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getProductBadge() {
        return productBadge;
    }

    public void setProductBadge(String productBadge) {
        this.productBadge = productBadge;
    }
}
