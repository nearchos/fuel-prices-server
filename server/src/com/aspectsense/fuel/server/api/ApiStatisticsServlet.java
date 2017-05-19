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

import com.aspectsense.fuel.server.data.*;
import com.aspectsense.fuel.server.datastore.ApiKeyFactory;
import com.aspectsense.fuel.server.datastore.DailySummaryFactory;
import com.aspectsense.fuel.server.datastore.StationsFactory;
import com.aspectsense.fuel.server.json.DailySummaryBundle;
import com.aspectsense.fuel.server.json.DailySummaryParser;
import com.aspectsense.fuel.server.json.StationsParser;
import com.aspectsense.fuel.server.json.StatisticsParser;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

import static com.aspectsense.fuel.server.json.Util.SIMPLE_DATE_FORMAT;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         19/03/2017
 */
public class ApiStatisticsServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static final long ONE_DAY = 24L * 60 * 60 * 1000L;
    public static final int DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD = 30;
    public static final int MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD = 365;

    public static final String SYNC_NAMESPACE = "statistics";

    /**
     * Supports these URIs/types of requests:
     * <ol>
     *     <li>/api/statistics-daily?key=...&date=2017-05-14</li>
     *     <li>/api/statistics-period?key=...&from=2017-04-15&to=2017-05-14</li>
     *     <li>/api/statistics?key=... -> defaults to /api/statistics-period?key=...&from=DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD-ago&to=yesterday</li>
     * </ol>
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final String key = request.getParameter("key");
        final String durationS = request.getParameter("duration");
        int duration = DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD;
        try {
            duration = Integer.parseInt(durationS);
        } catch (NumberFormatException nfe) {
            duration = DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD;
            log.warning(nfe.getMessage());
        }
        if(duration < DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD) {
            duration = DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD;
            log.warning("Duration cannot be less than: DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD=" + DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD);
        }
        if(duration > MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD) {
            duration = MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD;
            log.warning("Exceeded max limit od duration: MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD=" + MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD);
        }
        final String city = request.getParameter("city");
        final String district = request.getParameter("district");
        if(key == null || key.isEmpty()) {
            response.getWriter().println(" { \"status\": \"error\", \"message\": \"undefined  or empty key\" }");
        } else if(!ApiKeyFactory.isActive(key)) {
            response.getWriter().println(" { \"status\": \"error\", \"message\": \"invalid key\" }");
        } else {
//            try {
            final String responseMessage;
            if(district != null) {
                responseMessage = getStatisticsMessageByDistrictAsJSON(duration, district);
            } else if(city != null) {
                responseMessage = getStatisticsMessageByCityAsJSON(duration, city);
            } else {
                responseMessage = getStatisticsMessageAsJSON(duration); // compute reply
            }
            response.getWriter().println(responseMessage);
//            } catch (JSONException jsone) {
//                log.warning("JSON error: " + jsone.getMessage());
//                response.getWriter().println(" { \"status\": \"error\", \"message\": \"" + jsone.getMessage() + "\" }");
//            }
        }
    }

    private String getStatisticsMessageAsJSON() throws JSONException {

        final long now = System.currentTimeMillis();
        final String to = SIMPLE_DATE_FORMAT.format(new Date(now));
        String from = to;
        final String memcacheKey = "statistics-" + to;

        // first check if the requested data is already in memcache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService(SYNC_NAMESPACE);
        if(memcacheService.contains(memcacheKey)) {
            return (String) memcacheService.get(memcacheKey);
        } else { // key not found in memcache - data must be dynamically generated now (and stored in cache at the end)
            final Map<String,String> dailySummariesAsJson = new HashMap<>();
            long currentTimestamp = now;
            for(int i = 0; i < DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD; i++) {
                final String dateS = SIMPLE_DATE_FORMAT.format(new Date(currentTimestamp));
                final String dailySummaryAsJson = getDailySummaryAsJson(memcacheService, dateS);
                if(dailySummaryAsJson != null) {
                    dailySummariesAsJson.put(dateS, dailySummaryAsJson);
                }
                from = dateS;
                currentTimestamp -= ONE_DAY;
            }

            final String statisticsMessageAsJson = createStatisticsMessageAsJson(from, to, dailySummariesAsJson, getAllStationCodes());

            memcacheService.put(memcacheKey, statisticsMessageAsJson); // store in memcache

            return statisticsMessageAsJson;
        }
    }

    static private Set<String> getAllStationCodes() {
        final Stations stations = StationsFactory.getLatestStations();
        final Vector<Station> allStations = stations != null ? StationsParser.fromStationsJson(stations.getJson()) : new Vector<Station>();
        final HashSet<String> allStationCodes = new HashSet<>();
        for(final Station station : allStations) {
            allStationCodes.add(station.getStationCode());
        }
        return allStationCodes;
    }

    static private Set<String> filterStationCodesByCity(final String city) {
        final Stations stations = StationsFactory.getLatestStations();
        final Vector<Station> allStations = stations != null ? StationsParser.fromStationsJson(stations.getJson()) : new Vector<Station>();
        final HashSet<String> filteredStationCodes = new HashSet<>();
        for(final Station station : allStations) {
            if(station.getStationCity().equals(city)) {
                filteredStationCodes.add(station.getStationCode());
            }
        }
        return filteredStationCodes;
    }

    static private Set<String> filterStationCodesByDistrict(final String district) {
        final Stations stations = StationsFactory.getLatestStations();
        final Vector<Station> allStations = stations != null ? StationsParser.fromStationsJson(stations.getJson()) : new Vector<Station>();
        final HashSet<String> filteredStationCodes = new HashSet<>();
        for(final Station station : allStations) {
            if(station.getStationDistrict().equals(district)) {
                filteredStationCodes.add(station.getStationCode());
            }
        }
        return filteredStationCodes;
    }

    /**
     * Returns a JSON-formatted string of the statistics message relevant to the specified dates and filtered stations.
     * @param duration number of days to cover, starting from today and going backwards (must be between
     * {@link @DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD} and {@link @MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD}
     * @return a JSON-formatted string of the statistics message relevant to the specified dates and filtered stations
     * @throws IOException if a parsing error occurs
     */
    static String getStatisticsMessageAsJSON(final int duration) throws IOException {
        return getStatisticsMessageAsJSON(duration, getAllStationCodes());
    }

    /**
     * Returns a JSON-formatted string of the statistics message relevant to the specified dates and filtered stations.
     * @param duration number of days to cover, starting from today and going backwards (must be between
     * {@link @DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD} and {@link @MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD}
     * @param city the city by which the stations are filtered, e.g. 'ΛΕΜΕΣΟΣ'
     * @return a JSON-formatted string of the statistics message relevant to the specified dates and filtered stations
     * @throws IOException if a parsing error occurs
     */
    static String getStatisticsMessageByCityAsJSON(final int duration, final String city) throws IOException {
        return getStatisticsMessageAsJSON(duration, filterStationCodesByCity(city));
    }

    /**
     * Returns a JSON-formatted string of the statistics message relevant to the specified dates and filtered stations.
     * @param duration number of days to cover, starting from today and going backwards (must be between
     * {@link @DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD} and {@link @MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD}
     * @param district the city by which the stations are filtered, e.g. 'Παραμύθα'
     * @return a JSON-formatted string of the statistics message relevant to the specified dates and filtered stations
     * @throws IOException if a parsing error occurs
     */
    static String getStatisticsMessageByDistrictAsJSON(final int duration, final String district) throws IOException {
        return getStatisticsMessageAsJSON(duration, filterStationCodesByDistrict(district));
    }

    /**
     *
     * @param duration number of days to cover, starting from today and going backwards (must be between
     * {@link @DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD} and {@link @MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD}
     * @param selectedStationIds
     * @return a JSON-formatted string of the statistics message relevant to the specified dates and filtered stations
     * @throws IOException
     */
    private static String getStatisticsMessageAsJSON(final int duration, final Set<String> selectedStationIds) throws IOException {

        final Map<String,String> dailySummariesAsJson = new HashMap<>();
        // we know that duration in in [DEFAULT_NUM_OF_DAYS_IN_STATISTICS_PERIOD, MAX_NUM_OF_DAYS_IN_STATISTICS_PERIOD]
        final long toTimestamp = System.currentTimeMillis();
        final String to = SIMPLE_DATE_FORMAT.format(new Date(toTimestamp));
        final long fromTimestamp = toTimestamp - duration * ONE_DAY;
        final String from = SIMPLE_DATE_FORMAT.format(new Date(fromTimestamp));
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        for(long d = fromTimestamp; d <= toTimestamp; d+=ONE_DAY) {
            final String dateS = SIMPLE_DATE_FORMAT.format(new Date(d));
            final String dailySummaryAsJson = getDailySummaryAsJson(memcacheService, dateS);
            if(dailySummaryAsJson != null) {
                dailySummariesAsJson.put(dateS, dailySummaryAsJson);
            }
        }

        try {
            return createStatisticsMessageAsJson(from, to, dailySummariesAsJson, selectedStationIds);
        } catch (JSONException jsone) {
            throw new IOException(jsone.getMessage());
        }
    }

    private static String getDailySummaryAsJson(final MemcacheService memcacheService, final String dateS) {
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

    private static String createStatisticsMessageAsJson(final String from, final String to, final Map<String,String> datesToDailySummariesAsJsonMap, final Set<String> selectedStationCodes)
            throws JSONException {

        final long start = System.currentTimeMillis();

        final Set<String> allStations = new HashSet<>();
        final Map<String, Map<String, Integer[]>> datesToStationsToPrices = new HashMap<>();
        final Map<String, Double> datesToCrudeOilPriceInUsd = new HashMap<>();
        final Map<String, Double> datesToEurUsd = new HashMap<>();
        final Map<String, Double> datesToEurGbp = new HashMap<>();
        for(final String dateS : datesToDailySummariesAsJsonMap.keySet()) {
            final DailySummaryBundle dailySummaryBundle = DailySummaryParser.fromDailySummaryJson(datesToDailySummariesAsJsonMap.get(dateS));
            datesToCrudeOilPriceInUsd.put(dateS, dailySummaryBundle.getCrudeOilPriceInUSD());
            datesToEurUsd.put(dateS, dailySummaryBundle.getEurToUsd());
            datesToEurGbp.put(dateS, dailySummaryBundle.getEurToGbp());
            final Map<String, Integer[]> stationsToPrices = dailySummaryBundle.getStationCodeToPricesMap();
            allStations.addAll(stationsToPrices.keySet());
            datesToStationsToPrices.put(dateS, stationsToPrices);
        }

        final Vector<String> allDatesSorted = new Vector<>(datesToDailySummariesAsJsonMap.keySet());
        Collections.sort(allDatesSorted);

        // the generated message will have for each station and each fuel type...
        // stationCode -> timestamps: [date-1, date-2, date-3, ..., date-N], prices: [price-1, price-2, price-3, ..., price-N]
        final String firstDate = allDatesSorted.get(0);
        final Map<String, Map<FuelType, TimestampedPrices>> stationsToFuelTypeToTimestampedPricesMap = new HashMap<>();
        for(final String station : allStations) {
            final Map<FuelType, TimestampedPrices> fuelTypeToTimestampedPricesMap = new HashMap<>();
            for(int fuelTypeIndex = 0; fuelTypeIndex < FuelType.ALL_FUEL_TYPES.length; fuelTypeIndex++) {
                final FuelType fuelType = FuelType.ALL_FUEL_TYPES[fuelTypeIndex];
                Integer [] lastPrices = datesToStationsToPrices.get(firstDate).get(station); // [ unleaded95Price, unleaded98Price, ..., heatingPrice]
                final TimestampedPrices timestampedPrices = new TimestampedPrices();
                if(lastPrices != null) {
                    timestampedPrices.add(firstDate, lastPrices[fuelTypeIndex]);
                } else { // lastPrices == null
                    lastPrices = new Integer[] {0, 0, 0, 0, 0};
                }
                for(int dateIndex = 1; dateIndex < allDatesSorted.size(); dateIndex++) {
                    final String selectedDate = allDatesSorted.get(dateIndex);
                    Integer[] currentPrices = datesToStationsToPrices.get(selectedDate).get(station);
                    // if null, the station is missing so reset its values to all zeros
                    if(currentPrices == null) currentPrices = new Integer[] {0, 0, 0, 0, 0};
                    if (!currentPrices[fuelTypeIndex].equals(lastPrices[fuelTypeIndex])) {
                        timestampedPrices.add(selectedDate, currentPrices[fuelTypeIndex]);
                        lastPrices[fuelTypeIndex] = currentPrices[fuelTypeIndex];
                    }
                }
                fuelTypeToTimestampedPricesMap.put(fuelType, timestampedPrices);
            }
            stationsToFuelTypeToTimestampedPricesMap.put(station, fuelTypeToTimestampedPricesMap);
        }

        // compute means, medians, mins, maxs over time
        final Map<String,Double[]> uniqueIncludedDatesToMeans = new HashMap<>(); // mean = averages
        final Map<String,Integer[]> uniqueIncludedDatesToMedians = new HashMap<>(); // medians = medians
        final Map<String,Integer[]> uniqueIncludedDatesToMins = new HashMap<>(); // mins = minimums
        final Map<String,Integer[]> uniqueIncludedDatesToMaxs = new HashMap<>(); // maxs = maximums

        // first initialize the sums and counts to 0
        final Map<String, Map<FuelType, Integer>> dateToFuelTypeToSumsMap = new HashMap<>();
        final Map<String, Map<FuelType, Integer>> dateToFuelTypeToCountsMap = new HashMap<>();
        for(final String date : datesToDailySummariesAsJsonMap.keySet()) {
            final Map<FuelType, Integer> fuelTypeToSumsMap = new HashMap<>();
            final Map<FuelType, Integer> fuelTypeToCountsMap = new HashMap<>();
            for(int i = 0; i < FuelType.ALL_FUEL_TYPES.length; i++) {
                fuelTypeToSumsMap.put(FuelType.ALL_FUEL_TYPES[i], 0);
                fuelTypeToCountsMap.put(FuelType.ALL_FUEL_TYPES[i], 0);
            }
            dateToFuelTypeToSumsMap.put(date, fuelTypeToSumsMap);
            dateToFuelTypeToCountsMap.put(date, fuelTypeToCountsMap);
        }

        // next add the sums and the counts for all dates and for all stations
        for(final String date : datesToDailySummariesAsJsonMap.keySet()) {
            final Map<String, Integer[]> stationsToPrices = datesToStationsToPrices.get(date);
            final Map<FuelType, Integer> fuelTypeToSumsMap = dateToFuelTypeToSumsMap.get(date);
            final Map<FuelType, Integer> fuelTypeToCountsMap = dateToFuelTypeToCountsMap.get(date);
            final Integer [] maximums = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
            final Integer [] minimums = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
            for(final String station : stationsToPrices.keySet()) {
                if(selectedStationCodes.contains(station)) {
                    final Integer [] prices = stationsToPrices.get(station);
                    for(int i = 0; i < FuelType.ALL_FUEL_TYPES.length; i++) {
                        if(prices[i] != 0) {
                            int sum = fuelTypeToSumsMap.get(FuelType.ALL_FUEL_TYPES[i]);
                            fuelTypeToSumsMap.put(FuelType.ALL_FUEL_TYPES[i], sum + prices[i]);
                            int count = fuelTypeToCountsMap.get(FuelType.ALL_FUEL_TYPES[i]);
                            fuelTypeToCountsMap.put(FuelType.ALL_FUEL_TYPES[i], count + 1);
                            if(prices[i] > maximums[i]) {
                                maximums[i] = prices[i];
                            }
                            if(prices[i] < minimums[i]) {
                                minimums[i] = prices[i];
                            }
                        }
                    }
                }
            }
            uniqueIncludedDatesToMaxs.put(date, maximums);
            uniqueIncludedDatesToMins.put(date, minimums);
        }

        // finally compute medians
        for(final String date : datesToStationsToPrices.keySet()) {
            final Map<String, Integer[]> stationsToPrices = datesToStationsToPrices.get(date);
            final Integer [] medians = new Integer[FuelType.ALL_FUEL_TYPES.length];
            for(int i = 0; i < FuelType.ALL_FUEL_TYPES.length; i++) {
                final Vector<Integer> prices = new Vector<>();
                for(final String station : stationsToPrices.keySet()) {
                    if(selectedStationCodes.contains(station)) {
                        prices.add(stationsToPrices.get(station)[i]);
                    }
                }
                Collections.sort(prices);
                medians[i] = prices.get(prices.size() / 2);
            }
            uniqueIncludedDatesToMedians.put(date, medians);
        }

        for(final String date : datesToDailySummariesAsJsonMap.keySet()) {
            final Map<FuelType, Integer> fuelTypeToSumsMap = dateToFuelTypeToSumsMap.get(date);
            final Map<FuelType, Integer> fuelTypeToCountsMap = dateToFuelTypeToCountsMap.get(date);
            final Double [] means = new Double[FuelType.ALL_FUEL_TYPES.length];
            for(int i = 0; i < FuelType.ALL_FUEL_TYPES.length; i++) {
                int sum = fuelTypeToSumsMap.get(FuelType.ALL_FUEL_TYPES[i]);
                int count = fuelTypeToCountsMap.get(FuelType.ALL_FUEL_TYPES[i]);
                if (count > 0) {
                    means[i] = sum * 1d / count;
                } else {
                    means[i] = 0d;
                }
            }
//            log.warning("statistics [" + date + "] -> SUMS: " + fuelTypeToSumsMap + ", COUNTS: " + fuelTypeToCountsMap);
            uniqueIncludedDatesToMeans.put(date, means);
        }

        final long end = System.currentTimeMillis();
        final String duration = String.format(Locale.US, "%.2f seconds", (end - start) / 1000d);

        return StatisticsParser.toStatisticsJson(
                datesToCrudeOilPriceInUsd,
                datesToEurUsd,
                datesToEurGbp,
                stationsToFuelTypeToTimestampedPricesMap,
                selectedStationCodes,
                uniqueIncludedDatesToMeans,
                uniqueIncludedDatesToMedians,
                uniqueIncludedDatesToMins,
                uniqueIncludedDatesToMaxs,
                duration,
                from,
                to);
    }
}