package com.example.marketplace.Model;

import java.util.Date;

public class OrderModel {
    private String orderId;
    private String ownerId;
    private String assignedDriverId;
    private CartModel product;
    private Date orderDate;
    private double totalPrice;
    private double longitude;
    private double latitude;
    private Date deliveredAt;

    public OrderModel() {
        this.orderDate = new Date();
        deliveredAt = null;
        assignedDriverId = "";
    }

    public String getAssignedDriverId() {
        return assignedDriverId;
    }

    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Date isDelivered() {
        return deliveredAt;
    }

    public void setDelivered(Date delivered) {
        this.deliveredAt = delivered;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public CartModel getProduct() {
        return product;
    }

    public void setProduct(CartModel product) {
        this.product = product;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
