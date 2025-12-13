package com.quarteredge.core.strategy;

import com.quarteredge.core.indicator.AverageTrueRangeIndicator;
import com.quarteredge.core.indicator.DefiningRangeIndicator;
import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.OrderDTO;
import java.util.Optional;

public class QuarterEdgeStrategy implements Strategy {
    private final AverageTrueRangeIndicator atrIndicator;
    private final DefiningRangeIndicator drIndicator;

    public QuarterEdgeStrategy(int atrPeriod) {
        this.atrIndicator = new AverageTrueRangeIndicator(atrPeriod);
        this.drIndicator = new DefiningRangeIndicator();
    }

    public void push(CandleDTO data) {
        atrIndicator.add(data);
        drIndicator.add(data);
    }

    public Optional<OrderDTO> getStatus() {
        return Optional.empty();
    }
}
