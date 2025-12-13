package com.quarteredge;

import com.quarteredge.core.service.BacktestService;
import com.quarteredge.core.strategy.MovingAverageCrossoverStrategy;

/**
 * Main application class for QuarterEdge. This class serves as the entry point for the QuarterEdge
 * application.
 */
public class QuarterEdgeApplication {
    /** Represents the period length for the fast Simple Moving Average (SMA) calculation. */
    private static final int FAST_SMA_PERIOD = 5;

    /** Represents the period length for the slow Simple Moving Average (SMA) calculation. */
    private static final int SLOW_SMA_PERIOD = 20;

    /** Represents the increment value for the Simple Moving Average (SMA) calculation. */
    private static final double INCREMENT = 0.01;

    /** Application entry point. Prints "Hello World" to the standard output stream. */
    static void main() {
        System.out.println("Hello World");
        var strategy =
                new MovingAverageCrossoverStrategy(FAST_SMA_PERIOD, SLOW_SMA_PERIOD, INCREMENT);
        BacktestService backtestService = new BacktestService(strategy, "data/CL_5min_sample.csv");
        backtestService.run();
    }
}
