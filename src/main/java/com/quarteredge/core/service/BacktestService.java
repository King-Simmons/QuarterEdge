package com.quarteredge.core.service;

import com.quarteredge.core.model.BackTestSession;
import com.quarteredge.core.strategy.Strategy;
import com.quarteredge.core.util.Parser;
import java.io.File;

/** Service class for running backtests. */
public class BacktestService {
    /** The parser to be used for parsing the data file. */
    private final Parser parser;

    /** The strategy to be used for the backtest. */
    private final Strategy strategy;

    /**
     * Constructor for the BacktestService class.
     *
     * @param strategy The strategy to be used for the backtest.
     * @param filePath The path to the data file to be parsed.
     */
    public BacktestService(final Strategy strategy, final String filePath) {
        this.strategy = strategy;
        this.parser = new Parser(new File(filePath));
    }

    /** Runs the backtest. */
    public void run() {
        parser.parse();
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
