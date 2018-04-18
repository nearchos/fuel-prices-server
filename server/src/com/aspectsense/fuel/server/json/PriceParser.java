package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.model.Price;

import java.util.Arrays;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16
 */
public class PriceParser {

    public static String toJson(final Price price) {
        return "{ \"stationCode\": \"" + price.getStationCode() + "\", \"prices\": " + Arrays.toString(price.getPrices()) + ", \"timestamps\": " + Arrays.toString(price.getTimestamps()) + " }";
    }

//    public static Map<String, Price> jsonArrayToMap(final JSONArray pricesJsonArray) throws JSONException {
//        final Map<String, Price> pricesMap = new HashMap<>();
//        for(int i = 0; i < pricesJsonArray.length(); i++) {
//            final JSONObject priceJsonObject = pricesJsonArray.getJSONObject(i);
//            final Price price = fromJsonObject(priceJsonObject);
//            pricesMap.put(price.getStationCode(), price);
//        }
//        return pricesMap;
//    }
//
//    public static Price fromJsonObject(final JSONObject priceJsonObject) throws JSONException {
//        final String stationCode = priceJsonObject.getString("stationCode");
//
//        final JSONArray pricesJsonArray = priceJsonObject.getJSONArray("prices");
//        final int [] prices = new int[pricesJsonArray.length()];
//        for(int i = 0; i < pricesJsonArray.length(); i++) {
//            prices[i] = pricesJsonArray.getInt(i);
//        }
//        final long [] timestamps;
//        if(priceJsonObject.has("timestamps")) {
//            final JSONArray timestampsJsonArray = priceJsonObject.getJSONArray("timestamps");
//            timestamps = new long[timestampsJsonArray.length()];
//            for (int i = 0; i < timestampsJsonArray.length(); i++) {
//                timestamps[i] = timestampsJsonArray.getLong(i);
//            }
//        } else {
//            final long now = System.currentTimeMillis();
//            timestamps = new long[FuelType.ALL_FUEL_TYPES.length]; // by default, all 'now' (for backwards compatibility)
//            for (int i = 0; i < timestamps.length; i++) {
//                timestamps[i] = now;
//            }
//        }
//        return new Price(stationCode, prices, timestamps);
//    }
}