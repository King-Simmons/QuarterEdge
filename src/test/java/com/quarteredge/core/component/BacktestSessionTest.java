package com.quarteredge.core.component;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.SessionStatus;
import com.quarteredge.core.strategy.EmaCrossoverStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.quarteredge.util.CommonUtils.createDefaultCandleList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BacktestSessionTest {
    private BacktestSession backtestSession;
    private final EmaCrossoverStrategy EmaStrategy = new EmaCrossoverStrategy(1, 2, .1);
    private List<CandleDTO> data = createDefaultCandleList();

    @BeforeEach
    void init() {
        backtestSession = new BacktestSession(EmaStrategy, data);
    }

    @Test
    @DisplayName("Backtest should start with PENDING status")
    void testBacktestSessionPending() {
        assertEquals(SessionStatus.PENDING, backtestSession.getStatus());
    }

    @Test
    @DisplayName("startSession() should complete successfully")
    void testBacktestSessionSuccess() {
        backtestSession.startSession();
        assertEquals(SessionStatus.COMPLETED, backtestSession.getStatus());
    }

    @Test
    @DisplayName("startSession() should complete successfully")
    void testBacktestSessionSuccess2() {
        backtestSession.startSession();
        assertEquals(SessionStatus.COMPLETED, backtestSession.getStatus());
    }
}
