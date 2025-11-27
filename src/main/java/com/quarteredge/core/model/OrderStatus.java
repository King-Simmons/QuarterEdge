package com.quarteredge.core.model;

/**
 * Represents the status of an order in the trading system.
 *
 * <p>An order progresses through different states during its lifecycle, from initial creation to
 * final closure.
 *
 * @author King Simmons
 * @version 1.0
 * @since 1.0
 */
public enum OrderStatus {
    /**
     * Indicates that the order has been created but not yet activated. The order is waiting to be
     * processed or submitted to the market. I.e., limit or stop orders
     */
    PENDING,
    /** Indicates that the order is currently active in the market. */
    ACTIVE,
    /** Indicates that the order has been closed due to the take-profit price being hit. */
    CLOSED_TP_HIT,
    /** Indicates that the order has been closed due to the stop-loss price being hit. */
    CLOSED_SL_HIT,
    /** Indicates that the order has been closed manually. Either by the user or the system. */
    CLOSED_MANUAL,
    /**
     * Indicates that the order has been canceled. I.e., price-limit is never triggered by the end
     * of the trading session.
     */
    CLOSED_CANCELED,
    /** Indicates that the order status is unknown and cannot be determined. */
    CLOSED_UNKNOWN
}
