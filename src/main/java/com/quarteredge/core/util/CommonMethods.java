package com.quarteredge.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CommonMethods {
    public List<Double> getQuarterLevelsInRange(double min, double max, double increment) {
        List<Double> levels = new ArrayList<>();
        BigDecimal newMin = new BigDecimal(min).setScale(2, RoundingMode.DOWN);
        BigDecimal newMax = new BigDecimal(max).setScale(2, RoundingMode.DOWN);
        BigDecimal newIncrement = new BigDecimal(increment).setScale(2, RoundingMode.DOWN);
        BigDecimal level = newIncrement.multiply(new BigDecimal(25));
        for (BigDecimal i = newMin;
                i.doubleValue() <= newMax.doubleValue();
                i = i.add(newIncrement)) {
            if (i.divideAndRemainder(level)[1].doubleValue() == 0) {
                levels.add(i.doubleValue());
            }
        }
        return levels;
    }
}
