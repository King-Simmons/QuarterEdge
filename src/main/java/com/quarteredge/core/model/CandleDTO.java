package com.quarteredge.core.model;

import java.time.LocalTime;

public record CandleDTO(
        String date,
        LocalTime time,
        double open,
        double high,
        double low,
        double close,
        double volume) {}
