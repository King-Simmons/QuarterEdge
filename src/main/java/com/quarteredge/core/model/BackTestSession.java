package com.quarteredge.core.model;

import static com.quarteredge.core.util.Constants.LAST_CANDLE_CLOSE_TIME;

import com.quarteredge.core.strategy.Strategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BackTestSession {
    private final List<CandleDTO> data;
    private final List<OrderDTO> orders;
    private final Strategy strategy;
    private SessionStatus status;

    public BackTestSession(Strategy strategy, List<CandleDTO> data) {
        this.strategy = strategy;
        this.data = data;
        this.orders = new ArrayList<>();
        this.status = SessionStatus.PENDING;
    }

    public void startSession() {
        if (this.status != SessionStatus.PENDING) return;
        this.status = SessionStatus.STARTED;
        try {
            for (CandleDTO candle : data) {
                strategy.push(candle);
                Optional<OrderDTO> order = strategy.getStatus();
                order.ifPresent(orders::add);
                updateOrders(orders, candle);
            }
        } catch (Exception e) {
            System.out.println("BackTestSession.start() - Exception: " + e.getMessage());
            this.status = SessionStatus.FAILED;
            return;
        }
        this.status = SessionStatus.COMPLETED;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    private boolean canBeOpened(OrderDTO order, CandleDTO candle) {
        if (order.status() != OrderStatus.PENDING) return false;
        // Determine if the entry price is hit
        return order.entry() <= candle.high() && order.entry() >= candle.low();
    }

    private boolean canBeClosed(OrderDTO order, CandleDTO candle) {
        if (order.status() != OrderStatus.ACTIVE && order.status() != OrderStatus.PENDING) {
            return false;
        }
        boolean isSLHit = order.SL() <= candle.high() && order.SL() >= candle.low();
        boolean isTPHit = order.TP() <= candle.high() && order.TP() >= candle.low();
        return isSLHit || isTPHit || candle.time().equals(LAST_CANDLE_CLOSE_TIME);
    }

    private OrderStatus determineCloseStatus(OrderDTO order, CandleDTO candle) {
        if (order.status() != OrderStatus.ACTIVE && order.status() != OrderStatus.PENDING) {
            return order.status();
        }
        boolean isSLHit = order.SL() <= candle.high() && order.SL() >= candle.low();
        boolean isTPHit = order.TP() <= candle.high() && order.TP() >= candle.low();
        if (isSLHit && isTPHit) return OrderStatus.CLOSED_UNKNOWN;
        if (isSLHit) return OrderStatus.CLOSED_SL_HIT;
        if (isTPHit) return OrderStatus.CLOSED_TP_HIT;

        return order.status() == OrderStatus.ACTIVE
                ? OrderStatus.CLOSED_MANUAL
                : OrderStatus.CLOSED_CANCELED;
    }

    private void updateOrders(List<OrderDTO> orders, CandleDTO candle) {
        if (orders.isEmpty()) return;
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
}
