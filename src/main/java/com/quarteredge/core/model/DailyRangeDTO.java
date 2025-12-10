package com.quarteredge.core.model;

public record DailyRangeDTO(
        boolean isActive,
        double drHigh,
        double drLow,
        double idrHigh,
        double idrLow) {}
