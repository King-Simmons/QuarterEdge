package com.quarteredge.core.service;

import static com.quarteredge.core.util.Constants.LOSS_IDX;
import static com.quarteredge.core.util.Constants.LOSS_SUM_IDX;
import static com.quarteredge.core.util.Constants.MAE_SUM_IDX;
import static com.quarteredge.core.util.Constants.MFE_SUM_IDX;
import static com.quarteredge.core.util.Constants.WIN_IDX;
import static com.quarteredge.core.util.Constants.WIN_SUM_IDX;

import com.quarteredge.core.model.Direction;
import com.quarteredge.core.model.OrderDTO;
import com.quarteredge.core.model.OrderStatus;
import java.util.List;

/**
 * PerformanceService class.
 *
 * <p>This class is responsible for calculating the performance metrics of a backtesting session.
 *
 * @author King Simmons
 * @version 1.0
 * @since 0.3.0
 */
public class PerformanceService {
    /**
     * The list of sessions that contains orders for each session to calculate performance metrics
     * for.
     */
    private final List<List<OrderDTO>> sessions;

    /**
     * Constructs a new PerformanceService with the specified list of orders.
     *
     * @param sessions the list of sessions with orders to calculate performance metrics for
     */
    public PerformanceService(final List<List<OrderDTO>> sessions) {
        this.sessions = sessions;
    }

    /**
     * Calculates the performance metrics of the backtesting session.
     *
     * @return the performance metrics of the backtesting session
     */
    public String calculatePerformance() {
        if (sessions.isEmpty()) {
            return "No sessions to calculate performance metrics.";
        }
        var wins = 0.0;
        var losses = 0.0;

        var winSum = 0.0;
        var lossSum = 0.0;
        var mfeSum = 0.0;
        var maeSum = 0.0;

        for (List<OrderDTO> session : sessions) {
            var winsAndLosses = getWinsAndLosses(session);
            wins += winsAndLosses[WIN_IDX];
            losses += winsAndLosses[LOSS_IDX];

            winSum += winsAndLosses[WIN_SUM_IDX];
            lossSum += winsAndLosses[LOSS_SUM_IDX];
            mfeSum += winsAndLosses[MFE_SUM_IDX];
            maeSum += winsAndLosses[MAE_SUM_IDX];
        }

        var winRate = getWinRate(wins, losses);
        var mfeAverage = mfeSum / (wins + losses);
        var maeAverage = maeSum / (wins + losses);
        var winAverage = winSum / wins;
        var lossAverage = lossSum / losses;
        var expectancy = getExpectancy(wins, losses, winSum, lossSum);

        return String.format(
                """
                Wins: %.0f
                Losses: %.0f
                Win Rate: %.2f%%
                Avg Win R: %.2f
                Avg Loss R: %.2f
                Avg MFE: %.2f
                Avg MAE: %.2f
                Expectancy: %.2f
                """,
                wins, losses, winRate, winAverage, lossAverage, mfeAverage, maeAverage, expectancy);
    }

    /**
     * Retrieves the number of wins and losses from the list of orders.
     *
     * @param orders list of orders from the session
     * @return an array containing the number of wins and losses
     */
    private double[] getWinsAndLosses(final List<OrderDTO> orders) {
        var wins = 0;
        var losses = 0;
        double winRSum = 0;
        double lossRSum = 0;
        double mfeSum = 0;
        double maeSum = 0;

        for (OrderDTO order : orders) {
            if (order.status() == OrderStatus.CLOSED_CANCELED) {
                continue;
            }
            var res =
                    order.direction() == Direction.BUY
                            ? order.closePrice() - order.entry()
                            : order.entry() - order.closePrice();
            var mfe =
                    order.direction() == Direction.BUY
                            ? Math.min(order.orderStatsDTO().getMaximumFavorablePrice(), order.TP())
                                    - order.entry()
                            : order.entry()
                                    - Math.max(
                                            order.orderStatsDTO().getMaximumFavorablePrice(),
                                            order.TP());

            var mae =
                    order.direction() == Direction.BUY
                            ? Math.max(order.orderStatsDTO().getMaximumAdversePrice(), order.SL())
                                    - order.entry()
                            : order.entry()
                                    - Math.min(
                                            order.orderStatsDTO().getMaximumAdversePrice(),
                                            order.SL());
            var risk =
                    order.direction() == Direction.BUY
                            ? order.entry() - order.SL()
                            : order.SL() - order.entry();
            var r = res / risk;

            mfeSum += mfe;
            maeSum += mae;

            if (res > 0) {
                wins++;
                winRSum += r;
            } else {
                losses++;
                lossRSum += r;
            }
        }
        return new double[] {wins, losses, winRSum, lossRSum, mfeSum, maeSum};
    }

    /**
     * Retrieves the expectancy of the backtesting session.
     *
     * @param wins the number of wins
     * @param losses the number of losses
     * @param winRSum the sum of the win ratios
     * @param lossRSum the sum of the loss ratios
     * @return the expectancy of the backtesting session
     */
    private double getExpectancy(
            final double wins, final double losses, final double winRSum, final double lossRSum) {
        var winRate = getWinRate(wins, losses);
        var lossRate = getLossRate(wins, losses);
        var winAverage = winRSum / wins;
        var lossAverage = lossRSum / losses;
        return (winRate * winAverage) - (lossRate * lossAverage * -1);
    }

    /**
     * Calculates the win rate of the backtesting session.
     *
     * @param wins the number of wins
     * @param losses the number of losses
     * @return the win rate of the backtesting session
     */
    private double getWinRate(final double wins, final double losses) {
        return wins / (wins + losses);
    }

    /**
     * Calculates the loss rate of the backtesting session.
     *
     * @param wins the number of wins
     * @param losses the number of losses
     * @return the loss rate of the backtesting session
     */
    private double getLossRate(final double wins, final double losses) {
        return losses / (wins + losses);
    }
}
