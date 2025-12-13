package com.quarteredge.core.indicator;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.util.FifoQueue;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Simple Moving Average (SMA) indicator implementation.
 *
 * <p>This indicator calculates a simple moving average over a specified period length by
 * maintaining a rolling window of the most recent closing prices and computing their average. The
 * indicator uses {@link BigDecimal} for precise financial calculations and {@link
 * RoundingMode#HALF_UP} for rounding.
 *
 * @author King Simmons
 * @version 1.0
 * @since 0.1.0
 * @see Indicator
 * @see CandleDTO
 */
public class MovingAverageIndicator implements Indicator {
    /**
     * Queue that maintains the rolling window of price data points. The size of this queue is
     * limited to the specified length.
     */
    private final FifoQueue<BigDecimal> dataQueue;

    /**
     * The current calculated MA value. Initialized to -1 to indicate insufficient data for
     * calculation.
     */
    private BigDecimal val;

    /** The period length (number of data points) used for the moving average calculation. */
    private final int length;

    /**
     * The running sum of all values currently in the data queue. Used to efficiently calculate the
     * average without iterating through the queue.
     */
    private BigDecimal total;

    /**
     * Constructs a new MA indicator with the specified period length.
     *
     * @param length the number of periods to use for the moving average calculation
     */
    public MovingAverageIndicator(final int length) {
        this.dataQueue = new FifoQueue<>(length);
        this.val = new BigDecimal(-1);
        this.length = length;
        this.total = new BigDecimal(0);
    }

    /**
     * Adds new candlestick data to the indicator and updates the moving average value.
     *
     * <p>Extracts the closing price from the {@link CandleDTO} object and recalculates the moving
     * average.
     *
     * @param data the {@link CandleDTO} object containing OHLCV price information
     */
    @Override
    public void add(final CandleDTO data) {
        calculate(data.close());
    }

    /**
     * Returns the current calculated moving average value.
     *
     * @return the most recent moving average value as a {@link BigDecimal}, or -1 if insufficient
     *     data points are available (less than the specified period length)
     */
    @Override
    public BigDecimal get() {
        if (length > dataQueue.size()) {
            return new BigDecimal(-1);
        }
        return val;
    }

    /**
     * Calculates the moving average based on the new input value.
     *
     * <p>Maintains a rolling window of data points. When the window is full, removes the oldest
     * value before adding the new one. The average is calculated by dividing the total sum by the
     * period length, rounded using {@link RoundingMode#HALF_UP}.
     *
     * @param input the new price data point to add to the calculation
     */
    private void calculate(final double input) {
        var bd = new BigDecimal(input);
        if (dataQueue.size() == length && length > 0) {
            total = total.subtract(dataQueue.poll());
        }
        total = total.add(bd);
        dataQueue.add(bd);
        val = total.divide(new BigDecimal(length), 2, RoundingMode.HALF_UP);
    }
}
