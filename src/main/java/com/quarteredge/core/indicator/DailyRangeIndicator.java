package com.quarteredge.core.indicator;

import com.quarteredge.core.model.CandleDTO;
import com.quarteredge.core.model.DailyRangeDTO;

public class DailyRangeIndicator implements Indicator {
    private boolean isActive;
    private double drHigh;
    private double drLow;
    private double idrHigh;
    private double idrLow;
    private DailyRangeDTO dailyRangeDTO;

    public DailyRangeIndicator() {
        this.isActive = false;
        this.dailyRangeDTO = new DailyRangeDTO(false, -1, -1, -1, -1);
    }

    public void add(CandleDTO data) {
        if (isActive) {
            return;
        }
        if (data.time().equals("09:30:00")) {
            drHigh = Math.max(drHigh, data.high());
            drLow = Math.min(drLow, data.low());
            idrHigh = Math.max(idrHigh, data.close());
            idrLow = Math.min(idrLow, data.close());
        }

        drHigh = Math.max(drHigh, data.high());
        drLow = Math.min(drLow, data.low());
        idrHigh = Math.max(idrHigh, data.close());
        idrLow = Math.min(idrLow, data.close());

        if (data.time().equals("10:25:00")) {
            isActive = true;
            dailyRangeDTO = new DailyRangeDTO(isActive, drHigh, drLow, idrHigh, idrLow);
        }
    }

    public DailyRangeDTO get() {
        return dailyRangeDTO;
    }
}
