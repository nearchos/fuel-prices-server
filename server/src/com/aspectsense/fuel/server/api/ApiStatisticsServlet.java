/*
 * This file is part of the Cyprus Fuel Guide server.
 *
 * The Cyprus Fuel Guide server is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Cyprus Fuel Guide server is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cyprus Fuel Guide. If not, see <http://www.gnu.org/licenses/>.
 */

package com.aspectsense.fuel.server.api;

import com.aspectsense.fuel.server.data.DailySummary;
import com.aspectsense.fuel.server.data.FuelType;
import com.aspectsense.fuel.server.data.TimestampedPrices;
import com.aspectsense.fuel.server.datastore.ApiKeyFactory;
import com.aspectsense.fuel.server.datastore.DailySummaryFactory;
import com.aspectsense.fuel.server.json.DailySummaryParser;
import com.aspectsense.fuel.server.json.StatisticsParser;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         19/03/2017
 */
public class ApiStatisticsServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger("cyprusfuelguide");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final String key = request.getParameter("key");
        if(key == null || key.isEmpty()) {
            response.getWriter().println(" { \"status\": \"error\", \"message\": \"undefined  or empty key\" }");
        } else if(!ApiKeyFactory.isActive(key)) {
            response.getWriter().println(" { \"status\": \"error\", \"message\": \"invalid key\" }");
        } else {
            try {
                response.getWriter().println(getStatisticsMessageAsJSON()); // compute reply
            } catch (JSONException jsone) {
                log.warning("JSON error: " + jsone.getMessage());
                response.getWriter().println(" { \"status\": \"error\", \"message\": \"" + jsone.getMessage() + "\" }");
            }
        }
    }

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final long ONE_DAY = 24L * 60 * 60 * 1000L;
    public static final int MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD = 30;

    public static final String SYNC_NAMESPACE = "statistics";

    private String getStatisticsMessageAsJSON() throws JSONException {

        final long now = System.currentTimeMillis();
        final String today = SIMPLE_DATE_FORMAT.format(new Date(now));
        final String memcacheKey = "statistics-" + today;

        // first check if the requested data is already in memcache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService(SYNC_NAMESPACE);
        if(memcacheService.contains(memcacheKey)) {
            return (String) memcacheService.get(memcacheKey);
        } else { // key not found in memcache - data must be dynamically generated now (and stored in cache at the end)
            final Map<String,String> dailySummariesAsJson = new HashMap<>();
            long currentTimestamp = now;
            for(int i = 0; i < MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD; i++) {
                final String dateS = SIMPLE_DATE_FORMAT.format(new Date(currentTimestamp));
                final String dailySummaryAsJson = getDailySummaryAsJson(memcacheService, dateS);
                if(dailySummaryAsJson != null) {
                    dailySummariesAsJson.put(dateS, dailySummaryAsJson);
                }
                currentTimestamp += ONE_DAY;
            }

            final String statisticsMessageAsJson = createStatisticsMessageAsJson(dailySummariesAsJson, today);

            memcacheService.put(memcacheKey, statisticsMessageAsJson); // store in memcache

            return statisticsMessageAsJson;
        }
    }

    private String getDailySummaryAsJson(final MemcacheService memcacheService, final String dateS) {
        final String dailySummaryAsJson;
        final String memcacheKey = "daily-" + dateS;
        if(memcacheService.contains(memcacheKey)) {
            return (String) memcacheService.get(memcacheKey);
        } else {
            final DailySummary dailySummary = DailySummaryFactory.getDailySummary(dateS);
            if(dailySummary == null) {
                return null;
            } else {
                dailySummaryAsJson = dailySummary.getJson();
                memcacheService.put(memcacheKey, dailySummaryAsJson);
                return dailySummaryAsJson;
            }
        }
    }

    private String createStatisticsMessageAsJson(final Map<String,String> dailySummariesAsJson, final String today)
            throws JSONException {

        final long start = System.currentTimeMillis();

        final Set<String> allStations = new HashSet<>();
        final Map<String, Map<String, Integer[]>> datesToStationsToPrices = new HashMap<>();
        for(final String dateS : dailySummariesAsJson.keySet()) {
            final Map<String, Integer[]> stationsToPrices = DailySummaryParser.fromDailySummaryJson(dailySummariesAsJson.get(dateS));
            allStations.addAll(stationsToPrices.keySet());
            datesToStationsToPrices.put(dateS, stationsToPrices);
        }

        final Vector<String> sortedDates = new Vector<>(dailySummariesAsJson.keySet());
        Collections.sort(sortedDates);

        // the generated message will have for each station and each fuel type...
        // stationCode -> timestamps: [date-1, date-2, date-3, ..., date-N], prices: [price-1, price-2, price-3, ..., price-N]
        final Map<String, Map<FuelType, TimestampedPrices>> stationsToFuelTypeToTimestampedPricesMap = new HashMap<>();
        for(final String station : allStations) {
            final Integer [] lastPrices = datesToStationsToPrices.get(sortedDates.get(0)).get(station);
            final Map<FuelType, TimestampedPrices> fuelTypeToTimestampedPricesMap = new HashMap<>();
            for(int j = 0; j < FuelType.ALL_FUEL_TYPES.length; j++) {
                final FuelType fuelType = FuelType.ALL_FUEL_TYPES[j];
                final Integer [] currentPrices = datesToStationsToPrices.get(sortedDates.get(0)).get(station);
                final TimestampedPrices timestampedPrices = new TimestampedPrices(sortedDates.get(0), currentPrices[j]);
                for(int i = 1; i < sortedDates.size(); i++) {
                    if(!currentPrices[j].equals(lastPrices[j])) {
                        timestampedPrices.add(sortedDates.get(i), currentPrices[j]);
                        lastPrices[j] = currentPrices[j];
                    }
                }
                fuelTypeToTimestampedPricesMap.put(fuelType, timestampedPrices);
            }
            stationsToFuelTypeToTimestampedPricesMap.put(station, fuelTypeToTimestampedPricesMap);
        }

        final long end = System.currentTimeMillis();
        final String duration = String.format(Locale.US, "%.2f seconds", (end - start) / 1000d);

        return StatisticsParser.toStatisticsJson(stationsToFuelTypeToTimestampedPricesMap, duration, today);
    }
}