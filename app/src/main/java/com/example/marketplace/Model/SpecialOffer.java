package com.example.marketplace.Model;

public class SpecialOffer {
    Integer imgUrl;
    Integer unionsUrl;
    Integer bgColor;
    String title;

    public SpecialOffer(String title, Integer imgUrl, Integer unionsUrl, Integer bgColor) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.bgColor = bgColor;
        this.unionsUrl = unionsUrl;
    }

    public Integer getImgUrl() {
        return imgUrl;
    }

    public Integer getUnionsUrl() {
        return unionsUrl;
    }

    public Integer getBgColor() {
        return bgColor;
    }

    public String getTitle() {
        return title;
    }
}
