package com.quarteredge.util;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.util.Parser;
import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    /**
     * Creates a {@link CandleDTO} with the provided close price and placeholder values for other
     * fields to facilitate testing.
     *
     * @param close the close price to embed in the test candle
     * @return a {@link CandleDTO} instance with the specified close
     */
    public static CandleDTO createDefaultCandleWithClose(final double close) {
        return new CandleDTO("", LocalTime.of(9, 35, 0), 2, 2, 2, close, 2);
    }

    /**
     * Creates a list of {@link CandleDTO} instances with placeholder values for other fields to
     * facilitate testing.
     *
     * @return a list of {@link CandleDTO} instances
     */
    public static List<CandleDTO> createDefaultCandleList() {
        List<CandleDTO> defaultList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            defaultList.add(createDefaultCandleWithClose(i));
        }
        return defaultList;
    }

    public static List<CandleDTO> generateTestSession() {
        Parser parser = new Parser(new File("src/test/java/com/quarteredge/util/testSession.csv"));
        parser.parse();
        List<CandleDTO> testSession = new ArrayList<>();
        parser.getSessionMap().values().forEach(testSession::addAll);
        return testSession;
    }
}
