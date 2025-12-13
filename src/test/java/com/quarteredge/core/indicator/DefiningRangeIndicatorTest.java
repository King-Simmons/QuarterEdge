package com.quarteredge.core.indicator;

import static com.quarteredge.util.CommonUtils.generateTestSession;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.quarteredge.core.model.CandleDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefiningRangeIndicatorTest {
    /** The DefiningRangeIndicator instance to be tested. */
    private DefiningRangeIndicator drIndicator;

    /** The test session data to be used for testing. */
    private final List<CandleDTO> testSession = generateTestSession();

    /** Initializes the DefiningRangeIndicator instance before each test. */
    @BeforeEach
    void init() {
        drIndicator = new DefiningRangeIndicator();
    }

    @Test
    void testGetInvalid() {
        assertFalse(drIndicator.get());
    }

    @Test
    void testGetValid() {
        for (CandleDTO candle : testSession) {
            drIndicator.add(candle);
            IO.println(candle);
            IO.println(drIndicator.get());
            IO.println(drIndicator.hasBreakoutOccurred());
        }
    }
}
