package com.quarteredge.core.strategy;

import com.quarteredge.core.model.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.quarteredge.util.CommonUtils.createDefaultCandleWithClose;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmaCrossoverStrategyTest {
    private EmaCrossoverStrategy emaCrossoverStrategy;

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

    private void populateStrategyWithDefaultData(EmaCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
        strategy.push(createDefaultCandleWithClose(1));
    }

    private void populateStrategyWithBullishData(EmaCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(10));
        strategy.push(createDefaultCandleWithClose(11));
        strategy.push(createDefaultCandleWithClose(12));
        strategy.push(createDefaultCandleWithClose(13));
    }

    private void populateStrategyWithBearishData(EmaCrossoverStrategy strategy) {
        strategy.push(createDefaultCandleWithClose(12));
        strategy.push(createDefaultCandleWithClose(11));
        strategy.push(createDefaultCandleWithClose(10));
        strategy.push(createDefaultCandleWithClose(9));
    }
}
