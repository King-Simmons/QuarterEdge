package com.quarteredge.core.indicator;

import static com.quarteredge.util.CommonUtils.createDefaultCandleWithClose;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MovingAverageIndicator}.
 *
 * <p>These tests validate the rolling-window simple moving average behavior currently implemented
 * in {@link MovingAverageIndicator} (despite the EMA name), including:
 *
 * <ul>
 *   <li>Returning -1 when insufficient data points are available
 *   <li>Computing a rounded average over the configured window length
 *   <li>Using only the most recent values when the window advances
 * </ul>
 *
 * @see MovingAverageIndicator
 */
public class MovingAverageIndicatorTest {
    /**
     * The {@link MovingAverageIndicatorTest} instance under test, configured with a period length
     * of 3.
     */
    private MovingAverageIndicator ma;

    /** The period length for the {@link MovingAverageIndicator} under test. */
    private static final int PERIOD = 3;

    /**
     * Initializes a new {@link MovingAverageIndicator} with a period length of 3 before each test.
     */
    @BeforeEach
    void init() {
        ma = new MovingAverageIndicator(PERIOD);
    }

    /**
     * Verifies that {@link MovingAverageIndicator#get()} returns {@code -1} when fewer than the
     * required number of data points have been added.
     */
    @Test
    @DisplayName("get() should return -1 when not enough data")
    void testGetInvalid() {
        assertEquals(new BigDecimal(-1), ma.get());
    }

    /**
     * Verifies the indicator returns the arithmetic average once the window is filled. Adds three
     * equal closes and expects an average equal to that value.
     */
    @Test
    @DisplayName("calculate() should return average")
    void testCalculateValid() {
        ma.add(createDefaultCandleWithClose(2));
        ma.add(createDefaultCandleWithClose(2));
        ma.add(createDefaultCandleWithClose(2));

        assertEquals(0, new BigDecimal("2").compareTo(ma.get()));
    }

    /**
     * Verifies the rolling-window behavior and rounding:
     *
     * <ul>
     *   <li>With closes 75, 2, 2 with the average is 26.33 (HALF_UP rounding to 2 decimals).
     *   <li>After adding another 2, the window becomes 2, 2, 2 and the average is 2.00.
     * </ul>
     */
    @Test
    @DisplayName("calculate() should return average based on most recent data")
    void testCalculateValidMostRecent() {
        ma.add(createDefaultCandleWithClose(0));
        ma.add(createDefaultCandleWithClose(2));
        ma.add(createDefaultCandleWithClose(2));
        assertEquals(0, new BigDecimal("1.33").compareTo(ma.get()));
        ma.add(createDefaultCandleWithClose(2));

        assertEquals(0, new BigDecimal("2").compareTo(ma.get()));
    }
}
