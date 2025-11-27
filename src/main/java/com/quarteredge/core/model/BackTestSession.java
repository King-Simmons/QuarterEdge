package com.quarteredge.core.model;

import static com.quarteredge.core.util.Constants.LAST_CANDLE_CLOSE_TIME;

import com.quarteredge.core.strategy.Strategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * BackTestSession class.
 *
 * <p>This class represents a backtesting session for evaluating the performance of a trading
 * strategy. It processes a sequence of candlestick data points and generates a list of orders based
 * on the strategy's decisions.
 *
 * @author King Simmons
 * @version 1.0
 * @since 1.0
 * @see CandleDTO
 * @see OrderDTO
 * @see Strategy
 * @see SessionStatus
 */
public class BackTestSession {
    /** Represents the list of candlestick data points used during a backtesting session. */
    private final List<CandleDTO> data;

    /** Represents the list of orders generated during the backtesting session. */
    private final List<OrderDTO> orders;

    /** Represents the strategy used for backtesting. */
    private final Strategy strategy;

    /** Represents the current status of the backtesting session. */
    private SessionStatus status;

    /**
     * Constructs a new BackTestSession with the specified strategy and data.
     *
     * @param strategy the strategy to use for backtesting
     * @param data the list of candlestick data points that will be processed
     */
    public BackTestSession(final Strategy strategy, final List<CandleDTO> data) {
        this.strategy = strategy;
        this.data = data;
        this.orders = new ArrayList<>();
        this.status = SessionStatus.PENDING;
    }

    /**
     * Starts the backtesting session.
     *
     * <p>This method processes the candlestick data points and generates a list of orders based on
     * the strategy's decisions. It updates the session status accordingly and handles any
     * exceptions that may occur during the process.
     */
    public void startSession() {
        if (this.status != SessionStatus.PENDING) {
            return;
        }
        this.status = SessionStatus.STARTED;
        try {
            for (CandleDTO candle : data) {
                strategy.push(candle);
                Optional<OrderDTO> order = strategy.getStatus();
                order.ifPresent(orders::add);
                updateOrders(candle);
            }
        } catch (Exception e) {
            System.out.println("BackTestSession.start() - Exception: " + e.getMessage());
            this.status = SessionStatus.FAILED;
            return;
        }
        this.status = SessionStatus.COMPLETED;
    }

    /**
     * Updates the status of all orders based on the current candlestick data.
     *
     * @param candle the current candlestick data point
     */
    private void updateOrders(final CandleDTO candle) {
        if (orders.isEmpty()) {
            return;
        }
        for (int i = 0; i < orders.size(); i++) {
            OrderDTO order = orders.get(i);
            // check if order can be opened
            if (canBeOpened(order, candle)) {
                order =
                        new OrderDTO(
                                order.SL(),
                                order.TP(),
                                order.entry(),
                                order.closePrice(),
                                order.direction(),
                                candle.time(),
                                order.closeTime(),
                                OrderStatus.ACTIVE);
                // update order
                orders.set(i, order);
            }

            // check if order can be closed
            if (canBeClosed(order, candle)) {
                OrderStatus closeStatus = determineCloseStatus(order, candle);
                order =
                        new OrderDTO(
                                order.SL(),
                                order.TP(),
                                order.entry(),
                                order.closePrice(),
                                order.direction(),
                                candle.time(),
                                order.closeTime(),
                                closeStatus);
                // update order
                orders.set(i, order);
            }
        }
    }

    /**
     * Retrieves the current status of the backtesting session.
     *
     * @return the current session status, represented as a {@link SessionStatus} enum value
     */
    public SessionStatus getStatus() {
        return status;
    }

    /**
     * Retrieves the list of orders generated during the backtesting session.
     *
     * @return the list of orders, represented as a {@link List<OrderDTO>}
     */
    public List<OrderDTO> getOrders() {
        return orders;
    }

    /**
     * Determines if an order can be opened based on the current candlestick data. Should only be
     * called when the order is in the PENDING state.
     *
     * @param order the order to check
     * @param candle the current candlestick data point
     * @return true if the order can be opened, false otherwise
     */
    private boolean canBeOpened(final OrderDTO order, final CandleDTO candle) {
        if (order.status() != OrderStatus.PENDING) {
            return false;
        }
        // Determine if the entry price is hit
        return order.entry() <= candle.high() && order.entry() >= candle.low();
    }

    /**
     * Determines if an order can be closed based on the current candlestick data. Should only be
     * called when the order is in the ACTIVE or PENDING state.
     *
     * @param order the order to check
     * @param candle the current candlestick data point
     * @return true if the order can be closed, false otherwise
     */
    private boolean canBeClosed(final OrderDTO order, final CandleDTO candle) {
        if (order.status() != OrderStatus.ACTIVE && order.status() != OrderStatus.PENDING) {
            return false;
        }
        boolean isSLHit = order.SL() <= candle.high() && order.SL() >= candle.low();
        boolean isTPHit = order.TP() <= candle.high() && order.TP() >= candle.low();
        return isSLHit || isTPHit || candle.time().equals(LAST_CANDLE_CLOSE_TIME);
    }

    /**
     * Determines the close status of an order based on the current candlestick data. Should only be
     * called when the order is in the ACTIVE or PENDING state.
     *
     * @param order the order to check
     * @param candle the current candlestick data point
     * @return the close status of the order, represented as a {@link OrderStatus}
     */
    private OrderStatus determineCloseStatus(final OrderDTO order, final CandleDTO candle) {
        if (order.status() != OrderStatus.ACTIVE && order.status() != OrderStatus.PENDING) {
            return order.status();
        }
        boolean isSLHit = order.SL() <= candle.high() && order.SL() >= candle.low();
        boolean isTPHit = order.TP() <= candle.high() && order.TP() >= candle.low();
        if (isSLHit && isTPHit) {
            return OrderStatus.CLOSED_UNKNOWN;
        }
        if (isSLHit) {
            return OrderStatus.CLOSED_SL_HIT;
        }
        if (isTPHit) {
            return OrderStatus.CLOSED_TP_HIT;
        }

        return order.status() == OrderStatus.ACTIVE
                ? OrderStatus.CLOSED_MANUAL
                : OrderStatus.CLOSED_CANCELED;
    }
}
