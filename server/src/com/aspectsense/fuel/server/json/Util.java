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
            cheapestStationsPerCity.put(city, new HashSet<String>());
        }
//        final Map<String, Station> stationsMap = StationsParser.jsonArrayToMap(stationsJson);
//        final Map<String,Integer> prices = PricesParser.fromPricesJson(pricesJson);
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
}
