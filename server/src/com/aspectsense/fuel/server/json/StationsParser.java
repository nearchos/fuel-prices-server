package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.model.Station;
import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author Nearchos Paspallis on 06-Feb-16.
 */
public class StationsParser {

    public static String toStationsJson(final Vector<PetroleumPriceDetail> petroleumPriceDetails) {

        final StringBuilder jsonStringBuilder = new StringBuilder("[\n");
        int count = 0;
        for (final PetroleumPriceDetail petroleumPriceDetail : petroleumPriceDetails) {
            boolean isLastElement = ++count == petroleumPriceDetails.size();
            jsonStringBuilder.append("  ").append(StationParser.toStationJson(petroleumPriceDetail)).append(isLastElement ? "\n" : ",\n");
        }
        jsonStringBuilder.append("]\n");

        return jsonStringBuilder.toString();
    }

    public static Vector<Station> fromStationsJson(final String json) {
        final Station [] stations = new Gson().fromJson(json, Station[].class);
        return new Vector<>(Arrays.asList(stations));
//        try {
//            final JSONArray jsonArray = new JSONArray(json);
//            return fromStationsJsonArray(jsonArray);
//        } catch (JSONException jsone) {
//            throw new RuntimeException(jsone);
//        }
    }

//    public static Vector<Station> fromStationsJsonArray(final JSONArray stationsJsonArray) {
//        final Vector<Station> stations = new Vector<>();
//        try {
//            for(int i = 0; i < stationsJsonArray.length(); i++) {
//                final JSONObject stationJsonObject = stationsJsonArray.getJSONObject(i);
//                stations.add(StationParser.fromStationJsonObject(stationJsonObject));
//            }
//        } catch (JSONException jsone) {
//            throw new RuntimeException(jsone);
//        }
//
//        return stations;
//    }

    public static Map<String,Station> jsonArrayToMap(final String json) {
        final Map<String,Station> map = new HashMap<>();
        final Vector<Station> stations = fromStationsJson(json);
        for(final Station station : stations) {
            map.put(station.getStationCode(), station);
        }
        return map;
//        try {
//            return jsonArrayToMap(new JSONArray(json));
//        } catch (JSONException jsone) {
//            throw new RuntimeException(jsone);
//        }
    }

//    public static Map<String,Station> jsonArrayToMap(final JSONArray stationsJsonArray) {
//        final Vector<Station> allStations = fromStationsJsonArray(stationsJsonArray);
//        final Map<String,Station> map = new HashMap<>();
//        for(final Station station : allStations) {
//            map.put(station.getStationCode(), station);
//        }
//        return map;
//    }
}