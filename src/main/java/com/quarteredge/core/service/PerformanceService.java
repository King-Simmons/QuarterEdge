package com.quarteredge.core.service;

import static com.quarteredge.core.util.Constants.LOSS_IDX;
import static com.quarteredge.core.util.Constants.LOSS_SUM_IDX;
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
    /** The list of orders to calculate performance metrics for. */
    private final List<OrderDTO> orders;

    /**
     * Constructs a new PerformanceService with the specified list of orders.
     *
     * @param orders the list of orders to calculate performance metrics for
     */
    public PerformanceService(final List<OrderDTO> orders) {
        this.orders = orders;
    }

    /** Calculates the performance metrics of the backtesting session. */
    public void calculatePerformance() {
        if (orders.isEmpty()) {
            IO.println("No orders to calculate performance metrics.");
            return;
        }
        var winsAndLosses = getWinsAndLosses();
        var wins = winsAndLosses[WIN_IDX];
        var losses = winsAndLosses[LOSS_IDX];
        var winRate = getWinRate(wins, losses);
        var winSum = winsAndLosses[WIN_SUM_IDX];
        var lossSum = winsAndLosses[LOSS_SUM_IDX];
        var winAverage = winSum / wins;
        var lossAverage = lossSum / losses;
        var expectancy = getExpectancy(wins, losses, winSum, lossSum);

        IO.println(
                String.format(
                        """
                        Wins: %.0f
                        Losses: %.0f
                        Win Rate: %.2f%%
                        Avg Win R: %.2f
                        Avg Loss R: %.2f
                        Expectancy: %.2f
                        """,
                        wins, losses, winRate, winAverage, lossAverage, expectancy));
    }

    /**
     * Retrieves the number of wins and losses from the list of orders.
     *
     * @return an array containing the number of wins and losses
     */
    private double[] getWinsAndLosses() {
        var wins = 0;
        var losses = 0;
        double winRSum = 0;
        double lossRSum = 0;
        for (OrderDTO order : orders) {
            if (order.status() == OrderStatus.CLOSED_CANCELED) {
                continue;
            }
            var res =
                    order.direction() == Direction.BUY
                            ? order.closePrice() - order.entry()
                            : order.entry() - order.closePrice();
            var risk =
                    order.direction() == Direction.BUY
                            ? order.entry() - order.SL()
                            : order.SL() - order.entry();
            var r = res / risk;

            if (res > 0) {
                wins++;
                winRSum += r;
            } else {
                losses++;
                lossRSum += r;
            }
        }
        return new double[] {wins, losses, winRSum, lossRSum};
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
