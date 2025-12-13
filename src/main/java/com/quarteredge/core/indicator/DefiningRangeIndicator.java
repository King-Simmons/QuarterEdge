package com.quarteredge.core.indicator;

import static com.quarteredge.core.util.Constants.RDR_SESSION_END_TIME;
import static com.quarteredge.core.util.Constants.RDR_SESSION_START_TIME;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.DefiningRangeDTO;
import com.quarteredge.core.model.Direction;

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
    /** The highest price observed during the RTH session. */
    private double drHigh;

    /** The lowest price observed during the RTH session. */
    private double drLow;

    /** The highest closing price observed during the RTH session. */
    private double idrHigh;

    /** The lowest closing price observed during the RTH session. */
    private double idrLow;

    /** Flag indicating if a breakout has occurred. */
    private boolean breakoutHasOccurred;

    /** The DTO containing the calculated defining range values. */
    private DefiningRangeDTO definingRangeDTO;

    /** The direction of the defining range. */
    private Direction direction;

    /**
     * Constructs a new DefiningRangeIndicator with initial values. The indicator starts in an
     * inactive state with all range values set to -1.
     */
    public DefiningRangeIndicator() {
        definingRangeDTO = null;
        breakoutHasOccurred = false;
        direction = null;
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
            definingRangeDTO = null;
            breakoutHasOccurred = false;
            direction = null;
            drHigh = -1;
            drLow = Double.MAX_VALUE;
            idrHigh = -1;
            idrLow = Double.MAX_VALUE;
        }

        if (data.time().isAfter(RDR_SESSION_START_TIME)
                && data.time().isBefore(RDR_SESSION_END_TIME)) {
            drHigh = Math.max(drHigh, data.high());
            drLow = Math.min(drLow, data.low());
            idrHigh = Math.max(idrHigh, data.close());
            idrLow = Math.min(idrLow, data.close());
        }

        if (data.time().equals(RDR_SESSION_END_TIME) || data.time().isAfter(RDR_SESSION_END_TIME)) {
            definingRangeDTO = new DefiningRangeDTO(drHigh, drLow, idrHigh, idrLow);
        }
        if (definingRangeDTO != null) {
            if (data.close() > drHigh
                    || data.close() < drLow
                    || data.open() > drHigh
                    || data.open() < drLow) {
                this.direction = data.close() > drHigh ? Direction.BUY : Direction.SELL;
                this.breakoutHasOccurred = true;
            }
        }
    }

    /**
     * Returns true if the defining range has been formed, false otherwise.
     *
     * @return true if the defining range has been formed, false otherwise
     */
    public Boolean get() {
        return definingRangeDTO != null;
    }

    /**
     * Returns the flag defining if a breakout has occurred.
     *
     * @return true if a breakout has occurred, false otherwise
     */
    public boolean hasBreakoutOccurred() {
        return breakoutHasOccurred;
    }

    /**
     * Returns the highest price observed during the RDR session.
     *
     * @return the highest price observed during the RDR session
     */
    public double getDrHigh() {
        return drHigh;
    }

    /**
     * Returns the lowest price observed during the RDR session.
     *
     * @return the lowest price observed during the RDR session
     */
    public double getDrLow() {
        return drLow;
    }

    /**
     * Returns the direction of the defining range.
     *
     * @return the direction of the defining range
     */
    public Direction getDirection() {
        return direction;
    }
}
