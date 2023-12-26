package com.example.marketplace.Model;

public class FlowerModel {
    int imageUrl;
    private String title;
    private float length;
    private int price;

    public FlowerModel(String title, float length, int price, int imageUrl) {
        this.title = title;
        this.length = 10; // TODO: Change Real Length
        this.price = price;
        this.imageUrl = imageUrl;
    }
    public FlowerModel(String title, int price, int imageUrl) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }


    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
