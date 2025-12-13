package com.quarteredge.core.indicator;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.util.FifoQueue;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of the Average True Range (ATR) technical indicator.
 *
 * <p>The ATR is a volatility indicator that measures market volatility by decomposing the entire
 * range of an asset price for a given period. It was developed by J. Welles Wilder Jr. and is
 * commonly used to determine stop-loss levels, position sizing, and market volatility.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Measures market volatility over a specified period
 *   <li>Calculates the True Range (TR) for each period
 *   <li>Maintains a rolling average of the True Range values
 *   <li>Provides a single value representing the average volatility
 * </ul>
 *
 * @author King Simmons
 * @version 1.0
 * @since 0.2.0
 * @see CandleDTO
 * @see Indicator
 */
public class AverageTrueRangeIndicator implements Indicator {
    /** The period used for calculating the ATR. */
    private final int length;

    /** Queue to store the most recent True Range values. */
    private final FifoQueue<BigDecimal> dataQueue;

    /** The current ATR value. */
    private BigDecimal atr;

    /**
     * Constructs a new ATR indicator with the specified period length.
     *
     * @param length the number of periods to use for the ATR calculation
     * @throws IllegalArgumentException if the length is less than or equal to 0
     */
    public AverageTrueRangeIndicator(final int length) {
        this.length = length;
        this.dataQueue = new FifoQueue<>(length);
        this.atr = new BigDecimal(-1);
    }

    /**
     * Adds a new candlestick data point to the ATR calculation.
     *
     * <p>This method calculates the True Range (TR) for the given candlestick and updates the ATR
     * value. The TR is the greatest of the following:
     *
     * <ul>
     *   <li>Current High - Current Low
     *   <li>Current High - Previous Close
     *   <li>Current Low - Previous Close
     * </ul>
     *
     * @param data the candlestick data point to add
     * @throws NullPointerException if data is null
     */
    @Override
    public void add(final CandleDTO data) {
        calculate(
                new BigDecimal(data.high()),
                new BigDecimal(data.low()),
                new BigDecimal(data.close()));
    }

    /**
     * Returns the current ATR value.
     *
     * @return the current ATR value, or -1 if not enough data points have been added
     */
    @Override
    public BigDecimal get() {
        return atr;
    }

    /**
     * Calculates the ATR based on the current candlestick data.
     *
     * <p>This method handles both the initial ATR calculation (simple average of TR) and later
     * calculations (RMA).
     *
     * @param high the high price of the current period
     * @param low the low price of the current period
     * @param close the close price of the current period
     */
    private void calculate(final BigDecimal high, final BigDecimal low, final BigDecimal close) {
        // Calculate True Range
        BigDecimal trueRange = high.subtract(low).setScale(2, RoundingMode.HALF_DOWN);
        trueRange = trueRange.max(high.subtract(close).setScale(2, RoundingMode.HALF_DOWN));
        trueRange = trueRange.max(low.subtract(close).setScale(2, RoundingMode.HALF_DOWN));
        // IO.println(trueRange);
        if (dataQueue.size() < length) {
            dataQueue.add(trueRange);

            // Calculate initial ATR as the simple average of TR values once targeted size is
            // reached
            if (dataQueue.size() == length) {
                BigDecimal total = new BigDecimal(0);
                for (BigDecimal value : dataQueue.getQueue()) {
                    total = total.add(value);
                }
                atr = total.divide(new BigDecimal(length), 2, RoundingMode.HALF_UP);
            }
        } else {
            dataQueue.add(trueRange);
            // Update ATR using the standard formula
            atr =
                    atr.multiply(new BigDecimal(length - 1))
                            .add(trueRange)
                            .divide(new BigDecimal(length), RoundingMode.HALF_DOWN);
        }
    }
}
