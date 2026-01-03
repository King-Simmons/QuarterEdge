package com.quarteredge.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for resampling OHLCV (Open, High, Low, Close, Volume) financial data to different
 * time intervals. This implementation specifically handles resampling to 5-minute intervals.
 */
public class Resample {
    /** Conversion factor from nanoseconds to milliseconds. */
    private static final int NANOSECONDS_TO_MILLISECONDS = 1_000_000;

    /** Duration of 5 minutes in milliseconds. */
    private static final int FIVE_MINUTES_MS = 5 * 60 * 1000;

    /** Number of required columns in the input CSV (0-8 inclusive). */
    private static final int CSV_REQUIRED_COLUMNS = 9;

    /** Represents a single OHLCV (Open, High, Low, Close, Volume) data point. */
    private static final class OHLCV {
        /** Timestamp in milliseconds since epoch. */
        private final long timestamp;

        /** Opening price. */
        private final double open;

        /** Highest price in the period. */
        private final double high;

        /** Lowest price in the period. */
        private final double low;

        /** Closing price. */
        private final double close;

        /** Trading volume. */
        private final int volume;

        OHLCV(
                final long timestamp,
                final double open,
                final double high,
                final double low,
                final double close,
                final int volume) {
            this.timestamp = timestamp;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public double getOpen() {
            return open;
        }

        public double getHigh() {
            return high;
        }

        public double getLow() {
            return low;
        }

        public double getClose() {
            return close;
        }

        public int getVolume() {
            return volume;
        }

        @Override
        public String toString() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(new Date(timestamp));
            return String.format(
                    Locale.US,
                    "%s,%.2f,%.2f,%.2f,%.2f,%d",
                    formattedDate,
                    open,
                    high,
                    low,
                    close,
                    volume);
        }
    }

    /**
     * Resamples OHLCV data from an input file to 5-minute intervals and writes to an output file.
     *
     * @param inputFile Path to the input CSV file containing OHLCV data
     * @param outputFile Path where the resampled data will be written
     * @throws IOException if there's an error reading or writing the files
     * @throws IllegalArgumentException if input parameters are invalid
     */
    public static void resampleTo5Min(final String inputFile, final String outputFile)
            throws IOException {
        List<OHLCV> candles = readInputFile(inputFile);
        List<OHLCV> resampledCandles = resampleCandles(candles);
        writeOutputFile(outputFile, resampledCandles);
    }

    /**
     * Reads OHLCV data from a CSV file.
     *
     * @param inputFile Path to the input CSV file
     * @return List of OHLCV objects parsed from the input file
     * @throws IOException if there's an error reading the file
     * @throws IllegalArgumentException if the input file has an invalid format
     */
    private static List<OHLCV> readInputFile(final String inputFile) throws IOException {
        List<OHLCV> candles = new ArrayList<>();

        try (BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length <= CSV_REQUIRED_COLUMNS) {
                    continue;
                }

                try {
                    long timestamp = Long.parseLong(parts[0].trim()) / NANOSECONDS_TO_MILLISECONDS;
                    final int openIdx = 4;
                    final int highIdx = 5;
                    final int lowIdx = 6;
                    final int closeIdx = 7;
                    final int volumeIdx = 8;

                    double open = Double.parseDouble(parts[openIdx].trim());
                    double high = Double.parseDouble(parts[highIdx].trim());
                    double low = Double.parseDouble(parts[lowIdx].trim());
                    double close = Double.parseDouble(parts[closeIdx].trim());
                    int volume = Integer.parseInt(parts[volumeIdx].trim());

                    candles.add(new OHLCV(timestamp, open, high, low, close, volume));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        }

        // Sort by timestamp to ensure data is in order
        candles.sort(Comparator.comparingLong(OHLCV::getTimestamp));
        return candles;
    }

    /**
     * Resamples a list of OHLCV candles to 5-minute intervals.
     *
     * @param candles List of input OHLCV candles to be resampled
     * @return List of resampled OHLCV candles with 5-minute intervals
     * @throws IllegalArgumentException if the input list is empty
     */
    private static List<OHLCV> resampleCandles(final List<OHLCV> candles) {
        if (candles.isEmpty()) {
            return new ArrayList<>();
        }

        List<OHLCV> resampled = new ArrayList<>();
        long currentInterval =
                (candles.getFirst().getTimestamp() / FIVE_MINUTES_MS) * FIVE_MINUTES_MS;
        List<OHLCV> currentGroup = new ArrayList<>();

        for (OHLCV candle : candles) {
            if (candle.getTimestamp() >= currentInterval + FIVE_MINUTES_MS) {
                // Process the completed group
                if (!currentGroup.isEmpty()) {
                    resampled.add(aggregateCandles(currentGroup));
                    currentGroup.clear();
                }
                // Move to the next interval
                currentInterval = (candle.getTimestamp() / FIVE_MINUTES_MS) * FIVE_MINUTES_MS;
            }
            currentGroup.add(candle);
        }

        // Process the last group
        if (!currentGroup.isEmpty()) {
            resampled.add(aggregateCandles(currentGroup));
        }

        return resampled;
    }

    /**
     * Aggregates multiple OHLCV candles into a single candle.
     *
     * @param candles List of OHLCV candles to aggregate
     * @return A single OHLCV candle representing the aggregated data
     * @throws IllegalArgumentException if the input list is empty
     */
    private static OHLCV aggregateCandles(final List<OHLCV> candles) {
        if (candles.isEmpty()) {
            throw new IllegalArgumentException("Cannot aggregate empty candle list");
        }

        double open = candles.getFirst().getOpen();
        double high = candles.stream().mapToDouble(OHLCV::getHigh).max().orElse(0);
        double low = candles.stream().mapToDouble(OHLCV::getLow).min().orElse(0);
        double close = candles.getLast().getClose();
        int volume = candles.stream().mapToInt(OHLCV::getVolume).sum();

        return new OHLCV(
                candles.getFirst().getTimestamp(), // Use the start time of the interval
                open,
                high,
                low,
                close,
                volume);
    }

    /**
     * Writes a list of OHLCV candles to a CSV file.
     *
     * @param outputFile Path to the output CSV file
     * @param candles List of OHLCV candles to write
     * @throws IOException if there's an error writing to the file
     */
    private static void writeOutputFile(final String outputFile, final List<OHLCV> candles)
            throws IOException {
        try (BufferedWriter writer =
                new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            // Write header
            writer.write("datetime,open,high,low,close,volume\n");

            // Write data
            for (OHLCV candle : candles) {
                writer.write(candle.toString() + "\n");
            }
        }
    }

    /**
     * Main method for command-line execution.
     *
     * @param args Command line arguments: [input_file] [output_file]
     * @throws IOException if there's an error processing the files
     */
    static void main(final String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println(
                    "Usage: java -cp <classpath> com.quarteredge.core.util.Resample <input_file>"
                            + " <output_file>");
            System.exit(1);
        }

        String inputFile = "";
        String outputFile = "";

        resampleTo5Min(inputFile, outputFile);
        System.out.println("Successfully resampled data to " + outputFile);
    }
}
