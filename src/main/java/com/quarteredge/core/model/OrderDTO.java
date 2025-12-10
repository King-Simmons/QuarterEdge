package com.quarteredge.core.model;

import java.time.LocalTime;

public record OrderDTO(
        double SL,
        double TP,
        double entry,
        double closePrice,
        Direction direction,
        LocalTime startTime,
        LocalTime closeTime,
        OrderStatus status) {}
