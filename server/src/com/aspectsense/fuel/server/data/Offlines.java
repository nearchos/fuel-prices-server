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
 * along with Foobar. If not, see <http://www.gnu.org/licenses/>.
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
public class Offlines implements Serializable {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    private final String uuid;
    private final String json;
    private final long lastUpdated;

    public Offlines(String uuid, String json, long lastUpdated) {
        this.uuid = uuid;
        this.json = json; // of the form "{ "offlines": [ { "stationCode": "EK012", "offline": true }, { "stationCode": "ES007", "offline": : false } , ... , { "stationCode": "PE010", "offline": : false } ] }"
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public Map<String,Boolean> getStationCodeToOfflineMap() {
        final Map<String,Boolean> stationCodeToOfflineMap = new HashMap<>();
        try {
            final JSONObject jsonObject = new JSONObject(json);

            // parse JSON
            final JSONArray offlines = jsonObject.getJSONArray("offlines");
            for(int i = 0; i < offlines.length(); i++) {
                final JSONObject offlinesJSONObject = offlines.getJSONObject(i);
                final String stationCode = offlinesJSONObject.getString("stationCode");
                final boolean offline = offlinesJSONObject.getBoolean("offline");
                stationCodeToOfflineMap.put(stationCode, offline);
            }
        } catch (JSONException jsone) {
            log.severe("JSON Error: " + jsone);
            log.severe("Error while parsing JSON: " + json);
        }
        return stationCodeToOfflineMap;
    }
}