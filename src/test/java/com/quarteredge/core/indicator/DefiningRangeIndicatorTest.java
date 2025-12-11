package com.quarteredge.core.indicator;

import static com.quarteredge.util.CommonUtils.generateTestSession;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.quarteredge.core.model.CandleDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefiningRangeIndicatorTest {
    private DefiningRangeIndicator drIndicator;
    private final List<CandleDTO> testSession = generateTestSession();

    @BeforeEach
    void init() {
        drIndicator = new DefiningRangeIndicator();
    }

    @Test
    void testGetInvalid() {
        assertNull(drIndicator.get());
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
