package com.quarteredge.core.component;

import static com.quarteredge.core.util.Constants.LAST_CANDLE_CLOSE_TIME;
import static com.quarteredge.util.CommonUtils.createDefaultCandleList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.Direction;
import com.quarteredge.core.model.OrderDTO;
import com.quarteredge.core.model.OrderStatus;
import com.quarteredge.core.model.SessionStatus;
import com.quarteredge.core.strategy.Strategy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

public class BacktestSessionTest {
    /** A mocked strategy instance for testing. */
    private final Strategy mockedStrategy = mock();

    /** A mocked backtest session instance for testing. */
    private BacktestSession mockedBacktestSession;

    /** A list of candles for testing. */
    private final List<CandleDTO> data = createDefaultCandleList();

    @Test
    @DisplayName("Backtest should start with PENDING status")
    void testBacktestSessionPending() {
        mockedBacktestSession = new BacktestSession(mockedStrategy, data);
        assertEquals(SessionStatus.PENDING, mockedBacktestSession.getStatus());
    }

    @Test
    @DisplayName("startSession() should complete successfully")
    void testBacktestSessionSuccess() {
        mockedBacktestSession = new BacktestSession(mockedStrategy, data);
        mockedBacktestSession.startSession();
        assertEquals(SessionStatus.COMPLETED, mockedBacktestSession.getStatus());
    }

    @Test
    @DisplayName("startSession() should return immediately if not PENDING")
    void testBacktestSessionNotPending() {
        mockedBacktestSession = new BacktestSession(mockedStrategy, data);
        for (SessionStatus status : SessionStatus.values()) {
            if (status == SessionStatus.PENDING) {
                continue;
            }
            setStatus(status);
            mockedBacktestSession.startSession();
            assertEquals(status, mockedBacktestSession.getStatus());
        }
    }

    @Test
    @DisplayName("startSession() should return FAILED for any exception")
    void testBackestSessionFailed() {
        mockedBacktestSession = new BacktestSession(mockedStrategy, data);
        when(mockedStrategy.getStatus()).thenThrow(new RuntimeException());
        mockedBacktestSession.startSession();
        assertEquals(SessionStatus.FAILED, mockedBacktestSession.getStatus());
    }

    @Test
    @DisplayName(
            "startSession() should not check for orders if candle is at session start time"
                    + " (18:00:00)")
    void testBacktestSessionNoOrdersAtSessionStartTime() {
        var list = new ArrayList<CandleDTO>();
        list.add(new CandleDTO("", "18:00:00", 2, 2, 2, 3, 2));
        mockedBacktestSession = new BacktestSession(mockedStrategy, list);
        mockedBacktestSession.startSession();
        verify(mockedStrategy, times(1)).push(any());
        verify(mockedStrategy, never()).getStatus();
    }

    @Test
    @DisplayName("updateOrders() should not process empty orders list")
    void testUpdateOrdersWithEmptyOrders() {
        mockedBacktestSession = new BacktestSession(mockedStrategy, new ArrayList<>());
        mockedBacktestSession.startSession();
        assertEquals(SessionStatus.COMPLETED, mockedBacktestSession.getStatus());
    }

    @Test
    @DisplayName(
            "updateOrders() should update order status to CLOSED_TP_HIT when take profit is hit")
    void testUpdateOrdersClosesWithTpHit() {
        mockedBacktestSession = new BacktestSession(mockedStrategy, data);
        mockedBacktestSession
                .getOrders()
                .add(
                        new OrderDTO(
                                1, 2, 1.5, -1, Direction.BUY, "07:30:00", "", OrderStatus.ACTIVE));
        mockedBacktestSession.startSession();
        OrderDTO updatedOrder = mockedBacktestSession.getOrders().getFirst();
        assertEquals(OrderStatus.CLOSED_TP_HIT, updatedOrder.status());
        assertEquals(2, updatedOrder.closePrice());
        assertEquals("09:30:00", updatedOrder.closeTime());
    }

    @Test
    @DisplayName("updateOrders() should update order status to CLOSED_SL_HIT when stop loss is hit")
    void testUpdateOrdersClosesWithSlHit() {
        mockedBacktestSession = new BacktestSession(mockedStrategy, data);
        mockedBacktestSession
                .getOrders()
                .add(
                        new OrderDTO(
                                2, 1, 1.5, -1, Direction.SELL, "07:30:00", "", OrderStatus.ACTIVE));
        mockedBacktestSession.startSession();
        OrderDTO updatedOrder = mockedBacktestSession.getOrders().getFirst();
        assertEquals(OrderStatus.CLOSED_SL_HIT, updatedOrder.status());
        assertEquals(2, updatedOrder.closePrice());
        assertEquals("09:30:00", updatedOrder.closeTime());
    }

    @Test
    @DisplayName("updateOrders() should close all orders at session end")
    void testUpdateOrdersClosesAllAtSessionEnd() {
        data.add(new CandleDTO("", LAST_CANDLE_CLOSE_TIME, 5, 6, 7, 8, 100));
        mockedBacktestSession = new BacktestSession(mockedStrategy, data);
        mockedBacktestSession
                .getOrders()
                .add(
                        new OrderDTO(
                                110,
                                200,
                                150.5,
                                -1,
                                Direction.BUY,
                                "07:30:00",
                                "",
                                OrderStatus.ACTIVE));
        mockedBacktestSession
                .getOrders()
                .add(new OrderDTO(110, 200, 150.5, -1, Direction.BUY, "", "", OrderStatus.PENDING));

        mockedBacktestSession.startSession();
        OrderDTO updatedOrder = mockedBacktestSession.getOrders().get(0);
        OrderDTO updatedOrder2 = mockedBacktestSession.getOrders().get(1);
        assertEquals(OrderStatus.CLOSED_MANUAL, updatedOrder.status());
        assertEquals(OrderStatus.CLOSED_CANCELED, updatedOrder2.status());
        assertEquals(LAST_CANDLE_CLOSE_TIME, updatedOrder.closeTime());
    }

    @Test
    @DisplayName("updateOrders() should return CLOSED_UNKNOWN when both SL and TP are hit")
    void testDetermineCloseStatusWhenBothSlAndTpHit() {
        data.add(new CandleDTO("", "09:35:00", 5, 200, 3, 8, 100));
        mockedBacktestSession = new BacktestSession(mockedStrategy, data);
        mockedBacktestSession
                .getOrders()
                .add(
                        new OrderDTO(
                                101,
                                130,
                                100.5,
                                -1,
                                Direction.BUY,
                                "07:30:00",
                                "",
                                OrderStatus.ACTIVE));
        mockedBacktestSession.startSession();
        OrderDTO updatedOrder = mockedBacktestSession.getOrders().getFirst();
        assertEquals(OrderStatus.CLOSED_UNKNOWN, updatedOrder.status());
        assertEquals(-1, updatedOrder.closePrice());
        assertEquals("09:35:00", updatedOrder.closeTime());
    }

    private void setStatus(final SessionStatus status) {
        Field field =
                ReflectionUtils.findFields(
                                BacktestSession.class,
                                f -> f.getName().equals("status"),
                                ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                        .getFirst();
        field.setAccessible(true);
        try {
            field.set(mockedBacktestSession, status);
        } catch (Exception e) {
            IO.println(e.getMessage());
        }
    }
}
