package com.quarteredge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.quarteredge.core.model.Direction;
import com.quarteredge.core.model.OrderDTO;
import com.quarteredge.core.model.OrderStatsDTO;
import com.quarteredge.core.model.OrderStatus;
import com.quarteredge.core.service.PerformanceService;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PerformanceServiceTest {
    /** The performance service to be tested. */
    private PerformanceService performanceService;

    @DisplayName("calculatePerformance() should not do run calculations if no orders in list")
    @Test
    void calculatePerformanceNoOrders() {
        performanceService = new PerformanceService(List.of());
        var result = performanceService.calculatePerformance();
        assertEquals("No orders to calculate performance metrics.", result);
    }

    @DisplayName("calculatePerformance() should run calculations if orders in list")
    @Test
    void calculatePerformanceWithOrders() {
        performanceService =
                new PerformanceService(
                        List.of(
                                new OrderDTO(
                                        1,
                                        2,
                                        1.5,
                                        2,
                                        Direction.BUY,
                                        LocalTime.of(12, 0, 0),
                                        LocalTime.of(12, 0, 0),
                                        OrderStatus.CLOSED_TP_HIT,
                                        new OrderStatsDTO(0, 0))));
        performanceService.calculatePerformance();
        var res = performanceService.calculatePerformance();
        assertNotEquals("No orders to calculate performance metrics.", res);
    }
}
