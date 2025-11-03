package com.quarteredge.core.indicator;

import com.quarteredge.core.model.CandleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link EmaIndicator}.
 * <p>
 * These tests validate the rolling-window simple moving average behavior currently
 * implemented in {@link EmaIndicator} (despite the EMA name), including:
 * </p>
 * <ul>
 *   <li>Returning -1 when insufficient data points are available</li>
 *   <li>Computing a rounded average over the configured window length</li>
 *   <li>Using only the most recent values when the window advances</li>
 * </ul>
 *
 * @see EmaIndicator
 * @see CandleDTO
 */
public class EmaIndicatorTest {
    /**
     * The {@link EmaIndicator} instance under test, configured with a period length of 3.
     */
    private EmaIndicator ema;

    /**
     * The period length for the {@link EmaIndicator} under test.
     */
    private static final int PERIOD = 3;

    /**
     * Initializes a new {@link EmaIndicator} with a period length of 3
     * before each test.
     */
    @BeforeEach
    void init() {
        ema = new EmaIndicator(PERIOD);
    }

    /**
     * Verifies that {@link EmaIndicator#get()} returns {@code -1} when fewer than
     * the required number of data points have been added.
     */
    @Test
    @DisplayName("get() should return -1 when not enough data")
    void testGetInvalid() {
        assertEquals(new BigDecimal(-1), ema.get());
    }

    /**
     * Verifies the indicator returns the arithmetic average once the window is filled.
     * Adds three equal closes and expects an average equal to that value.
     */
    @Test
    @DisplayName("calculate() should return average")
    void testCalculateValid() {
        ema.add(createDefaultCandleWithClose(2));
        ema.add(createDefaultCandleWithClose(2));
        ema.add(createDefaultCandleWithClose(2));

        assertEquals(0, new BigDecimal("2").compareTo(ema.get()));
    }

    /**
     * Verifies the rolling-window behavior and rounding:
     * <ul>
     *   <li>With closes 75, 2, 2 with the average is 26.33 (HALF_UP rounding to 2 decimals).</li>
     *   <li>After adding another 2, the window becomes 2, 2, 2 and the average is 2.00.</li>
     * </ul>
     */
    @Test
    @DisplayName("calculate() should return average based on most recent data")
    void testCalculateValidMostRecent() {
        ema.add(createDefaultCandleWithClose(0));
        ema.add(createDefaultCandleWithClose(2));
        ema.add(createDefaultCandleWithClose(2));
        assertEquals(0, new BigDecimal("1.33").compareTo(ema.get()));
        ema.add(createDefaultCandleWithClose(2));

        assertEquals(0, new BigDecimal("2").compareTo(ema.get()));
    }

    /**
     * Creates a {@link CandleDTO} with the provided close price and placeholder values
     * for other fields to facilitate testing.
     *
     * @param close the close price to embed in the test candle
     * @return a {@link CandleDTO} instance with the specified close
     */
    private CandleDTO createDefaultCandleWithClose(final double close) {
        return new CandleDTO("", "", 2, 2, 2, close, 2);
    }
}
