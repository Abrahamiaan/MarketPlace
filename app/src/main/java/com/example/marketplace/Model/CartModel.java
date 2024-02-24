package com.example.marketplace.Model;

public class CartModel {
    String ownerId;
    ProductModel productModel;
    int count;

    public CartModel(ProductModel productModel, int count, String id) {
        this.ownerId = id;
        this.productModel = productModel;
        this.count = count;
    }

    public CartModel() {}

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ProductModel getProductModel() {
        return productModel;
    }

    public void setProductModel(ProductModel productModel) {
        this.productModel = productModel;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
