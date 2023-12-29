package com.example.marketplace.Model;

public class FlowerModel {
    private String title;
    private int price;
    private String photo;

    public FlowerModel(String title, int price, String photo) {
        this.title = title;
        this.price = price;
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
