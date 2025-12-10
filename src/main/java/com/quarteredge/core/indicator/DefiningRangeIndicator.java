package com.quarteredge.core.indicator;

import static com.quarteredge.core.util.Constants.RDR_SESSION_END_TIME;
import static com.quarteredge.core.util.Constants.RDR_SESSION_START_TIME;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.DefiningRangeDTO;

/**
 * An indicator that tracks the defining range and implied defining range (IDR) during the Regular
 * Trading Hours (RTH) session.
 *
 * <p>This indicator calculates two main ranges during the RTH session:
 *
 * <ul>
 *   <li>Defining Range (DR): The high and low prices during the entire RTH session
 *   <li>Implied Defining Range (IDR): The high and low prices based on closing prices during the
 *       RTH session
 * </ul>
 *
 * <p>The RTH session is defined by {@code RDR_SESSION_START_TIME} (09:29:59) and {@code
 * RDR_SESSION_END_TIME} (10:30:00) from the Constants class.
 *
 * @author King Simmons
 * @version 1.0
 * @since v0.2.0
 * @see Indicator
 * @see DefiningRangeDTO
 */
public class DefiningRangeIndicator implements Indicator {
    /** Flag indicating if the daily range calculation is complete for the current session. */
    private boolean isActive;

    /** The highest price observed during the RTH session. */
    private double drHigh;

    /** The lowest price observed during the RTH session. */
    private double drLow;

    /** The highest closing price observed during the RTH session. */
    private double idrHigh;

    /** The lowest closing price observed during the RTH session. */
    private double idrLow;

    /** The DTO containing the calculated defining range values. */
    private DefiningRangeDTO definingRangeDTO;

    /**
     * Constructs a new DefiningRangeIndicator with initial values. The indicator starts in an
     * inactive state with all range values set to -1.
     */
    public DefiningRangeIndicator() {
        this.isActive = false;
        this.definingRangeDTO = new DefiningRangeDTO(false, -1, -1, -1, -1);
    }

    /**
     * Processes a new candlestick data point to update the daily range calculations.
     *
     * <p>This method updates the following metrics during the RTH session (09:29:59 to 10:30:00):
     *
     * <ul>
     *   <li>Updates the DR high and low based on the candle's high and low prices
     *   <li>Updates the IDR high and low based on the candle's closing price
     * </ul>
     *
     * <p>Once the session end time is reached, the indicator becomes active, and no further updates
     * to the ranges are made for that session.
     *
     * @param data the candlestick data point to process
     * @throws NullPointerException if the data parameter is null
     */
    public void add(final CandleDTO data) {
        if (data.time().isBefore(RDR_SESSION_START_TIME)) {
            this.isActive = false;
        }
        if (isActive) {
            return;
        }
        if (data.time().isAfter(RDR_SESSION_START_TIME)
                && data.time().isBefore(RDR_SESSION_END_TIME)) {
            drHigh = Math.max(drHigh, data.high());
            drLow = Math.min(drLow, data.low());
            idrHigh = Math.max(idrHigh, data.close());
            idrLow = Math.min(idrLow, data.close());
        }

        if (data.time().equals(RDR_SESSION_END_TIME) || data.time().isAfter(RDR_SESSION_END_TIME)) {
            isActive = true;
            definingRangeDTO = new DefiningRangeDTO(true, drHigh, drLow, idrHigh, idrLow);
        }
    }

    /**
     * Returns the current state of the defining range calculations.
     *
     * @return a {@link DefiningRangeDTO} containing the current DR and IDR values. If the session
     *     is not yet complete, the DTO will have isActive set to false.
     */
    public DefiningRangeDTO get() {
        return definingRangeDTO;
    }
}
