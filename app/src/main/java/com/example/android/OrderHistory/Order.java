package com.example.android.OrderHistory;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class Order {

    private int id;
    private int productId;
    private int userId;
    private int quantity;
    private int price;
    private String dateTime;
    private String paymentType;
    private String paymentDocument;
    private String shipmentRef;
    private String info;

    public Order() {
    }

    public Order(int id, int productId, int userId, int quantity, int price, String dateTime, String paymentType, String paymentDocument, String shipmentRef, String info) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.quantity = quantity;
        this.price = price;
        this.dateTime = dateTime;
        this.paymentType = paymentType;
        this.paymentDocument = paymentDocument;
        this.shipmentRef = shipmentRef;
        this.info = info;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentDocument() {
        return paymentDocument;
    }

    public void setPaymentDocument(String paymentDocument) {
        this.paymentDocument = paymentDocument;
    }

    public String getShipmentRef() {
        return shipmentRef;
    }

    public void setShipmentRef(String shipmentRef) {
        this.shipmentRef = shipmentRef;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
