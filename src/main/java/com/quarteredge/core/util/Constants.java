package com.quarteredge.core.util;

/**
 * Defines constant index positions for accessing fields in CSV trading data records.
 * <p>
 * This class provides named constants for the column indices in CSV files containing
 * market data (such as candlestick/OHLCV data). Using these constants instead of
 * literal numeric values improves code readability and maintainability.
 * </p>
 * <p>
 * Expected CSV format: Date, Time, Open, High, Low, Close, Volume
 * </p>
 *
 * @author QuarterEdge
 * @version 1.0
 * @since 1.0
 * @see Parser
 */
public final class Constants {
    /**
     * Index position for the date field in CSV data records.
     */
    public static final int DATE_INDEX = 0;

    /**
     * Index position for the time field in CSV data records.
     */
    public static final int TIME_INDEX = 1;

    /**
     * Index position for the opening price field in CSV data records.
     */
    public static final int OPEN_INDEX = 2;

    /**
     * Index position for the high-price field in CSV data records.
     */
    public static final int HIGH_INDEX = 3;

    /**
     * Index position for the low-price field in CSV data records.
     */
    public static final int LOW_INDEX = 4;

    /**
     * Index position for the closing price field in CSV data records.
     */
    public static final int CLOSE_INDEX = 5;

    /**
     * Index position for the volume field in CSV data records.
     */
    public static final int VOLUME_INDEX = 6;
}
