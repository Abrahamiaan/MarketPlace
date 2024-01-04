package com.example.marketplace.Model;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable{
    private String productId;
    private String title;
    private String category;
    private String details;
    private String photo;
    private int price;
    private String seller;
    private List<Double> lengths;
    private List<String> colors;

    public ProductModel() {

    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public List<Double> getLengths() {
        return lengths;
    }

    public void setLengths(List<Double> lengths) {
        this.lengths = lengths;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }
}
