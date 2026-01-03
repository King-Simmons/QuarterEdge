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

public class Resample {

    private static class OHLCV {
        long timestamp; // in milliseconds
        double open;
        double high;
        double low;
        double close;
        int volume;

        public OHLCV(
                long timestamp, double open, double high, double low, double close, int volume) {
            this.timestamp = timestamp;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
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

    public static void resampleTo5Min(String inputFile, String outputFile) throws IOException {
        List<OHLCV> candles = readInputFile(inputFile);
        List<OHLCV> resampledCandles = resampleCandles(candles);
        writeOutputFile(outputFile, resampledCandles);
    }

    private static List<OHLCV> readInputFile(String inputFile) throws IOException {
        List<OHLCV> candles = new ArrayList<>();

        try (BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 10) continue;

                try {
                    long timestamp =
                            Long.parseLong(parts[0].trim()) / 1_000_000; // Convert to milliseconds
                    double open = Double.parseDouble(parts[4].trim());
                    double high = Double.parseDouble(parts[5].trim());
                    double low = Double.parseDouble(parts[6].trim());
                    double close = Double.parseDouble(parts[7].trim());
                    int volume = Integer.parseInt(parts[8].trim());

                    candles.add(new OHLCV(timestamp, open, high, low, close, volume));
                } catch (NumberFormatException e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        }

        // Sort by timestamp to ensure data is in order
        candles.sort(Comparator.comparingLong(c -> c.timestamp));
        return candles;
    }

    private static List<OHLCV> resampleCandles(List<OHLCV> candles) {
        if (candles.isEmpty()) {
            return new ArrayList<>();
        }

        List<OHLCV> resampled = new ArrayList<>();

        // Group by 5-minute intervals
        long intervalMs = 5 * 60 * 1000; // 5 minutes in milliseconds
        long currentInterval = (candles.get(0).timestamp / intervalMs) * intervalMs;

        List<OHLCV> currentGroup = new ArrayList<>();

        for (OHLCV candle : candles) {
            if (candle.timestamp >= currentInterval + intervalMs) {
                // Process the completed group
                if (!currentGroup.isEmpty()) {
                    resampled.add(aggregateCandles(currentGroup));
                    currentGroup.clear();
                }
                // Move to next interval
                currentInterval = (candle.timestamp / intervalMs) * intervalMs;
            }
            currentGroup.add(candle);
        }

        // Process the last group
        if (!currentGroup.isEmpty()) {
            resampled.add(aggregateCandles(currentGroup));
        }

        return resampled;
    }

    private static OHLCV aggregateCandles(List<OHLCV> candles) {
        if (candles.isEmpty()) {
            throw new IllegalArgumentException("Cannot aggregate empty candle list");
        }

        double open = candles.get(0).open;
        double high = candles.stream().mapToDouble(c -> c.high).max().orElse(0);
        double low = candles.stream().mapToDouble(c -> c.low).min().orElse(0);
        double close = candles.get(candles.size() - 1).close;
        int volume = candles.stream().mapToInt(c -> c.volume).sum();

        return new OHLCV(
                candles.get(0).timestamp, // Use the start time of the interval
                open,
                high,
                low,
                close,
                volume);
    }

    private static void writeOutputFile(String outputFile, List<OHLCV> candles) throws IOException {
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

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(
                    "Usage: java -cp <classpath> com.quarteredge.core.util.Resample <input_file>"
                            + " <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            resampleTo5Min(inputFile, outputFile);
            System.out.println("Successfully resampled data to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
