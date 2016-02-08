package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.data.Station;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Nearchos on 07-Feb-16.
 */
public class SyncMessageParser {

    public static String computeDiff(final String fromJson, final String toJson) throws JSONException {
        final JSONObject fromJsonObject = new JSONObject(fromJson);
        final JSONArray fromJsonStationsArray = fromJsonObject.getJSONObject("stations").getJSONArray("stations");
        final Map<String, Station> fromCodeToStationsMap = new HashMap<>();
        for(int i = 0; i < fromJsonStationsArray.length(); i++) {
            final Station station = StationsParser.fromStationJsonObject(fromJsonStationsArray.getJSONObject(i));
            fromCodeToStationsMap.put(station.getStationCode(), station);
        }
        final JSONArray fromJsonOfflinesArray = fromJsonObject.getJSONObject("offlines").getJSONArray("offlines");
        final Map<String, Boolean> fromOfflines = OfflinesParser.fromOfflinesJsonArray(fromJsonOfflinesArray);
        final JSONArray fromJsonPricesArray = fromJsonObject.getJSONArray("prices");
        final Map<String, Vector<Integer>> fromCodeToPricesInMillieurosMap = new HashMap<>();
        for(int i = 0; i < fromJsonPricesArray.length(); i++) {
            final JSONObject priceJsonObject = fromJsonPricesArray.getJSONObject(i);
            final String stationCode = priceJsonObject.getString("code");
            final Vector<Integer> prices = new Vector<>();
            final JSONArray fromPricesJsonArray = priceJsonObject.getJSONArray("prices");
            for(int j = 0; j < fromPricesJsonArray.length(); j++) {
                prices.add(fromPricesJsonArray.getInt(j));
            }
            fromCodeToPricesInMillieurosMap.put(stationCode, prices);
        }

        final JSONObject toJsonObject = new JSONObject(toJson);
        final JSONArray toJsonStationsArray = toJsonObject.getJSONObject("stations").getJSONArray("stations");
        final Vector<Station> toStations = new Vector<>();
        for(int i = 0; i < toJsonStationsArray.length(); i++) {
            final Station station = StationsParser.fromStationJsonObject(toJsonStationsArray.getJSONObject(i));
            toStations.add(station);
        }
        final JSONArray toJsonOfflinesArray = toJsonObject.getJSONObject("offlines").getJSONArray("offlines");
        final Map<String, Boolean> toOfflines = OfflinesParser.fromOfflinesJsonArray(toJsonOfflinesArray);
        final JSONArray toJsonPricesArray = toJsonObject.getJSONArray("prices");

        final Vector<Station> modifiedStations = new Vector<>();
        for(final Station station : toStations) {
            if(!station.equals(fromCodeToStationsMap.get(station.getStationCode()))) {
                modifiedStations.add(station);
            }
        }

        final Map<String, Boolean> modifiedOfflines = new HashMap<>();
        for(final Map.Entry<String, Boolean> toOfflineEntry : toOfflines.entrySet()) {
            final String stationCode = toOfflineEntry.getKey();
            final boolean offline = toOfflineEntry.getValue();
            if(fromOfflines.get(stationCode) != offline) {
                modifiedOfflines.put(stationCode, offline);
            }
        }

        final Map<String,Vector<Integer>> modifiedPrices = new HashMap<>();
        for(int i = 0; i < toJsonPricesArray.length(); i++) {
            final JSONObject pricesJsonObject = toJsonPricesArray.getJSONObject(i);
            final String stationCode = pricesJsonObject.getString("code");
            final JSONArray pricesJsonArray = pricesJsonObject.getJSONArray("prices");
            final Vector<Integer> prices = new Vector<>();
            for(int j = 0; j < pricesJsonArray.length(); j++) {
                prices.add(pricesJsonArray.getInt(j));
            }
            if(different(prices, fromCodeToPricesInMillieurosMap.get(stationCode))) {
                modifiedPrices.put(stationCode, prices);
            }
        }

        final StringBuilder stringBuilder = new StringBuilder("{");

        // compute differences in stations
        stringBuilder.append("  \"stations\": [");
        for(int i = 0; i < modifiedStations.size(); i++) {
            final Station station = modifiedStations.elementAt(i);
            if(!station.equals(fromCodeToStationsMap.get(station.getStationCode()))) {
                stringBuilder.append("    ").append(StationsParser.toStationJson(station));
                stringBuilder.append(i < modifiedStations.size() - 1 ? ",\n" : "\n");
            }
        }
        // compute differences in offlines
        stringBuilder.append("  ], \"offlines\": [");
        int counter = 0;
        for(Map.Entry<String, Boolean> entry : modifiedOfflines.entrySet()) {
            stringBuilder.append("    { \"stationCode\": \"").append(entry.getKey()).append("\", \"offline\": ").append(entry.getValue());
            stringBuilder.append(counter++ < modifiedOfflines.size() - 1 ? "},\n" : "}\n");
        }
        // compute differences in prices
        stringBuilder.append("  ], \"prices\": [");
        counter = 0;
        for(Map.Entry<String, Vector<Integer>> entry : modifiedPrices.entrySet()) {
            stringBuilder.append("    { \"code\": \"").append(entry.getKey()).append("\", \"prices\": ").append(entry.getValue());
            stringBuilder.append(counter++ < modifiedPrices.size() - 1 ? "},\n" : "}\n");
        }
        stringBuilder.append("  ]");

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private static boolean different(final Vector<Integer> left, final Vector<Integer> right) {
        if(left == null || right == null) throw new NullPointerException("Invalid null argument(s)");
        if(left.size() != right.size()) return true;
        for(int i = 0; i < left.size(); i++) {
            if(left.elementAt(i) != right.elementAt(i)) return true;
        }
        return false;
    }
}