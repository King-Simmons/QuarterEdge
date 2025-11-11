package com.quarteredge.util;

import com.quarteredge.core.model.CandleDTO;

public class CommonUtils {
    /**
     * Creates a {@link CandleDTO} with the provided close price and placeholder values for other
     * fields to facilitate testing.
     *
     * @param close the close price to embed in the test candle
     * @return a {@link CandleDTO} instance with the specified close
     */
    public static CandleDTO createDefaultCandleWithClose(final double close) {
        return new CandleDTO("", "", 2, 2, 2, close, 2);
    }
}
