package com.quarteredge.core.strategy;

import static com.quarteredge.core.util.Constants.CL_TICK_INCREMENT;
import static com.quarteredge.core.util.Constants.LAST_CANDLE_CLOSE_TIME;

import com.quarteredge.core.indicator.AverageTrueRangeIndicator;
import com.quarteredge.core.indicator.DefiningRangeIndicator;
import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.Direction;
import com.quarteredge.core.model.OrderDTO;
import com.quarteredge.core.model.OrderStatus;
import com.quarteredge.core.util.CommonMethods;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The QuarterEdgeStrategy class implements the Strategy interface and is used to generate trading
 * signals based on the Average True Range (ATR) and Defining Range (DR) indicators.
 *
 * <p>This strategy generates a trading signal when the ATR is greater than 0 and the Defining Range
 * has been formed. The trading signal is generated based on the direction of the Defining Range.
 *
 * @author King Simmons
 * @version 1.0
 * @since v0.2.0
 * @see Strategy
 * @see AverageTrueRangeIndicator
 * @see DefiningRangeIndicator
 */
public class QuarterEdgeStrategy implements Strategy {
    /** Average True Range indicator. */
    private final AverageTrueRangeIndicator atrIndicator;

    /** Defining Range indicator. */
    private final DefiningRangeIndicator drIndicator;

    /** Flag indicating if an order has been created. */
    private boolean isOrderCreated;

    /**
     * Constructs a new QuarterEdgeStrategy with the specified ATR period.
     *
     * @param atrPeriod the period for the ATR indicator
     */
    public QuarterEdgeStrategy(final int atrPeriod) {
        this.atrIndicator = new AverageTrueRangeIndicator(atrPeriod);
        this.drIndicator = new DefiningRangeIndicator();
        this.isOrderCreated = false;
    }

    /**
     * Processes a new candlestick data point to update the indicators.
     *
     * @param data the candlestick data point to process
     */
    public void push(final CandleDTO data) {
        atrIndicator.add(data);
        drIndicator.add(data);
        if (data.time().isAfter(LAST_CANDLE_CLOSE_TIME)) {
            isOrderCreated = false;
        }
    }

    /**
     * Returns the current status of the strategy.
     *
     * @return the current status of the strategy
     */
    public Optional<OrderDTO> getStatus() {
        if (isOrderCreated) {
            return Optional.empty();
        }
        if (!drIndicator.get() || !drIndicator.hasBreakoutOccurred()) {
            return Optional.empty();
        }
        if (atrIndicator.get().doubleValue() < 0) {
            return Optional.empty();
        }

        Optional<OrderDTO> order = Optional.ofNullable(createOrder());
        if (order.isPresent()) isOrderCreated = true;
        return order;
    }

    /**
     * Creates a new order based on the current state of the strategy.
     *
     * @return the new order
     */
    private OrderDTO createOrder() {
        double atr = atrIndicator.get().doubleValue();
        double high = drIndicator.getDrHigh();
        double low = drIndicator.getDrLow();
        Direction direction = drIndicator.getDirection();
        List<Double> qTLevels = CommonMethods.getQuarterLevelsInRange(low, high, CL_TICK_INCREMENT);
        if (qTLevels.isEmpty()) {
            return null;
        }
        BigDecimal entryPrice =
                direction == Direction.BUY
                        ? new BigDecimal(qTLevels.getLast())
                        : new BigDecimal(qTLevels.getFirst());
        BigDecimal stopLoss =
                direction == Direction.BUY
                        ? entryPrice.subtract(new BigDecimal(atr))
                        : entryPrice.add(new BigDecimal(atr));
        BigDecimal takeProfit =
                direction == Direction.BUY
                        ? entryPrice.add(new BigDecimal(atr))
                        : entryPrice.subtract(new BigDecimal(atr));
        return new OrderDTO(
                stopLoss.doubleValue(),
                takeProfit.doubleValue(),
                entryPrice.doubleValue(),
                -1,
                direction,
                null,
                null,
                OrderStatus.PENDING);
    }
}
