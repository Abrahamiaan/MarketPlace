package com.example.marketplace.Model;

public class Category {
    int id;
    Integer imageUrl;
    String  name;
    String  title;

    public Category(int id, Integer imageUrl, String name, String title) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public Integer getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageUrl(Integer imageUrl) {
        this.imageUrl = imageUrl;
    }
}
