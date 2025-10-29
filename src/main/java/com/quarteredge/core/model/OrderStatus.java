package com.quarteredge.core.model;

/**
 * Represents the status of an order in the trading system.
 * <p>
 * An order progresses through different states during its lifecycle,
 * from initial creation to final closure.
 * </p>
 *
 * @author King Simmons
 * @version 1.0
 * @since 1.0
 */
public enum OrderStatus {
    /**
     * Indicates that the order has been created but not yet activated.
     * The order is waiting to be processed or submitted to the market.
     * I.e., limit or stop orders
     */
    PENDING,
    /**
     * Indicates that the order is currently active in the market.
     */
    ACTIVE,
    /**
     * Indicates that the order has been closed.
     * The order has reached its target, hit stop loss or expired.
     */
    CLOSED
}
