package com.quarteredge.core.model;

public class OrderStatsDTO {
    /**
     * Keeps track of the Maximum Favorable Price. The highest/lowest a price goes in the trade's
     * favor.
     */
    private double maximumFavorablePrice;

    /**
     * Keeps track of the Maximum Adverse Price. The highest/lowest a price goes in the trade's
     * opposite favor.
     */
    private double maximumAdversePrice;

    /**
     * Constructs an instance of OrderStatsDTO with the specified maximum favorable and adverse
     * prices.
     *
     * @param maximumFavorablePrice the maximum favorable price, representing the most profitable
     *     price movement observed
     * @param maximumAdversePrice the maximum adverse price, representing the most unfavorable price
     *     movement observed
     */
    public OrderStatsDTO(final double maximumFavorablePrice, final double maximumAdversePrice) {
        this.maximumFavorablePrice = maximumFavorablePrice;
        this.maximumAdversePrice = maximumAdversePrice;
    }

    /**
     * Retrieves the maximum favorable price for the order. The maximum favorable price refers to
     * the most profitable price movement recorded during the lifetime of the order.
     *
     * @return the maximum favorable price as a double
     */
    public double getMaximumFavorablePrice() {
        return maximumFavorablePrice;
    }

    /**
     * Retrieves the maximum adverse price for the order. The maximum adverse price refers to the
     * most unfavorable price movement recorded during the lifetime of the order.
     *
     * @return the maximum adverse price as a double
     */
    public double getMaximumAdversePrice() {
        return maximumAdversePrice;
    }

    /**
     * Sets the maximum favorable price for the order. The maximum favorable price refers to the
     * most profitable price movement recorded during the lifetime of the order.
     *
     * @param maximumFavorablePrice the maximum favorable price to be set
     */
    public void setMaximumFavorablePrice(final double maximumFavorablePrice) {
        this.maximumFavorablePrice = maximumFavorablePrice;
    }

    /**
     * Sets the maximum adverse price for the order. The maximum adverse price refers to the most
     * unfavorable price movement recorded during the lifetime of the order.
     *
     * @param maximumAdversePrice the maximum adverse price to be set
     */
    public void setMaximumAdversePrice(final double maximumAdversePrice) {
        this.maximumAdversePrice = maximumAdversePrice;
    }
}
