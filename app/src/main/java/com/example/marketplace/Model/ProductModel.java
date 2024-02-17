package com.example.marketplace.Model;

import com.google.type.DateTime;

import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import java.time.LocalDateTime;


public class ProductModel implements Serializable {
    private String productId;
    private String title;
    private String details;
    private String sellerId;
    private String seller;
    private String category;
    private String photo;
    private int price;
    private Date listedTime;
    private double longitude;
    private double latitude;

    public ProductModel() {
        listedTime = new Date();
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getSellerId() {
        return sellerId;
    }
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getSeller() {
        return seller;
    }
    public void setSeller(String seller) {
        this.seller = seller;
    }
    public Date getListedTime() {
        return listedTime;
    }
    public void setListedTime(Date listedTime) {
        this.listedTime = listedTime;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
