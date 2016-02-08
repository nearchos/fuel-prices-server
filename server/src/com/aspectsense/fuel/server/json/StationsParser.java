package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.data.Station;
import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.Vector;

/**
 * Created by Nearchos on 06-Feb-16.
 */
public class StationsParser {

    public static String toStationsJson(final Vector<PetroleumPriceDetail> petroleumPriceDetails) {

        final StringBuilder jsonStringBuilder = new StringBuilder("[\n");
        int count = 0;
        for (final PetroleumPriceDetail petroleumPriceDetail : petroleumPriceDetails) {
            boolean isLastElement = ++count == petroleumPriceDetails.size();
            jsonStringBuilder
                    .append("    ").append(toStationJson(petroleumPriceDetail)).append(isLastElement ? "\n" : ",\n");
        }
        jsonStringBuilder.append("]\n");

        return jsonStringBuilder.toString();
    }

    public static Vector<Station> fromStationsJson(final String json) {
        final Vector<Station> stations = new Vector<>();
        try {
            final JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++) {
                final JSONObject stationJsonObject = jsonArray.getJSONObject(i);
                stations.add(fromStationJsonObject(stationJsonObject));
            }
        } catch (JSONException jsone) {
            throw new RuntimeException(jsone);
        }

        return stations;
    }

    public static String toStationJson(final Station station) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ \"code\": \"").append(station.getStationCode())
                .append("\", \"name\": \"").append(station.getStationName())
                .append("\", \"telNo\": \"").append(station.getStationTelNo())
                .append("\", \"address\": \"").append(station.getStationAddress())
                .append("\", \"district\": \"").append(station.getStationDistrict())
                .append("\", \"city\": \"").append(station.getStationCity())
                .append("\", \"lat\": ").append(station.getStationLatitude())
                .append(", \"lng\": ").append(station.getStationLongitude())
                .append(" }");
        return stringBuilder.toString();
    }

    public static String toStationJson(final PetroleumPriceDetail petroleumPriceDetail) {
        return "{ \"code\": \"" + petroleumPriceDetail.getStationCode() +
               "\", \"name\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationName()) +
               "\", \"telNo\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationTelNo()) +
               "\", \"address\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationAddress()) +
               "\", \"district\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationDistrict()) +
               "\", \"city\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationCity()) +
               "\", \"lat\": " + petroleumPriceDetail.getStationLatitude() +
               ", \"lng\": " + petroleumPriceDetail.getStationLongitude() +
               " }";
    }

    public static Station fromStationJsonObject(final JSONObject stationJsonObject) throws JSONException {
        return new Station(
                stationJsonObject.getString("code"),
                stationJsonObject.getString("name"),
                stationJsonObject.getString("telNo"),
                stationJsonObject.getString("city"),
                stationJsonObject.getString("district"),
                stationJsonObject.getString("address"),
                stationJsonObject.getDouble("lat"),
                stationJsonObject.getDouble("lng")
        );
    }

    public static String sanitizeForJSON(String unsanitized) {
        if(unsanitized == null) {
            return null;
        } else {
            while(unsanitized.startsWith("\"")) unsanitized = unsanitized.substring(1, unsanitized.length());
            while(unsanitized.endsWith("\"")) unsanitized = unsanitized.substring(0, unsanitized.length() - 1);
            return unsanitized.trim().replaceAll("\"", "'");
        }
    }
}