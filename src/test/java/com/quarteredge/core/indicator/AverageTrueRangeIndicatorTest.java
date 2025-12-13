package com.quarteredge.core.indicator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quarteredge.core.model.CandleDTO;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AverageTrueRangeIndicatorTest {
    private List<CandleDTO> testdata;
    private AverageTrueRangeIndicator indicator;
    private final int LENGTH = 14;

    @BeforeEach
    void init() {
        testdata = generateTestData();
        indicator = new AverageTrueRangeIndicator(LENGTH);
    }

    @Test
    @DisplayName("get() should return -1 if not enough data")
    public void testAverageTrueRangeIndicatorInvalid() {
        assertEquals(new BigDecimal(-1), indicator.get());
    }

    @Test
    @DisplayName("get() should return atr")
    public void testAverageTrueRangeIndicatorValid() {
        for (CandleDTO data : testdata) {
            indicator.add(data);
        }
        assertEquals(1.19, indicator.get().doubleValue());
        indicator.add(new CandleDTO(null, null, 24.89, 25.86, 24.66, 25.20, 1000));
        assertEquals(1.15, indicator.get().doubleValue());
    }

    private List<CandleDTO> generateTestData() {
        return List.of(
                new CandleDTO(null, null, 21.51, 21.95, 20.22, 21.51, 1000),
                new CandleDTO(null, null, 21.51, 22.25, 21.10, 21.61, 1000),
                new CandleDTO(null, null, 21.61, 21.50, 20.34, 20.83, 1000),
                new CandleDTO(null, null, 20.83, 23.25, 22.13, 22.65, 1000),
                new CandleDTO(null, null, 22.65, 23.03, 21.87, 22.41, 1000),
                new CandleDTO(null, null, 22.41, 23.34, 22.18, 22.67, 1000),
                new CandleDTO(null, null, 22.67, 23.66, 22.57, 23.05, 1000),
                new CandleDTO(null, null, 23.05, 23.97, 22.80, 23.31, 1000),
                new CandleDTO(null, null, 23.31, 24.29, 23.15, 23.68, 1000),
                new CandleDTO(null, null, 23.68, 24.60, 23.45, 23.97, 1000),
                new CandleDTO(null, null, 23.97, 24.92, 23.76, 24.31, 1000),
                new CandleDTO(null, null, 24.31, 25.23, 24.09, 24.60, 1000),
                new CandleDTO(null, null, 24.60, 25.55, 24.39, 24.89, 1000),
                new CandleDTO(null, null, 24.89, 25.86, 24.69, 25.20, 1000));
    }
}
