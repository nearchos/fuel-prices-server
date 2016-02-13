/*
 * This file is part of the Cyprus Fuel Guide server.
 *
 * The Cyprus Fuel Guide server is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Cyprus Fuel Guide server is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cyprus Fuel Guide. If not, see <http://www.gnu.org/licenses/>.
 */

package com.aspectsense.fuel.server.data;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         29/12/2015
 *         22:42
 */
public class Prices implements Serializable {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    private final String uuid;
    private final String fuelType;
    private final String json;
    private final long lastUpdated;

    public Prices(String uuid, String fuelType, String json, long lastUpdated) {
        this.uuid = uuid;
        this.fuelType = fuelType;
        this.json = json;
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getJson() { return json; }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public Map<String,Integer> getStationCodeToPriceInMillieurosMap() {
        final Map<String,Integer> stationCodeToPriceInMillieurosMap = new HashMap<>();
        try {
            // parse JSON
            final JSONArray prices = new JSONArray(json);
            for(int i = 0; i < prices.length(); i++) {
                JSONObject price = prices.getJSONObject(i);
                final String stationCode = price.getString("stationCode");
                final String priceString = price.getString("price");
                int priceInMillieuros;
                try { priceInMillieuros = (int) Double.parseDouble(priceString); } catch (NumberFormatException nfe) { priceInMillieuros = 0; }
                stationCodeToPriceInMillieurosMap.put(stationCode, priceInMillieuros);
            }
        } catch (JSONException jsone) {
            log.severe("JSON Error: " + jsone);
            log.severe("Error while parsing JSON: " + json);
            throw new RuntimeException(jsone);
        }
        return stationCodeToPriceInMillieurosMap;
    }
}