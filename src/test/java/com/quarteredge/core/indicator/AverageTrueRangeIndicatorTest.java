package com.quarteredge.core.indicator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quarteredge.core.model.CandleDTO;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link AverageTrueRangeIndicator} class.
 *
 * <p>This test class verifies the functionality of the Average True Range (ATR) indicator
 * implementation. The ATR is a volatility indicator that measures market volatility by decomposing
 * the entire range of an asset price for that period.
 *
 * <p>The tests cover the following scenarios:
 *
 * <ul>
 *   <li>Verifying the indicator returns -1 when insufficient data is available
 *   <li>Validating the ATR calculation with a standard 14-period window
 *   <li>Ensuring proper handling of the rolling window when new data is added
 * </ul>
 *
 * @author King Simmons
 * @version 1.0
 * @since 0.2.0
 * @see AverageTrueRangeIndicator
 */
public class AverageTrueRangeIndicatorTest {
    /** List of test data containing candlestick information. */
    private List<CandleDTO> testdata;

    /** The ATR indicator instance under test. */
    private AverageTrueRangeIndicator indicator;

    /** The standard look back period for ATR calculation. */
    private static final int LENGTH = 14;

    /**
     * Initializes the test environment before each test method execution. Creates a new instance of
     * the ATR indicator with the standard look back period.
     */
    @BeforeEach
    void init() {
        testdata = generateTestData();
        indicator = new AverageTrueRangeIndicator(LENGTH);
    }

    /**
     * Tests that the ATR indicator returns -1 when not enough data points are available. The ATR
     * requires at least one complete period of data before it can be calculated.
     */
    @Test
    @DisplayName("get() should return -1 if not enough data")
    public void testAverageTrueRangeIndicatorInvalid() {
        assertEquals(new BigDecimal(-1), indicator.get());
    }

    /**
     * Tests the ATR calculation with a full set of test data. Verifies that the ATR is calculated
     * correctly after adding the required number of data points.
     */
    @Test
    @DisplayName("get() should return atr")
    public void testAverageTrueRangeIndicatorValid() {
        for (CandleDTO data : testdata) {
            indicator.add(data);
        }
        assertEquals(1.19, indicator.get().doubleValue());
        indicator.add(new CandleDTO(null, null, 24.89, 25.86, 24.66, 25.20, 1000));
        assertEquals(1.19, indicator.get().doubleValue());
    }

    /**
     * Generates test data for ATR calculation. The data represents a sequence of candlesticks with
     * high, low, and close prices.
     *
     * @return a list of {@link CandleDTO} objects containing test data
     */
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
