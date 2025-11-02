package com.quarteredge.core.model;

public record CandleDTO(
        String date,
        String time,
        double open,
        double high,
        double low,
        double close,
        double volume) {}
