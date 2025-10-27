package com.quarteredge.core.indicator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a technical indicator that processes data and provides calculated values.
 * <p>
 * This interface defines the contract for technical indicators used in trading strategies.
 * Implementations should call a private calculate method within the {@link #add(List)} method to
 * update the indicator value, which can then be retrieved using {@link #get()}.
 * </p>
 *
 * @author QuarterEdge
 * @version 1.0
 * @since 1.0
 */
public interface Indicator {
    /**
     * Adds new data to the indicator and updates its calculated value.
     * <p>
     * This method should internally call a private calculate() method to update
     * the indicator value after processing the new data point.
     * </p>
     *
     * @param data the list of string data representing a data point to be added to the indicator
     */
    void add(List<String> data);

    /**
     * Returns the current calculated value of the indicator.
     *
     * @return the most recent calculated indicator value as a {@link BigDecimal}
     */
    BigDecimal get();
}
