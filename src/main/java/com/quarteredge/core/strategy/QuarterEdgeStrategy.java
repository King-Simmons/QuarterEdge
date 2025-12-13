package com.quarteredge.core.strategy;

import static com.quarteredge.core.util.Constants.CL_TICK_INCREMENT;
import static com.quarteredge.core.util.Constants.LAST_CANDLE_CLOSE_TIME;

import com.quarteredge.core.indicator.AverageTrueRangeIndicator;
import com.quarteredge.core.indicator.DefiningRangeIndicator;
import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.Direction;
import com.quarteredge.core.model.OrderDTO;
import com.quarteredge.core.model.OrderStatus;
import com.quarteredge.core.util.CommonMethods;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class QuarterEdgeStrategy implements Strategy {
    private final AverageTrueRangeIndicator atrIndicator;
    private final DefiningRangeIndicator drIndicator;
    private boolean isOrderCreated;

    public QuarterEdgeStrategy(int atrPeriod) {
        this.atrIndicator = new AverageTrueRangeIndicator(atrPeriod);
        this.drIndicator = new DefiningRangeIndicator();
        this.isOrderCreated = false;
    }

    public void push(CandleDTO data) {
        atrIndicator.add(data);
        drIndicator.add(data);
        if (data.time().isAfter(LAST_CANDLE_CLOSE_TIME)) {
            isOrderCreated = false;
        }
    }

    public Optional<OrderDTO> getStatus() {
        if (isOrderCreated) {
            return Optional.empty();
        }
        if (!drIndicator.get() || !drIndicator.hasBreakoutOccurred()) {
            return Optional.empty();
        }
        if (atrIndicator.get().doubleValue() > 0) {
            return Optional.empty();
        }

        Optional<OrderDTO> order = Optional.of(createOrder());
        isOrderCreated = true;
        return order;
    }

    private OrderDTO createOrder() {
        double atr = atrIndicator.get().doubleValue();
        double high = drIndicator.getDrHigh();
        double low = drIndicator.getDrLow();
        Direction direction = drIndicator.getDirection();
        List<Double> getQTLevels =
                CommonMethods.getQuarterLevelsInRange(low, high, CL_TICK_INCREMENT);

        BigDecimal entryPrice = new BigDecimal(getQTLevels.getFirst());
        BigDecimal stopLoss =
                direction == Direction.BUY
                        ? entryPrice.subtract(new BigDecimal(atr))
                        : entryPrice.add(new BigDecimal(atr));
        BigDecimal takeProfit =
                direction == Direction.BUY
                        ? entryPrice.add(new BigDecimal(atr))
                        : entryPrice.subtract(new BigDecimal(atr));
        return new OrderDTO(
                stopLoss.doubleValue(),
                takeProfit.doubleValue(),
                entryPrice.doubleValue(),
                -1,
                direction,
                null,
                null,
                OrderStatus.PENDING);
    }
}
