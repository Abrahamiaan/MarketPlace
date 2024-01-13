package com.example.marketplace.Utils;

import com.example.marketplace.Model.FlowerModel;

import java.util.Comparator;

public class SortHelper {
    public static Comparator<FlowerModel> sortByAscPrice = Comparator.comparingInt(FlowerModel::getPrice);
    public static Comparator<FlowerModel> sortByDescPrice = (o1, o2) -> Integer.compare(o2.getPrice(), o1.getPrice());
    public static Comparator<FlowerModel> sortByListedDate = Comparator
            .comparing(FlowerModel::getListedTime, Comparator.nullsLast(Comparator.naturalOrder()));
}
