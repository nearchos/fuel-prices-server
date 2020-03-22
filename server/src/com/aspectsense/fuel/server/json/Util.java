package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.data.City;
import com.aspectsense.fuel.server.model.Station;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16.
 */
public class Util {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String sanitizeForJSON(String unsanitized) {
        if(unsanitized == null) {
            return null;
        } else {
            while(unsanitized.startsWith("\"")) unsanitized = unsanitized.substring(1, unsanitized.length());
            while(unsanitized.endsWith("\"")) unsanitized = unsanitized.substring(0, unsanitized.length() - 1);
            return unsanitized.trim().replaceAll("\"", "'");
        }
    }

    public static Map<City,Set<String>> findCheapestStationsPerCity(final Map<String, Station> stationsMap, final Map<String,Integer> prices) {
        final Map<City,Integer> cheapestPricesPerCity = new HashMap<>();
        final Map<City,Set<String>> cheapestStationsPerCity = new HashMap<>();
        for(final City city : City.ALL_CITIES) {
            cheapestPricesPerCity.put(city, Integer.MAX_VALUE);
            cheapestStationsPerCity.put(city, new HashSet<>());
        }
        for(final String stationCode : prices.keySet()) {
            final Station station = stationsMap.get(stationCode);
            if(station == null) {
                log.warning("Skipping unknown station code: " + stationCode);
                continue;
            }
            final City stationCity = City.decode(station.getStationCity());
            final int price = prices.get(stationCode);
            if(price < cheapestPricesPerCity.get(stationCity)) {
                cheapestPricesPerCity.put(stationCity, price);
                cheapestStationsPerCity.get(stationCity).clear();
                cheapestStationsPerCity.get(stationCity).add(stationCode);
            } else if(price == cheapestPricesPerCity.get(stationCity)) {
                cheapestStationsPerCity.get(stationCity).add(stationCode);
            }
        }

        return cheapestStationsPerCity;
    }

    /**
     * Selects at least minNumberOfStationsPerCity stations from each city, sorted by price.
     *
     * @param stationsMap station code to station (needed to find the city)
     * @param prices station code to price
     * @param minNumberOfStationsPerCity minimum number of stations to be added per city
     * @return a mapping for each {@link City} pointing to a vector with the needed station codes, sorted by price from cheapest to more expensive
     */
    public static Map<City,Vector<String>> findCheapestStationsPerCity(final Map<String, Station> stationsMap, final Map<String,Integer> prices, final int minNumberOfStationsPerCity) {

        // initiate data structure to be returned
        final Map<City,Vector<String>> selectedStationsPerCity = new HashMap<>();

        // first populate the map with all station entries, by city
        final Map<City,Vector<Map.Entry<String,Integer>>> cityToSortedStationPriceEntries = new HashMap<>();
        for(final City city : City.ALL_CITIES) {
            cityToSortedStationPriceEntries.put(city, new Vector<>());
        }
        for(final Map.Entry<String,Integer> stationCodeToPriceEntry : prices.entrySet()) {
            final String stationCode = stationCodeToPriceEntry.getKey();
            final Station station = stationsMap.get(stationCode);
            if(station == null) {
                log.warning("Skipping unknown station code: " + stationCode);
                continue;
            }
            final City stationCity = City.decode(station.getStationCity());
            final Vector<Map.Entry<String,Integer>> stationPriceEntries = cityToSortedStationPriceEntries.get(stationCity);
            stationPriceEntries.add(stationCodeToPriceEntry);
        }

        // sort the station-price entries per city from cheapest to most expensive, and add the needed stations in the returned data
        for(final City city : City.ALL_CITIES) {

            final Vector<Map.Entry<String,Integer>> stationPriceEntries = cityToSortedStationPriceEntries.get(city);

            // first sort the station-price entries per city from cheapest to most expensive
            stationPriceEntries.sort(Map.Entry.comparingByValue()); // this asks for a smaller to larger sorting

            // second iterate the sorted entries to pick the needed data
            final Vector<String> selectedStationCodes = new Vector<>();
            int mostExpensivePriceAdded = 0; // keeps track of the most expensive price added to the list so far for this city
            for(final Map.Entry<String,Integer> stationPriceEntry : stationPriceEntries) {
                if(selectedStationCodes.size() < minNumberOfStationsPerCity) { // still space for more stations
                    selectedStationCodes.add(stationPriceEntry.getKey()); // add station
                    mostExpensivePriceAdded = stationPriceEntry.getValue(); // the last one added must be the most expensive, as we start from the cheapest
                } else { // if already full, consider adding more stations if their price is equal to the last one added
                    if(stationPriceEntry.getValue() == mostExpensivePriceAdded) {
                        selectedStationCodes.add(stationPriceEntry.getKey()); // add station (as it has the same price as the last one added)
                    }
                }
            }
            selectedStationsPerCity.put(city, selectedStationCodes);
        }

        return selectedStationsPerCity;
    }
}
