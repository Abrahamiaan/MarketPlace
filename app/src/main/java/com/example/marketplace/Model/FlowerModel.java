package com.example.marketplace.Model;

import java.io.Serializable;
import java.util.List;

public class FlowerModel extends ProductModel implements Serializable {
    private List<Double> lengths;
    private List<String> colors;

    public FlowerModel() { }

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
