package com.example.android.TrackingShipment;


/**
 * Created by Yasmin Jalal - 2019.
 */

public class TrackingShipment {

    private String Id;
    private String shipmentRef;
    private String userId;
    private String dateTime;
    private String info;

    public TrackingShipment() {
    }

    public TrackingShipment(String id, String shipmentRef, String userId, String dateTime, String info) {
        Id = id;
        this.shipmentRef = shipmentRef;
        this.userId = userId;
        this.dateTime = dateTime;
        this.info = info;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getShipmentRef() {
        return shipmentRef;
    }

    public void setShipmentRef(String shipmentRef) {
        this.shipmentRef = shipmentRef;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
