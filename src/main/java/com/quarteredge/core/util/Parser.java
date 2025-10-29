package com.quarteredge.core.util;

import com.quarteredge.core.model.Candle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import static com.quarteredge.core.util.Constants.*;

/**
 * Parses CSV trading data files and organizes data into trading sessions.
 * This parser processes CSV files containing market data (such as CL futures data)
 * and groups the records into sessions mapped by date. Each session contains
 * multiple records represented as lists of string values.
 *
 * @since 1.0
 */
public class Parser {
    /**
     * The CSV file to be parsed.
     */
    private final File file;

    /**
     * Map containing parsed data organized by trading session date.
     * <p>
     * Key: Date string representing the trading session<br>
     * Value: List of records, where each record is a list of field values
     * </p>
     */
    private final Map<String, List<Candle>> sessionMap;

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
     * <p>
     * This method reads the CSV file line by line, processes each record,
     * and organizes the data into trading sessions. The parsed data can
     * be retrieved using {@link #getSessionMap()}.
     * </p>
     *
     * @throws IllegalStateException if the file has not been set or is invalid
     */
    public void parse() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Iterator<String> iterator = reader.lines().iterator();
            List<Candle> currSession = new ArrayList<>();

            while (iterator.hasNext()) {
                List<String> currLine = Arrays.asList(iterator.next().split("[, ]"));
                // currSession.add(currLine.subList(1, currLine.size()));
                Candle data =
                        new Candle(
                                currLine.get(DATE_INDEX),
                                currLine.get(TIME_INDEX),
                                Double.parseDouble(currLine.get(OPEN_INDEX)),
                                Double.parseDouble(currLine.get(HIGH_INDEX)),
                                Double.parseDouble(currLine.get(LOW_INDEX)),
                                Double.parseDouble(currLine.get(CLOSE_INDEX)),
                                Double.parseDouble(currLine.get(VOLUME_INDEX)));
                currSession.add(data);

                if (data.time().equals("16:55:00")) {
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
     * <p>
     * The map is organized with date strings as keys and lists of records
     * as values. Each record is represented as a list of string fields.
     * </p>
     *
     * @return an immutable view of the session map containing parsed data,
     *         or an empty map if {@link #parse()} has not been called yet
     */
    public Map<String, List<Candle>> getSessionMap() {
        return sessionMap;
    }
}
