package com.quarteredge.core.strategy;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.OrderDTO;
import java.util.Optional;

/**
 * Strategy interface for determining whether to place a trade order or not. This interface
 * encapsulates the logic for making trading decisions based on indicator values.
 *
 * <p>Implementations of this interface should process indicator data and return an order if the
 * strategy determines that a trade should be placed. The order should contain the relevant details
 * for the trade, such as entry and exit points, stop loss, and take profit.
 *
 * @author King Simmons
 * @version 1.0
 * @since 1.0
 * @see com.quarteredge.core.indicator.Indicator
 * @see OrderDTO
 */
public interface Strategy {
    /**
     * Adds a new candle to the strategy for processing. This method should be called for each new
     * candlestick received from the market.
     *
     * @param data the new candlestick data point to process
     */
    void push(CandleDTO data);

    /**
     * Returns the current status of the strategy.
     *
     * @return an {@link Optional} containing the order if a trade should be placed, or an empty
     *     optional if no trade should be placed
     */
    Optional<OrderDTO> getStatus();
}
