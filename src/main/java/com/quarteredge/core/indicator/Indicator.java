package com.quarteredge.core.indicator;

import com.quarteredge.core.model.CandleDTO;
import java.math.BigDecimal;

/**
 * Represents a technical indicator that processes candlestick data and provides calculated values.
 * <p>
 * This interface defines the contract for technical indicators used in trading strategies.
 * Implementations should call a private calculate method within the {@link #add(CandleDTO)}
 * method to update the indicator value, which can then be retrieved using {@link #get()}.
 * </p>
 *
 * @author King Simmons
 * @version 1.0
 * @since 1.0
 * @see CandleDTO
 */
public interface Indicator {
    /**
     * Adds new candlestick data to the indicator and updates its calculated value.
     * <p>
     * This method should internally call a private calculate() method to update
     * the indicator value after processing the new candle data.
     * </p>
     *
     * @param data the {@link CandleDTO} object containing OHLCV data to be added to the indicator
     */
    void add(CandleDTO data);

    /**
     * Returns the current calculated value of the indicator.
     *
     * @return the most recent calculated indicator value as a {@link BigDecimal},
     *         or -1 if insufficient data is available for calculation
     */
    BigDecimal get();
}
