package com.quarteredge.core.indicator;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.util.FifoQueue;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class AverageTrueRangeIndicator implements Indicator {
    private final int length;
    private final FifoQueue<BigDecimal> dataQueue;
    private BigDecimal val;
    private BigDecimal total;

    public AverageTrueRangeIndicator(int length) {
        this.length = length;
        this.dataQueue = new FifoQueue<>(length);
        this.val = new BigDecimal(-1);
        this.total = new BigDecimal(0);
    }

    @Override
    public void add(CandleDTO data) {
        calculate(data.close());
    }

    @Override
    public BigDecimal get() {
        return val;
    }

    private void calculate(double input) {
        if (dataQueue.size() == length && length > 0) {
            total = total.subtract(dataQueue.poll());
        }
        total = total.add(new BigDecimal(input));
        dataQueue.add(new BigDecimal(input));
        val = total.divide(new BigDecimal(length), 2, RoundingMode.HALF_UP);
    }
}
