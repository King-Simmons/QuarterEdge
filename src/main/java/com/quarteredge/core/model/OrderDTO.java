package com.quarteredge.core.model;

public record OrderDTO(
        double SL,
        double TP,
        double entry,
        double closePrice,
        Direction direction,
        String startTime,
        String closeTime,
        OrderStatus status) {}
