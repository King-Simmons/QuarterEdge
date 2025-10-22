package com.quarteredge.core.indicator;

/**
 * Represents a technical indicator that processes data and provides calculated values.
 * <p>
 * This interface defines the contract for technical indicators used in trading strategies.
 * Implementations should call {@link #calculate()} within the {@link #add()} method to
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
     * This method should internally call {@link #calculate()} to update
     * the indicator value after adding the new data point.
     * </p>
     */
    void add();

    /**
     * Returns the current calculated value of the indicator.
     *
     * @return the most recent calculated indicator value
     */
    double get();

    /**
     * Calculates the indicator value based on the current data.
     * <p>
     * This method is typically called internally by {@link #add()} to update
     * the indicator value whenever new data is added.
     * </p>
     */
    void calculate();
}
