package com.quarteredge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quarteredge.core.util.CommonMethods;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test class for CommonMethods. */
public class CommonMethodsTest {

    /** Test getQuarterLevelsInRange method. */
    @Test
    @DisplayName("testGetQuarterLevelsInRange() should get correct levels in range.")
    public void testGetQuarterLevelsInRange() {
        CommonMethods commonMethods = new CommonMethods();
        List<Double> levels = commonMethods.getQuarterLevelsInRange(1, 10, 0.01);
        assertEquals(37, levels.size());
    }
}
