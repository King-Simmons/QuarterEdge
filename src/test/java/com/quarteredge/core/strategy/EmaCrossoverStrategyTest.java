package com.quarteredge.core.strategy;

import static com.quarteredge.util.CommonUtils.createDefaultCandleWithClose;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.quarteredge.core.model.OrderDTO;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link EmaCrossoverStrategy}.
 *
 * <p>These tests validate the order status returned by the strategy based on the following rules:
 *
 * <ul>
 *   <li>Returning an empty optional when fewer than the required number of data points have been
 *       added.
 *   <li>Returning an empty optional when no trade should be placed.
 *   <li>Returning an order when the crossover turns bearish.
 * </ul>
 *
 * @see EmaCrossoverStrategy
 * @see OrderDTO
 */
public class EmaCrossoverStrategyTest {
    /** The strategy under test. */
    private EmaCrossoverStrategy emaCrossoverStrategy;

    /**
     * Initializes a new {@link EmaCrossoverStrategy} with a period length of 3 before each test.
     */
    @BeforeEach
    void init() {
        emaCrossoverStrategy = new EmaCrossoverStrategy(1, 2, .1);
    }

    @Test
    @DisplayName("getStatus() should return empty when not enough data")
    void testGetStatusInvalid() {
        assertEquals(Optional.empty(), emaCrossoverStrategy.getStatus());
    }

    @Test
    @DisplayName("getStatus() should return empty when no trade should be placed")
    void testGetStatusNoTrade() {
        populateStrategyWithDefaultData(emaCrossoverStrategy);
        assertEquals(Optional.empty(), emaCrossoverStrategy.getStatus());
    }

    @Test
    @DisplayName("getStatus() should return Order when Crossover turns bearish")
    void testGetStatusBearishTrade() {
        populateStrategyWithBullishData(emaCrossoverStrategy);
        emaCrossoverStrategy.push(createDefaultCandleWithClose(12));
        Optional<OrderDTO> order = emaCrossoverStrategy.getStatus();
        assertTrue(order.isPresent());
    }

    @Test
    @DisplayName("getStatus() should return Order when Crossover turns bullish")
    void testGetStatusBullishTrade() {
        populateStrategyWithBearishData(emaCrossoverStrategy);
        emaCrossoverStrategy.push(createDefaultCandleWithClose(10));
        Optional<OrderDTO> order = emaCrossoverStrategy.getStatus();
        assertTrue(order.isPresent());
    }

    private void populateStrategyWithDefaultData(final EmaCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
    }

    private void populateStrategyWithBullishData(final EmaCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(10));
        strategy.push(createDefaultCandleWithClose(11));
        strategy.push(createDefaultCandleWithClose(12));
        strategy.push(createDefaultCandleWithClose(13));
    }

    private void populateStrategyWithBearishData(final EmaCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(12));
        strategy.push(createDefaultCandleWithClose(11));
        strategy.push(createDefaultCandleWithClose(10));
        strategy.push(createDefaultCandleWithClose(9));
    }
}
