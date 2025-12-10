package com.quarteredge.core.indicator;

import static com.quarteredge.core.util.Constants.RDR_SESSION_END_TIME;
import static com.quarteredge.core.util.Constants.RDR_SESSION_START_TIME;

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
        if (data.time().isBefore(RDR_SESSION_START_TIME)) {
            this.isActive = false;
        }
        if (isActive) {
            return;
        }
        if (data.time().isAfter(RDR_SESSION_START_TIME)
                && data.time().isBefore(RDR_SESSION_END_TIME)) {
            drHigh = Math.max(drHigh, data.high());
            drLow = Math.min(drLow, data.low());
            idrHigh = Math.max(idrHigh, data.close());
            idrLow = Math.min(idrLow, data.close());
        }

        if (data.time().equals(RDR_SESSION_END_TIME) || data.time().isAfter(RDR_SESSION_END_TIME)) {
            isActive = true;
            dailyRangeDTO = new DailyRangeDTO(true, drHigh, drLow, idrHigh, idrLow);
        }
    }

    public DailyRangeDTO get() {
        return dailyRangeDTO;
    }
}
