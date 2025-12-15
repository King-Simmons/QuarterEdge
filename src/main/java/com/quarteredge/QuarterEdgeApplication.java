package com.quarteredge;

import com.quarteredge.core.service.BacktestService;
import com.quarteredge.core.strategy.QuarterEdgeStrategy;

/**
 * Main application class for QuarterEdge. This class serves as the entry point for the QuarterEdge
 * application.
 */
public class QuarterEdgeApplication {
    /** Represents the period length for the ATR calculation. */
    private static final int ATR_PERIOD = 14;

    /** Application entry point. Prints "Hello World" to the standard output stream. */
    static void main() {
        System.out.println("Hello World");
        var strategy = new QuarterEdgeStrategy(ATR_PERIOD);
        BacktestService backtestService = new BacktestService(strategy, "data/CL_5min_sample.csv");
        backtestService.run();
    }
}
