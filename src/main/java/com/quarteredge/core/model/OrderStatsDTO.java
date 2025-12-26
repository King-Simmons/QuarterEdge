package com.quarteredge.core.model;

public class OrderStatsDTO {
    private double maximumFavorablePrice;
    private double maximumAdversePrice;

    public OrderStatsDTO(double maximumFavorablePrice, double maximumAdversePrice) {
        this.maximumFavorablePrice = maximumFavorablePrice;
        this.maximumAdversePrice = maximumAdversePrice;
    }

    public double getMaximumFavorablePrice() {
        return maximumFavorablePrice;
    }

    public double getMaximumAdversePrice() {
        return maximumAdversePrice;
    }

    public void setMaximumFavorablePrice(double maximumFavorablePrice) {
        this.maximumFavorablePrice = maximumFavorablePrice;
    }

    public void setMaximumAdversePrice(double maximumAdversePrice) {
        this.maximumAdversePrice = maximumAdversePrice;
    }
}
