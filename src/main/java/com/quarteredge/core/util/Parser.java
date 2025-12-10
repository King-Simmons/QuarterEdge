package com.quarteredge.core.util;

import static com.quarteredge.core.util.Constants.CLOSE_INDEX;
import static com.quarteredge.core.util.Constants.DATE_INDEX;
import static com.quarteredge.core.util.Constants.DATE_TIME_DEFAULT_FORMAT;
import static com.quarteredge.core.util.Constants.HIGH_INDEX;
import static com.quarteredge.core.util.Constants.LAST_CANDLE_CLOSE_TIME;
import static com.quarteredge.core.util.Constants.LOW_INDEX;
import static com.quarteredge.core.util.Constants.OPEN_INDEX;
import static com.quarteredge.core.util.Constants.TIME_INDEX;
import static com.quarteredge.core.util.Constants.VOLUME_INDEX;

import com.quarteredge.core.model.CandleDTO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses CSV trading data files and organizes candlestick data into trading sessions.
 *
 * <p>This parser processes CSV files containing OHLCV (Open, High, Low, Close, Volume) market data
 * and groups the candles into sessions mapped by date. Each session represents a full trading day
 * and ends when a candle with the time "16:55:00" is encountered.
 *
 * <p>Expected CSV format: Date, Time, Open, High, Low, Close, Volume
 *
 * @author King Simmons
 * @version 1.0
 * @since 1.0
 * @see CandleDTO
 * @see Constants
 */
public class Parser {
    /** The CSV file to be parsed. */
    private final File file;

    /**
     * Map containing parsed candlestick data organized by trading session date.
     *
     * <p>Key: Date string representing the trading session (e.g., "2024-01-15")<br>
     * Value: List of {@link CandleDTO} objects for that trading session
     *
     * <p>Uses {@link LinkedHashMap} to maintain insertion order of sessions.
     */
    private final Map<String, List<CandleDTO>> sessionMap;

    /**
     * Constructs a new Parser for the specified CSV file.
     *
     * @param file the CSV file containing trading data to parse
     * @throws IllegalArgumentException if a file is null
     */
    public Parser(final File file) {
        this.file = file;
        this.sessionMap = new LinkedHashMap<>();
    }

    /**
     * Parses the CSV file and populates the session map.
     *
     * <p>This method reads the CSV file line by line, processes each record, and organizes the data
     * into trading sessions. The parsed data can be retrieved using {@link #getSessionMap()}.
     *
     * @throws IllegalStateException if the file has not been set or is invalid
     */
    public void parse() {
        try {
            var reader = new BufferedReader(new FileReader(file));
            var iterator = reader.lines().iterator();
            var currSession = new ArrayList<CandleDTO>();

            while (iterator.hasNext()) {
                var currLine = Arrays.asList(iterator.next().split("[, ]"));
                var data =
                        new CandleDTO(
                                currLine.get(DATE_INDEX),
                                LocalTime.parse(currLine.get(TIME_INDEX), DATE_TIME_DEFAULT_FORMAT),
                                Double.parseDouble(currLine.get(OPEN_INDEX)),
                                Double.parseDouble(currLine.get(HIGH_INDEX)),
                                Double.parseDouble(currLine.get(LOW_INDEX)),
                                Double.parseDouble(currLine.get(CLOSE_INDEX)),
                                Double.parseDouble(currLine.get(VOLUME_INDEX)));
                currSession.add(data);

                if (data.time().equals(LAST_CANDLE_CLOSE_TIME)) {
                    sessionMap.put(data.date(), new ArrayList<>(currSession));
                    currSession.clear();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the map of parsed trading sessions.
     *
     * <p>The map is organized with date strings as keys and lists of {@link CandleDTO} objects as
     * values. Each list represents a complete trading session for that date. The map maintains the
     * insertion order (chronological order of sessions).
     *
     * @return the session map containing parsed candlestick data grouped by date, or an empty map
     *     if {@link #parse()} has not been called yet
     */
    public Map<String, List<CandleDTO>> getSessionMap() {
        return sessionMap;
    }
}
