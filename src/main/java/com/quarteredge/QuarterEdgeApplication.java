package com.quarteredge;

import com.quarteredge.core.model.BackTestSession;
import com.quarteredge.core.strategy.EmaCrossoverStrategy;
import com.quarteredge.core.util.Parser;
import java.io.File;

/**
 * Main application class for QuarterEdge. This class serves as the entry point for the QuarterEdge
 * application.
 */
public class QuarterEdgeApplication {
    /** Represents the period length for the fast Exponential Moving Average (EMA) calculation. */
    private static final int FAST_EMA_PERIOD = 5;

    /** Represents the period length for the slow Exponential Moving Average (EMA) calculation. */
    private static final int SLOW_EMA_PERIOD = 20;

    /** Represents the increment value for the Exponential Moving Average (EMA) calculation. */
    private static final double INCREMENT = 0.01;

    /** Application entry point. Prints "Hello World" to the standard output stream. */
    static void main() {
        System.out.println("Hello World");
        var parser = new Parser(new File("data/CL_5min_sample.csv"));
        parser.parse();
        var strategy = new EmaCrossoverStrategy(FAST_EMA_PERIOD, SLOW_EMA_PERIOD, INCREMENT);
        parser.getSessionMap()
                .forEach(
                        (key, value) -> {
                            IO.println(key);
                            var backTestSession = new BackTestSession(strategy, value);
                            IO.println(backTestSession.getStatus());
                            backTestSession.startSession();
                            IO.println(backTestSession.getStatus());
                            backTestSession.getOrders().forEach(IO::println);
                        });
    }
}
