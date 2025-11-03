package com.quarteredge;

import com.quarteredge.core.indicator.EmaIndicator;
import com.quarteredge.core.util.Parser;
import java.io.File;

/**
 * Main application class for QuarterEdge. This class serves as the entry point for the QuarterEdge
 * application.
 */
public class QuarterEdgeApplication {
    /**
     * The default period length for the Exponential Moving Average calculation. A 20-period EMA is
     * commonly used in technical analysis for short to medium-term trend identification.
     */
    private static final int EMA_PERIOD = 20;

    /** Application entry point. Prints "Hello World" to the standard output stream. */
    static void main() {
        System.out.println("Hello World");
        Parser parser = new Parser(new File("data/CL_5min_sample.csv"));
        parser.parse();
        EmaIndicator emaIndicator = new EmaIndicator(EMA_PERIOD);
        // DecimalFormat df = new DecimalFormat("#.##");
        parser.getSessionMap()
                .forEach(
                        (key, value) -> {
                            IO.println(key);
                            value.forEach(
                                    a -> {
                                        IO.println(a);
                                        emaIndicator.add(a);
                                        IO.println(emaIndicator.get());
                                    });
                        });
    }
}
