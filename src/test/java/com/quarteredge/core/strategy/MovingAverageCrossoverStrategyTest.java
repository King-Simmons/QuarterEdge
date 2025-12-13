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
 * Unit tests for {@link MovingAverageCrossoverStrategy}.
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
 * @see MovingAverageCrossoverStrategy
 * @see OrderDTO
 */
public class MovingAverageCrossoverStrategyTest {
    /** The strategy under test. */
    private MovingAverageCrossoverStrategy maCrossoverStrategy;

    /**
     * Initializes a new {@link MovingAverageCrossoverStrategy} with a period length of 3 before
     * each test.
     */
    @BeforeEach
    void init() {
        maCrossoverStrategy = new MovingAverageCrossoverStrategy(1, 2, .1);
    }

    @Test
    @DisplayName("getStatus() should return empty when not enough data")
    void testGetStatusInvalid() {
        assertEquals(Optional.empty(), maCrossoverStrategy.getStatus());
    }

    @Test
    @DisplayName("getStatus() should return empty when no trade should be placed")
    void testGetStatusNoTrade() {
        populateStrategyWithDefaultData(maCrossoverStrategy);
        assertEquals(Optional.empty(), maCrossoverStrategy.getStatus());
    }

    @Test
    @DisplayName("getStatus() should return Order when Crossover turns bearish")
    void testGetStatusBearishTrade() {
        populateStrategyWithBullishData(maCrossoverStrategy);
        maCrossoverStrategy.push(createDefaultCandleWithClose(12));
        Optional<OrderDTO> order = maCrossoverStrategy.getStatus();
        assertTrue(order.isPresent());
    }

    @Test
    @DisplayName("getStatus() should return Order when Crossover turns bullish")
    void testGetStatusBullishTrade() {
        populateStrategyWithBearishData(maCrossoverStrategy);
        maCrossoverStrategy.push(createDefaultCandleWithClose(10));
        Optional<OrderDTO> order = maCrossoverStrategy.getStatus();
        assertTrue(order.isPresent());
    }

    private void populateStrategyWithDefaultData(final MovingAverageCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
    }

    private void populateStrategyWithBullishData(final MovingAverageCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(10));
        strategy.push(createDefaultCandleWithClose(11));
        strategy.push(createDefaultCandleWithClose(12));
        strategy.push(createDefaultCandleWithClose(13));
    }

    private void populateStrategyWithBearishData(final MovingAverageCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(12));
        strategy.push(createDefaultCandleWithClose(11));
        strategy.push(createDefaultCandleWithClose(10));
        strategy.push(createDefaultCandleWithClose(9));
    }
}
