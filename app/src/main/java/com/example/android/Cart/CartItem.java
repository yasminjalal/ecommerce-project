package com.example.android.Cart;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class CartItem {
    private String name;
    private String description;
    private Double price;
    private Double weight;
    private String imagePath;
    private int catId;
    private int id;
    private int productId;
    private int userId;
    private Boolean isDeliveryFree;
    private int quantity;

    public CartItem(String name, String description, Double price, Double weight, String imagePath, boolean isDeliveryFree, int catId, int id, int productId, int userId, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.imagePath = imagePath;
        this.catId = catId;
        this.isDeliveryFree = isDeliveryFree;
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.quantity = quantity;
    }

    public CartItem() {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean getDeliveryFree() {
        return isDeliveryFree;
    }

    public void setDeliveryFree(Boolean deliveryFree) {
        isDeliveryFree = deliveryFree;
    }
    public void setDeliveryFreeString(String deliveryFree) {
        if(deliveryFree.equals("yes"))
        isDeliveryFree = true;
        else if(deliveryFree.equals("no"))
            isDeliveryFree = false;

    }
}
