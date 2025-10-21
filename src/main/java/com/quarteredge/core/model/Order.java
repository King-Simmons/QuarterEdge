package com.quarteredge.core.model;

public record Order(
        double SL,
        double TP,
        double entry,
        String startTime,
        String closeTime,
        OrderStatus status) {}
