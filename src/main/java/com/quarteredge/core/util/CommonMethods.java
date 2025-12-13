package com.quarteredge.core.util;

import static com.quarteredge.core.util.Constants.QUARTER_LEVEL_INCREMENT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** CommonMethods class. */
public class CommonMethods {
    /**
     * Gets the quarter levels in the range.
     *
     * @param min the minimum value.
     * @param max the maximum value.
     * @param increment the increment value.
     * @return the list of quarter levels.
     */
    public static List<Double> getQuarterLevelsInRange(
            final double min, final double max, final double increment) {
        List<Double> levels = new ArrayList<>();
        BigDecimal newMin = new BigDecimal(min).setScale(2, RoundingMode.DOWN);
        BigDecimal newMax = new BigDecimal(max).setScale(2, RoundingMode.DOWN);
        BigDecimal newIncrement = new BigDecimal(increment).setScale(2, RoundingMode.DOWN);
        BigDecimal level = newIncrement.multiply(new BigDecimal(QUARTER_LEVEL_INCREMENT));
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
