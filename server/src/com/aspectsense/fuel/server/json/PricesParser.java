package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis
 * 08-Feb-16
 */
public class PricesParser {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static String toPricesJson(final Vector<PetroleumPriceDetail> petroleumPriceDetails) {

        final StringBuilder jsonStringBuilder = new StringBuilder("[\n");
        int count = 0;
        for(final PetroleumPriceDetail petroleumPriceDetail : petroleumPriceDetails) {
            String priceInMillieuros = "0";
            try {
                final Double price = Double.parseDouble(petroleumPriceDetail.getFuelPrice());
                priceInMillieuros = String.format("%5d", (int) (price * 1000)).trim();
            } catch (NumberFormatException nfe) {
                log.warning("Could not parse '" + petroleumPriceDetail.getFuelPrice() + "' to Double: " + nfe.getMessage());
            }
            boolean isLastElement = ++count == petroleumPriceDetails.size();
            jsonStringBuilder.append("  { \"stationCode\": \"").append(petroleumPriceDetail.getStationCode())
                    .append("\", \"price\": ").append(priceInMillieuros).append(" }")
                    .append(isLastElement ? "\n" : ",\n");
        }
        jsonStringBuilder.append("]\n");

        return jsonStringBuilder.toString();
    }

    public static Map<String,Integer> fromPricesJson(final String pricesJson) {
        final Map<String,Integer> pricesMap = new HashMap<>();
        try {
            final JSONArray jsonArray = new JSONArray(pricesJson);
            for(int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                final String stationCode = jsonObject.getString("stationCode");
                final int price = jsonObject.getInt("price");
                pricesMap.put(stationCode, price);
            }
        } catch (JSONException jsone) {
            throw new RuntimeException(jsone);
        }

        return pricesMap;
    }
}