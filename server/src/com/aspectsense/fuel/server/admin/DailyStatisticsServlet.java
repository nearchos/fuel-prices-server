package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.DailySummary;
import com.aspectsense.fuel.server.data.FuelType;
import com.aspectsense.fuel.server.datastore.DailyStatisticsFactory;
import com.aspectsense.fuel.server.datastore.DailySummaryFactory;
import com.aspectsense.fuel.server.model.DailyStatistics;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class DailyStatisticsServlet extends HttpServlet {

    private static final long ONE_DAY = 24L * 60 * 60 * 1000;
    private static final long THREE_DAYS = 3L * ONE_DAY;
    private static final long TEN_DAYS = 10L * ONE_DAY;

    private static final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get the latest prices from today and compare with -1, -3 and -10 days
        final long now = System.currentTimeMillis();
        final String today = DailySummaryFactory.SIMPLE_DATE_FORMAT.format(new Date(now));
        final String minusThreeDays = DailySummaryFactory.SIMPLE_DATE_FORMAT.format(new Date(now - THREE_DAYS));
        final String minusTenDays = DailySummaryFactory.SIMPLE_DATE_FORMAT.format(new Date(now - TEN_DAYS));

        final DailySummary dailySummaryToday = DailySummaryFactory.getLatestDailySummaryOnOrBeforeBeforeDate(today);
        final DailySummary dailySummaryMinusThreeDays = DailySummaryFactory.getLatestDailySummaryOnOrBeforeBeforeDate(minusThreeDays);
        final DailySummary dailySummaryMinusTenDays = DailySummaryFactory.getLatestDailySummaryOnOrBeforeBeforeDate(minusTenDays);

        assert dailySummaryToday != null && dailySummaryMinusThreeDays != null && dailySummaryMinusTenDays != null;

        final com.aspectsense.fuel.server.model.DailySummary dailySummaryModelToday = gson.fromJson(dailySummaryToday.getJson(), com.aspectsense.fuel.server.model.DailySummary.class);
        final com.aspectsense.fuel.server.model.DailySummary dailySummaryModelMinusThreeDays = gson.fromJson(dailySummaryMinusThreeDays.getJson(), com.aspectsense.fuel.server.model.DailySummary.class);
        final com.aspectsense.fuel.server.model.DailySummary dailySummaryModelMinusTenDays = gson.fromJson(dailySummaryMinusTenDays.getJson(), com.aspectsense.fuel.server.model.DailySummary.class);

        final DailyStatistics.DayStatistics todayDayStatistics = createStatistics(dailySummaryModelToday.getStationCodeToPricesMap());
        final DailyStatistics.DayStatistics minusThreeDaysDayStatistics = createStatistics(dailySummaryModelMinusThreeDays.getStationCodeToPricesMap());
        final DailyStatistics.DayStatistics minusTenDaysDayStatistics = createStatistics(dailySummaryModelMinusTenDays.getStationCodeToPricesMap());

        final DailyStatistics dailyStatistics = new DailyStatistics(todayDayStatistics, minusThreeDaysDayStatistics, minusTenDaysDayStatistics);
        // store
        final String json = gson.toJson(dailyStatistics);
        DailyStatisticsFactory.addDailyStatistics(today, json);
        if(request.getParameter("debug") != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(json);
        }
    }

    private static final int NUM_OF_FUEL_TYPES = FuelType.ALL_FUEL_TYPES.length;

    private DailyStatistics.DayStatistics createStatistics(final Map<String,Integer[]> stationCodeToPrices) {

        final int [] mins = new int[NUM_OF_FUEL_TYPES];
        final int [] maxs = new int[NUM_OF_FUEL_TYPES];
        final int [] counts = new int[NUM_OF_FUEL_TYPES];
        final long [] sums = new long[NUM_OF_FUEL_TYPES];

        // init
        for(int i = 0; i < mins.length; i++) mins[i] = Integer.MAX_VALUE;
        for(int i = 0; i < maxs.length; i++) maxs[i] = 0;
        for(int i = 0; i < sums.length; i++) sums[i] = 0;

        // iterate prices
        final Collection<String> allStations = stationCodeToPrices.keySet();
        for(final String station : allStations) {
            final Integer [] prices = stationCodeToPrices.get(station);
            for(int i = 0; i < NUM_OF_FUEL_TYPES; i++) {
                final int price = prices[i];
                if(price > 0 && price < 100000) { // filter our prices outside [0,100) euros
                    if(price < mins[i]) mins[i] = price;
                    if(price > maxs[i]) maxs[i] = price;
                    counts[i]++;
                    sums[i] += price;
                }
            }
        }

        // compute averages
        final double [] averages = new double[NUM_OF_FUEL_TYPES];
        for(int i = 0; i < NUM_OF_FUEL_TYPES; i++) {
            averages[i] = sums[i] * 1d / counts[i];
        }

        final int [] medians = new int[NUM_OF_FUEL_TYPES];
        final Vector<Integer[]> priceValues = new Vector<>(stationCodeToPrices.values());
        for(int i = 0; i < medians.length; i++) {
            final int selectedIndex = i;
            priceValues.sort((prices1, prices2) -> prices1[selectedIndex].compareTo(prices2[selectedIndex]));
            medians[i] = getMedian(priceValues, selectedIndex);
        }

        return new DailyStatistics.DayStatistics(mins, maxs, medians, averages);
    }

    private Integer getMedian(final Vector<Integer[]> priceValues, final int index) {
        final Integer median;
        final int size = priceValues.size();
        if(size % 2 == 0) {
            median = (priceValues.get(size/2 - 1)[index] + priceValues.get(size/2)[index]) / 2;
        } else {
            median = priceValues.get(size/2)[index];
        }
        return median;
    }
}