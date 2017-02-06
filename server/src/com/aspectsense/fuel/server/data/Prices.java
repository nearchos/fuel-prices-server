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

    public Map<String,PriceInMillieurosAndTimestamp> getStationCodeToPriceInMillieurosAndTimestampMap() {
        final Map<String,PriceInMillieurosAndTimestamp> stationCodeToPriceInMillieurosAndTimestampMap = new HashMap<>();
        try {
            // parse JSON
            final JSONArray prices = new JSONArray(json);
            for(int i = 0; i < prices.length(); i++) {
                final JSONObject price = prices.getJSONObject(i);
                final String stationCode = price.getString("stationCode");
                final int priceInMillieuros = price.getInt("price");
                final long timestamp = price.has("timestamp") ? price.getLong("timestamp") : 0;
                stationCodeToPriceInMillieurosAndTimestampMap.put(stationCode, new PriceInMillieurosAndTimestamp(priceInMillieuros, timestamp));
            }
        } catch (JSONException jsone) {
            log.severe("JSON Error: " + jsone);
            log.severe("Error while parsing JSON: " + json);
            throw new RuntimeException(jsone);
        }
        return stationCodeToPriceInMillieurosAndTimestampMap;
    }

    public class PriceInMillieurosAndTimestamp {
        private int priceInMillieuros;
        private long timestamp;

        public PriceInMillieurosAndTimestamp(final int priceInMillieuros, final long timestamp) {
            this.priceInMillieuros = priceInMillieuros;
            this.timestamp = timestamp;
        }

        public int getPriceInMillieuros() {
            return priceInMillieuros;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}