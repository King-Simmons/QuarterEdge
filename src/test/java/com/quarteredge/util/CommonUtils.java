package com.quarteredge.util;

import com.quarteredge.core.model.CandleDTO;
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
        return new CandleDTO("", "09:30:00", 2, 2, 2, close, 2);
    }

    public static List<CandleDTO> createDefaultCandleList() {
        List<CandleDTO> defaultList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            defaultList.add(createDefaultCandleWithClose(i));
        }
        return defaultList;
    }
}
