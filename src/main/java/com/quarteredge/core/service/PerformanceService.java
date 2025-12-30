package com.quarteredge.core.service;

import static com.quarteredge.core.util.Constants.LOSS_IDX;
import static com.quarteredge.core.util.Constants.LOSS_SUM_IDX;
import static com.quarteredge.core.util.Constants.MAE_SUM_IDX;
import static com.quarteredge.core.util.Constants.MFE_SUM_IDX;
import static com.quarteredge.core.util.Constants.RISK_PER_TRADE;
import static com.quarteredge.core.util.Constants.STARTING_BALANCE;
import static com.quarteredge.core.util.Constants.TRADING_DAYS;
import static com.quarteredge.core.util.Constants.WIN_IDX;
import static com.quarteredge.core.util.Constants.WIN_SUM_IDX;

import com.quarteredge.core.model.Direction;
import com.quarteredge.core.model.OrderDTO;
import com.quarteredge.core.model.OrderStatus;
import java.util.ArrayList;
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
        var streaks = getWinAndLossStreaks();
        var maxWinStreak = streaks[0];
        var maxLossStreak = streaks[1];
        var maxDD = getMaxDrawdown();
        var sharpeRatio = getSharpRatio();

        return String.format(
                """
                Wins: %.0f
                Losses: %.0f
                Win Rate: %.2f%%
                Avg Win R: %.2f
                Avg Loss R: %.2f
                Avg MFE: %.2f
                Avg MAE: %.2f
                Max Win Streak :  %d
                Max Loss Streak : %d
                Max DrawDown : %.2f
                Sharpe Ratio: %.2f
                Expectancy: %.2f
                """,
                wins,
                losses,
                winRate,
                winAverage,
                lossAverage,
                mfeAverage,
                maeAverage,
                maxWinStreak,
                maxLossStreak,
                maxDD,
                sharpeRatio,
                expectancy);
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
        var winRSum = 0.0;
        var lossRSum = 0.0;
        var mfeSum = 0.0;
        var maeSum = 0.0;

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

    /**
     * Calculates the longest streaks of consecutive wins and losses from a collection of trading
     * sessions.
     *
     * <p>The method iterates through the list of sessions and their respective orders to compute
     * the maximum number of consecutive profitable trades (win streak) and the maximum number of
     * consecutive losing trades (loss streak). The results of trades are determined based on the
     * direction of the trade and price differences between the entry price and close price. Orders
     * with the status of {@code CLOSED_CANCELED} are skipped.
     *
     * @return an array of two integers where: - The first element is the maximum win streak. - The
     *     second element is the maximum loss streak.
     */
    private int[] getWinAndLossStreaks() {
        var winStreak = 0;
        var maxWinStreak = 0;
        var lossStreak = 0;
        var maxLossStreak = 0;
        var prevResult = 0.0;

        for (List<OrderDTO> session : sessions) {
            for (OrderDTO order : session) {
                if (order.status() == OrderStatus.CLOSED_CANCELED) {
                    continue;
                }
                var res =
                        order.direction() == Direction.BUY
                                ? order.closePrice() - order.entry()
                                : order.entry() - order.closePrice();

                if (res >= 0) {
                    if (prevResult >= 0) {
                        winStreak++;
                    } else {
                        winStreak = 1;
                    }
                    maxWinStreak = Math.max(winStreak, maxWinStreak);
                } else {
                    if (prevResult < 0) {
                        lossStreak++;
                    } else {
                        lossStreak = 1;
                    }
                    maxLossStreak = Math.max(lossStreak, maxLossStreak);
                }
                prevResult = res;
            }
        }

        return new int[] {maxWinStreak, maxLossStreak};
    }

    /**
     * Calculates the maximum drawdown (MaxDD) from a series of trading sessions. MaxDD is the
     * largest peak-to-trough decline in equity during the trading sessions, which helps evaluate
     * the risk of the trading strategy. The method iterates through multiple sessions and their
     * respective orders, updating the current equity based on the order's direction, entry price,
     * close price, and stop-loss. Closed or canceled orders are skipped. The maximum drawdown is
     * calculated as the difference between the equity peak and the current equity at any point
     * during the session.
     *
     * @return the maximum drawdown as a double value
     */
    private double getMaxDrawdown() {
        var maxDD = 0.0;
        var peak = STARTING_BALANCE;
        var currEquity = STARTING_BALANCE;

        for (List<OrderDTO> session : sessions) {
            for (OrderDTO order : session) {
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

                currEquity += r * (currEquity * RISK_PER_TRADE);
                peak = Math.max(peak, currEquity);
                maxDD = Math.max((peak - currEquity), maxDD);
            }
        }

        return maxDD;
    }

    /**
     * Calculates the Sharpe Ratio for the backtesting session based on the daily returns of trading
     * sessions. The Sharpe Ratio is a measure of risk-adjusted return, comparing the return of an
     * investment to its risk. It is calculated as the average daily return divided by the standard
     * deviation of daily returns, adjusted for the annualization factor.
     *
     * @return the calculated Sharpe Ratio as a double value
     */
    private double getSharpRatio() {
        var sharpeRatio = 0.0;
        var currEquity = STARTING_BALANCE;
        var dailyReturns = new ArrayList<Double>();
        for (List<OrderDTO> session : sessions) {
            var returns = 0.0;
            for (OrderDTO order : session) {
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

                returns = (r * RISK_PER_TRADE);
                currEquity += r * (currEquity * RISK_PER_TRADE);
            }
            dailyReturns.add(returns);
        }
        var averageDailyReturn =
                dailyReturns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        var excessDailyReturns =
                dailyReturns.stream()
                        .mapToDouble(Double::doubleValue)
                        .map(k -> Math.pow(k - averageDailyReturn, 2));
        var sumOfExcess = excessDailyReturns.sum();
        var divideOfExcess = sumOfExcess / (dailyReturns.size() - 1);
        var dailyStdDev = Math.sqrt(divideOfExcess);
        sharpeRatio = (averageDailyReturn / dailyStdDev) * Math.sqrt(TRADING_DAYS);

        return sharpeRatio;
    }
}
