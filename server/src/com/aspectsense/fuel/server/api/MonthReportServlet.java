package com.aspectsense.fuel.server.api;

import com.aspectsense.fuel.server.data.*;
import com.aspectsense.fuel.server.datastore.DailySummaryFactory;
import com.aspectsense.fuel.server.datastore.StationsFactory;
import com.aspectsense.fuel.server.model.MonthReport;
import com.aspectsense.fuel.server.model.Station;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Logger;

public class MonthReportServlet extends HttpServlet {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    private static final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setHeader("Access-Control-Allow-Origin", "*"); // todo consider limiting to own domain
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final PrintWriter printWriter = response.getWriter();

        // check if daily reports are turned on
        final String yearS = request.getParameter("year");
        final String monthS = request.getParameter("month");
        if(yearS == null || monthS == null) {
            printWriter.println("{ \"error\": \"null parameter\", \"advise\": \"Month report requires parameters 'year' and 'month', e.g. '?year=2018&month=06'\"}");
            return;
        }

        final int year;
        final int month;
        try {
            year = Integer.parseInt(yearS);
            month = Integer.parseInt(monthS);
        } catch (NumberFormatException nfe) {
            printWriter.println("{ \"error\": \"" + nfe.getMessage() + "\", \"advise\": \"Month report requires parameters 'year' and 'month', e.g. '?year=2018&month=06'\"}");
            return;
        }

        final String memCacheKey = getMemcacheServiceKey(year, month);
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        final String json;
        if(memcacheService.contains(memCacheKey)) {
            json = (String) memcacheService.get(memCacheKey);
        } else {
            java.time.LocalDate firstDayOfMonth = java.time.LocalDate.of(year, month, 1);
            java.time.LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

            final String fromS = firstDayOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE);
            final String toS = lastDayOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE);

            final Vector<DailySummary> dailySummaries = DailySummaryFactory.getSortedDailySummariesForMonth(fromS, toS);

            final Stations latestStations = StationsFactory.getStationsByDate(lastDayOfMonth.atTime(0, 0).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L);
            assert latestStations != null;

            final MonthReport monthReport = produceMonthReport(dailySummaries, latestStations.getJson());
            json = gson.toJson(monthReport);
            // cache reply
            memcacheService.put(memCacheKey, json);
        }

        printWriter.println(json);
    }

    private static String getMemcacheServiceKey(final int year, final int month) {
        return String.format("MONTH-REPORT-%d-%d", year, month);
    }

    static MonthReport produceMonthReport(final Vector<DailySummary> dailySummariesJson, String stationsJson) {
        final Map<Integer, MonthReport.StationCodeToPrice[]> fuelTypeToStationsOrderedByPriceDescending = new HashMap<>();
        final Map<Integer, MonthReport.CityToPrice[]> fuelTypeToCitiesOrderedByPriceDescending = new HashMap<>();
        final Map<Integer, MonthReport.BrandToPrice[]> fuelTypeToBrandsOrderedByPriceDescending = new HashMap<>();

        // pre-process JSON-based daily summaries by converting to POJOs
        final Vector<com.aspectsense.fuel.server.model.DailySummary> dailySummaries = new Vector<>();
        for(final DailySummary dailySummaryJson : dailySummariesJson) {
            final com.aspectsense.fuel.server.model.DailySummary dailySummary = gson.fromJson(dailySummaryJson.getJson(), com.aspectsense.fuel.server.model.DailySummary.class);
            dailySummaries.add(dailySummary);
        }

        // get all stations
        final Station [] stations = gson.fromJson(stationsJson, Station[].class);
        final Map<String,Station> stationCodeToStationsMap = new HashMap<>();
        for(final Station station : stations) {
            stationCodeToStationsMap.put(station.getStationCode(), station);
        }

        // compute best price for each fuel type for each day
        final Map<com.aspectsense.fuel.server.model.DailySummary,Integer[]> dailyLowestPrices = new HashMap<>();
        for(final com.aspectsense.fuel.server.model.DailySummary dailySummary : dailySummaries) {
            // prepare initial array with max possible values
            final Integer [] lowestPrices = new Integer[numOfFuelTypes];
            for(int i = 0; i < lowestPrices.length; i++) lowestPrices[i] = Integer.MAX_VALUE;

            final Map<String,Integer[]> daysStationCodeToPricesMap = dailySummary.getStationCodeToPricesMap();
            if(daysStationCodeToPricesMap == null) {
                log.severe("Null dailySummary " + dailySummary.toString());
                continue;
            }
            final Set<String> stationCodes = daysStationCodeToPricesMap.keySet();
            for(final String stationCode : stationCodes) {
                final Integer [] prices = daysStationCodeToPricesMap.get(stationCode);
                // compute the best (lowest) prices for each day - update lowestPrices array
                for(int i = 0; i < numOfFuelTypes; i++) {
                    if(prices[i] > 0 && // ignore zero prices
                            prices[i] < lowestPrices[i]) lowestPrices[i] = prices[i];
                }
            }
            dailyLowestPrices.put(dailySummary, lowestPrices);
        }

        final Map<String,Integer[]> stationCodeToLowestPriceDays = new HashMap<>();

        // for each day, add up the corresponding prices for each station
        final Map<String,SumsAndCounts> stationCodesToSumsAndCountsMap = new HashMap<>();
        for(final com.aspectsense.fuel.server.model.DailySummary dailySummary : dailySummaries) {
            final Map<String,Integer[]> daysStationCodeToPricesMap = dailySummary.getStationCodeToPricesMap();
            final Set<String> stationCodes = daysStationCodeToPricesMap.keySet();
            for(final String stationCode : stationCodes) {
                final Integer [] prices = daysStationCodeToPricesMap.get(stationCode);
                if (!stationCodesToSumsAndCountsMap.containsKey(stationCode)) {
                    stationCodesToSumsAndCountsMap.put(stationCode, new SumsAndCounts());
                }
                stationCodesToSumsAndCountsMap.get(stationCode).add(prices);

                // update the num of days a station has the lowest price
                Integer [] lowestPriceOccurrences = stationCodeToLowestPriceDays.get(stationCode);
                if(lowestPriceOccurrences == null) {
                    // initially, all occurrences are zero
                    lowestPriceOccurrences = new Integer[numOfFuelTypes];
                    for(int i = 0; i < numOfFuelTypes; i++) { lowestPriceOccurrences[i] = 0; }
                }
                final Integer [] lowestPricesOfTheDay = dailyLowestPrices.get(dailySummary);
                for(int i = 0; i < numOfFuelTypes; i++) {
                    if(prices[i] > 0 // ignore zero prices
                            && prices[i].equals(lowestPricesOfTheDay[i])) {
                        lowestPriceOccurrences[i] = lowestPriceOccurrences[i] + 1;
                    }
                }
                stationCodeToLowestPriceDays.put(stationCode, lowestPriceOccurrences);
            }
        }

        // for each fuel type, compute averages for each station
        final Map<FuelType, Vector<MonthReport.StationCodeToPrice>> fuelTypeToStationCodeToPrices = new HashMap<>();
        {
            final Set<String> stationCodes = stationCodesToSumsAndCountsMap.keySet();
            for(final FuelType fuelType : FuelType.values()) {
                final Vector<MonthReport.StationCodeToPrice> stationCodeToPrices = new Vector<>(stationCodes.size());
                int i = 0;
                for(final String stationCode : stationCodes) {
                    final Station station = stationCodeToStationsMap.get(stationCode);
                    final Integer [] occurrences = stationCodeToLowestPriceDays.get(stationCode);
                    int numOfDaysRankedTop = occurrences[fuelType.getCode()-1];
                    // there is a chance the station was 'closed' or 'deleted' before the end of that month - in this case we skip the station
                    if(station != null) {
                        stationCodeToPrices.add(new MonthReport.StationCodeToPrice(
                                stationCode,
                                station.getStationName(),
                                station.getStationBrand(),
                                stationCodesToSumsAndCountsMap.get(stationCode).getAverage(fuelType),
                                numOfDaysRankedTop,
                                station.getStationCityEn()));
                    }
                }
                fuelTypeToStationCodeToPrices.put(fuelType, stationCodeToPrices);
            }
        }

        // sort and set arrays
        for(final FuelType fuelType : FuelType.values()) {
            final Vector<MonthReport.StationCodeToPrice> stationCodeToPricesVector = fuelTypeToStationCodeToPrices.get(fuelType);
            final MonthReport.StationCodeToPrice [] stationCodeToPrices = stationCodeToPricesVector.toArray(new MonthReport.StationCodeToPrice[0]);
            Arrays.sort(stationCodeToPrices);
            fuelTypeToStationsOrderedByPriceDescending.put(fuelType.getCode(), stationCodeToPrices);
        }

        // now, compute the values per city - first add up all prices, per city
        final Map<String,SumsAndCounts> cityNamesToSumsAndCountsMap = new HashMap<>();
        for(final com.aspectsense.fuel.server.model.DailySummary dailySummary : dailySummaries) {
            final Map<String,Integer[]> daysStationCodeToPricesMap = dailySummary.getStationCodeToPricesMap();
            final Set<String> stationCodes = daysStationCodeToPricesMap.keySet();
            for(final String stationCode : stationCodes) {
                final Station station = stationCodeToStationsMap.get(stationCode);
                // there is a chance the station was 'closed' or 'deleted' before the end of that month - in this case we skip the station
                if(station != null) {
                    final String cityName = station.getStationCityEn();
                    final Integer [] prices = daysStationCodeToPricesMap.get(stationCode);
                    if (!cityNamesToSumsAndCountsMap.containsKey(cityName)) {
                        cityNamesToSumsAndCountsMap.put(cityName, new SumsAndCounts());
                    }
                    cityNamesToSumsAndCountsMap.get(cityName).add(prices);
                }
            }
        }

        // then store averages in the corresponding data structures
        for(final FuelType fuelType : FuelType.values()) {
            final City [] cities = City.ALL_CITIES;
            int i = 0;
            final MonthReport.CityToPrice [] cityToPrices = new MonthReport.CityToPrice[cities.length];
            for(final City city : cities) {
                final double price = cityNamesToSumsAndCountsMap.get(city.getNameEn()).getAverage(fuelType);
                cityToPrices[i] = new MonthReport.CityToPrice(city.getNameEn(), price);
                i++;
            }
            Arrays.sort(cityToPrices);
            fuelTypeToCitiesOrderedByPriceDescending.put(fuelType.getCode(), cityToPrices);
        }

        // now, compute the values per brand - first add up all prices, per brand
        final Map<String,SumsAndCounts> brandsToSumsAndCountsMap = new HashMap<>();
        for(final com.aspectsense.fuel.server.model.DailySummary dailySummary : dailySummaries) {
            final Map<String,Integer[]> daysStationCodeToPricesMap = dailySummary.getStationCodeToPricesMap();
            final Set<String> stationCodes = daysStationCodeToPricesMap.keySet();
            for(final String stationCode : stationCodes) {
                final Station station = stationCodeToStationsMap.get(stationCode);
                // there is a chance the station was 'closed' or 'deleted' before the end of that month - in this case we skip the station
                if(station != null) {
                    final String brand = station.getStationBrand();
                    final Integer[] prices = daysStationCodeToPricesMap.get(stationCode);
                    if (!brandsToSumsAndCountsMap.containsKey(brand)) {
                        brandsToSumsAndCountsMap.put(brand, new SumsAndCounts());
                    }
                    brandsToSumsAndCountsMap.get(brand).add(prices);
                }
            }
        }

        // then store averages in the corresponding data structures
        for(final FuelType fuelType : FuelType.values()) {
            final Vector<String> brands = new Vector<>(brandsToSumsAndCountsMap.keySet());
            int i = 0;
            final MonthReport.BrandToPrice [] brandToPrices = new MonthReport.BrandToPrice[brands.size()];
            for(final String brand : brands) {
                final double price = brandsToSumsAndCountsMap.get(brand).getAverage(fuelType);
                brandToPrices[i] = new MonthReport.BrandToPrice(brand, price);
                i++;
            }
            Arrays.sort(brandToPrices);
            fuelTypeToBrandsOrderedByPriceDescending.put(fuelType.getCode(), brandToPrices);
        }

        return new MonthReport(fuelTypeToStationsOrderedByPriceDescending, fuelTypeToCitiesOrderedByPriceDescending, fuelTypeToBrandsOrderedByPriceDescending);
    }

    private static final int numOfFuelTypes = FuelType.values().length;

    static class SumsAndCounts {

        double [] sum;
        int [] count;

        SumsAndCounts() {
            this.sum = new double[numOfFuelTypes];
            this.count = new int[numOfFuelTypes];
        }

        void add(Integer [] values) {
            for(int i = 0; i < numOfFuelTypes; i++) {
                if(values[i] == 0d) continue; // ignore zero-valued prices
                sum[i] += values[i];
                count[i]++;
            }
        }

        double [] getAverages() {
            final double [] averages = new double[numOfFuelTypes];
            for(int i = 0; i < numOfFuelTypes; i++) {
                // in case of 0 count, retuen max Double value (i.e. max price) to allow for correcto ordering of stations
                averages[i] = count[i] == 0 ? Double.MAX_VALUE : sum[i] / count[i];
            }
            return averages;
        }

        double getAverage(final FuelType fuelType) {
            final int selectedFuelTypeIndex = fuelType.getCode() - 1;
            // in case of 0 count, retuen max Double value (i.e. max price) to allow for correcto ordering of stations
            return count[selectedFuelTypeIndex] == 0 ? Double.MAX_VALUE : sum[selectedFuelTypeIndex] / count[selectedFuelTypeIndex];
        }

        String getFormattedAverage(final FuelType fuelType) {
            return String.format("%.4f", getAverage(fuelType));
        }

        @Override
        public String toString() {
            return Arrays.toString(getAverages());
        }
    }
}