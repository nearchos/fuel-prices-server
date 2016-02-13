package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author Nearchos Paspallis
 * 07-Feb-16
 */
public class OfflinesParser {

    public static String toOfflinesJson(final Vector<PetroleumPriceDetail> petroleumPriceDetails) {

        final StringBuilder jsonStringBuilder = new StringBuilder("[\n");
        int count = 0;
        for(final PetroleumPriceDetail petroleumPriceDetail : petroleumPriceDetails) {
            boolean isLastElement = ++count == petroleumPriceDetails.size();
            jsonStringBuilder.append("  { \"stationCode\": \"").append(petroleumPriceDetail.getStationCode())
                    .append("\", \"offline\": ").append(petroleumPriceDetail.isOffline()).append(" }")
                    .append(isLastElement ? "\n" : ",\n");
        }
        jsonStringBuilder.append("]\n");

        return jsonStringBuilder.toString();
    }

    public static Map<String, Boolean> fromOfflinesJsonArray(final JSONArray jsonArray) {
        final Map<String, Boolean> offlines = new HashMap<>();
        try {
            for(int i = 0; i < jsonArray.length(); i++) {
                final JSONObject offlineJsonObject = jsonArray.getJSONObject(i);
                final String stationCode = offlineJsonObject.getString("stationCode");
                final boolean offline = offlineJsonObject.getBoolean("offline");
                offlines.put(stationCode, offline);
            }
        } catch (JSONException jsone) {
            throw new RuntimeException(jsone);
        }

        return offlines;
    }
}
