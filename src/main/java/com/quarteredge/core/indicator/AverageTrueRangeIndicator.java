package com.quarteredge.core.indicator;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.util.FifoQueue;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class AverageTrueRangeIndicator implements Indicator {
    private final int length;
    private final FifoQueue<BigDecimal> dataQueue;
    private BigDecimal atr;

    public AverageTrueRangeIndicator(int length) {
        this.length = length;
        this.dataQueue = new FifoQueue<>(length);
        this.atr = new BigDecimal(-1);
    }

    @Override
    public void add(CandleDTO data) {
        calculate(
                new BigDecimal(data.high()),
                new BigDecimal(data.low()),
                new BigDecimal(data.close()));
    }

    @Override
    public BigDecimal get() {
        return atr;
    }

    private void calculate(BigDecimal high, BigDecimal low, BigDecimal close) {
        BigDecimal trueRange = high.subtract(low).setScale(2, RoundingMode.HALF_DOWN);
        trueRange = trueRange.max(high.subtract(close).setScale(2, RoundingMode.HALF_DOWN));
        trueRange = trueRange.max(low.subtract(close).setScale(2, RoundingMode.HALF_DOWN));
        // IO.println(trueRange);
        if (dataQueue.size() < length) {
            dataQueue.add(trueRange);

            if (dataQueue.size() == length) {
                BigDecimal total = new BigDecimal(0);
                for (BigDecimal value : dataQueue.getQueue()) {
                    total = total.add(value);
                }
                atr = total.divide(new BigDecimal(length), 2, RoundingMode.HALF_UP);
            }
        } else {
            dataQueue.add(trueRange);
            IO.println(atr);
            atr =
                    atr.multiply(new BigDecimal(length - 1))
                            .add(trueRange)
                            .divide(new BigDecimal(length), RoundingMode.HALF_DOWN);
        }
    }
}
