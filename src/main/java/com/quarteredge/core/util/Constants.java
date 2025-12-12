package com.quarteredge.core.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Defines constant index positions for accessing fields in CSV trading data records.
 *
 * <p>This class provides named constants for the column indices in CSV files containing market data
 * (such as candlestick/OHLCV data). Using these constants instead of literal numeric values
 * improves code readability and maintainability.
 *
 * <p>Expected CSV format: Date, Time, Open, High, Low, Close, Volume
 *
 * @author King Simmons
 * @version 1.0
 * @since 1.0
 * @see Parser
 */
public final class Constants {
    /** Index position for the date field in CSV data records. */
    public static final int DATE_INDEX = 0;

    /** Index position for the time field in CSV data records. */
    public static final int TIME_INDEX = 1;

    /** Index position for the opening price field in CSV data records. */
    public static final int OPEN_INDEX = 2;

    /** Index position for the high-price field in CSV data records. */
    public static final int HIGH_INDEX = 3;

    /** Index position for the low-price field in CSV data records. */
    public static final int LOW_INDEX = 4;

    /** Index position for the closing price field in CSV data records. */
    public static final int CLOSE_INDEX = 5;

    /** Index position for the volume field in CSV data records. */
    public static final int VOLUME_INDEX = 6;

    /** The time of the last candle in a trading session. (DAY). */
    public static final LocalTime LAST_CANDLE_CLOSE_TIME = LocalTime.of(16, 55, 0);

    /** The time of the first candle in a trading session. (DAY). */
    public static final LocalTime FIRST_CANDLE_OPEN_TIME = LocalTime.of(18, 0, 0);

    /** The time of the first candle in the RDR session. */
    public static final LocalTime RDR_SESSION_START_TIME = LocalTime.of(9, 29, 59);

    /** The time of the last candle in the RDR session. */
    public static final LocalTime RDR_SESSION_END_TIME = LocalTime.of(10, 30, 0);

    /** The formatter for parsing Date Time. */
    public static final DateTimeFormatter DATE_TIME_DEFAULT_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /** The increment value for the Quarter Level calculation. */
    public static final int QUARTER_LEVEL_INCREMENT = 25;
}
