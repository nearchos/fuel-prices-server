package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.data.Price;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16
 */
public class PriceParser {

    public static String toJson(final Price price) {
        return "{ \"stationCode\": \"" + price.getStationCode() + "\", \"prices\": " + Arrays.toString(price.getPrices()) + " }";
    }

    public static Map<String, Price> jsonArrayToMap(final JSONArray pricesJsonArray) throws JSONException {
        final Map<String, Price> pricesMap = new HashMap<>();
        for(int i = 0; i < pricesJsonArray.length(); i++) {
            final JSONObject priceJsonObject = pricesJsonArray.getJSONObject(i);
            final Price price = fromJsonObject(priceJsonObject);
            pricesMap.put(price.getStationCode(), price);
        }
        return pricesMap;
    }

    public static Price fromJsonObject(final JSONObject priceJsonObject) throws JSONException{
        final String stationCode = priceJsonObject.getString("stationCode");

        final JSONArray pricesJsonArray = priceJsonObject.getJSONArray("prices");
        final int [] prices = new int[pricesJsonArray.length()];
        for(int i = 0; i < pricesJsonArray.length(); i++) {
            prices[i] = pricesJsonArray.getInt(i);
        }
        return new Price(stationCode, prices);
    }
}