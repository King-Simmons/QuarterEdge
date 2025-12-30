package com.quarteredge.core.strategy;

import com.quarteredge.core.indicator.MovingAverageIndicator;
import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.Direction;
import com.quarteredge.core.model.OrderDTO;
import com.quarteredge.core.model.OrderStatsDTO;
import com.quarteredge.core.model.OrderStatus;
import java.util.Optional;

/**
 * Moving Average Crossover Strategy implementation. (Example)
 *
 * <p>This sample strategy uses two Moving Averages to determine when to enter and exit trades. It
 * is based on the crossover of the faster moving average over the slower moving average, indicating
 * a potential trend change.
 *
 * @author King Simmons
 * @version 1.0
 * @since 1.0
 * @see MovingAverageIndicator
 * @see OrderDTO
 */
public class MovingAverageCrossoverStrategy implements Strategy {
    /** Fast EMA indicator used to determine when to enter and exit trades. */
    private final MovingAverageIndicator fastSma;

    /** Slow EMA indicator used to determine when to enter and exit trades. */
    private final MovingAverageIndicator slowSma;

    /**
     * A flag indicating whether the current market trend is considered bullish. This variable is
     * determined based on the comparison between fast and slow EMA values in the context of the EMA
     * crossover strategy.
     *
     * <ul>
     *   <li><code>true</code>: The market is considered bullish (fast EMA is above slow EMA).
     *   <li><code>false</code>: The market is not bullish (fast EMA is below or equal to slow EMA).
     * </ul>
     *
     * This flag is used internally to make trading decisions within the strategy.
     */
    private boolean isBullish;

    /** Holds the current candlestick data being processed by the EMA Crossover Strategy. */
    private CandleDTO currentCandle;

    /**
     * The increment value used for price calculations to determine potential entry points,
     * stop-loss levels, or take-profit thresholds within the EMA crossover strategy. It is supposed
     * to represent the smallest price increment available for the trading instrument.
     */
    private final double increment;

    /**
     * Constructs an EMA Crossover Strategy with the specified parameters.
     *
     * @param fastPeriod the period for the fast EMA
     * @param slowPeriod the period for the slow EMA
     * @param increment the increment value for price calculations
     */
    public MovingAverageCrossoverStrategy(
            final int fastPeriod, final int slowPeriod, final double increment) {
        this.fastSma = new MovingAverageIndicator(fastPeriod);
        this.slowSma = new MovingAverageIndicator(slowPeriod);
        this.isBullish = false;
        this.currentCandle = null;
        this.increment = increment;
    }

    /**
     * Adds a new candlestick data point to the strategy.
     *
     * @param data the {@link CandleDTO} object containing OHLCV data to be added
     */
    @Override
    public void push(final CandleDTO data) {
        isBullish = fastSma.get().doubleValue() > slowSma.get().doubleValue();
        fastSma.add(data);
        slowSma.add(data);

        this.currentCandle = data;
    }

    /**
     * Returns the current status of the strategy.
     *
     * @return an {@link Optional} containing the order if a trade should be placed, or an empty
     *     optional if no trade should be placed
     */
    @Override
    public Optional<OrderDTO> getStatus() {
        double fast = fastSma.get().doubleValue();
        double slow = slowSma.get().doubleValue();
        if (fast == -1 || slow == -1 || currentCandle == null) {
            System.out.println("Insufficient data for EMA calculation");
            return Optional.empty();
        }

        return shouldCreateOrder(fast, slow)
                ? Optional.of(createOrder(currentCandle.close()))
                : Optional.empty();
    }

    /**
     * Determines whether to create an order based on the EMA values.
     *
     * @param fast the fast EMA value
     * @param slow the slow EMA value
     * @return true if an order should be created, false otherwise
     */
    private boolean shouldCreateOrder(final double fast, final double slow) {
        if (isBullish && fast < slow) {
            isBullish = false;
            return true;
        } else if (!isBullish && fast > slow) {
            isBullish = true;
            return true;
        }
        return false;
    }

    /**
     * Creates an order based on the entry price.
     *
     * @param entryPrice the entry price for the order
     * @return the created order
     */
    private OrderDTO createOrder(final double entryPrice) {
        Direction direction;
        double takeProfit;
        double stopLoss;
        final double stopLossValue = 10;
        final double takeProfitValue = 15;
        double closePrice = -1;

        if (isBullish) {
            direction = Direction.BUY;
            takeProfit = entryPrice + (takeProfitValue * increment);
            stopLoss = entryPrice - (stopLossValue * increment);
        } else {
            direction = Direction.SELL;
            takeProfit = entryPrice - (takeProfitValue * increment);
            stopLoss = entryPrice + (stopLossValue * increment);
        }

        return new OrderDTO(
                stopLoss,
                takeProfit,
                entryPrice,
                closePrice,
                direction,
                currentCandle.time(),
                null,
                OrderStatus.ACTIVE,
                new OrderStatsDTO(entryPrice, entryPrice));
    }
}
