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

package com.aspectsense.fuel.server.sync;

import com.aspectsense.fuel.server.admin.AdminSyncServlet;
import com.aspectsense.fuel.server.api.ApiSyncServlet;
import com.aspectsense.fuel.server.data.Offlines;
import com.aspectsense.fuel.server.data.Prices;
import com.aspectsense.fuel.server.datastore.*;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         10/01/2016
 *         10:02
 */
public class UpdateDatastoreServlet extends HttpServlet {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String apiKeyCode = request.getParameter("apiKeyCode");
        if(apiKeyCode == null || apiKeyCode.isEmpty() || !ApiKeyFactory.isActive(apiKeyCode)) {
            log.severe("Empty or invalid apiKeyCode: " + apiKeyCode);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid apiKeyCode: " + apiKeyCode + "\" }"); // normal JSON output
            return; // terminate here
        }

        // we use a single timestamp for the full update operation of all the prices
        final long updateTimestamp = System.currentTimeMillis();

//        final String json = ApiSyncServlet.getJSON(updateTimestamp);
        final String json = ApiSyncServlet.getSummaryJSON(updateTimestamp);

        try {
            final JSONObject jsonObject = new JSONObject(json);

            // update prices
            // first use the JSON file from mem cache to initialize the 'old' prices view...
            {
                final JSONArray pricesJsonArray = jsonObject.getJSONArray("prices");
                final Map<String,Integer> oldStationCodeAndFuelTypeToPricesInMillieurosMap = new HashMap<>();
                for(int i = 0; i < pricesJsonArray.length(); i++) {
                    final JSONObject priceJsonObject = pricesJsonArray.getJSONObject(i);
                    final String stationCode = priceJsonObject.getString("stationCode");
                    final JSONArray childPricesJsonArray = priceJsonObject.getJSONArray("prices");
                    for(int j = 0; j < childPricesJsonArray.length(); j++) {
                        final JSONObject childPriceJsonObject = childPricesJsonArray.getJSONObject(j);
                        final String fuelType = childPriceJsonObject.getString("type");
                        final int priceInMillieuros = childPriceJsonObject.getInt("price");
                        oldStationCodeAndFuelTypeToPricesInMillieurosMap.put(stationCode + "-" + fuelType, priceInMillieuros);
                    }
                }
                // ...and then update the prices as and when needed
                for(final String fuelType : AdminSyncServlet.FUEL_TYPES) {
                    // get the latest JSON for each fuelType
                    final Prices prices = PricesFactory.getLatestPrices(fuelType);
                    final Map<String, Integer> stationCodeToPriceInMillieurosMap = prices == null ? new HashMap<String, Integer>() : prices.getStationCodeToPriceInMillieurosMap();

                    // update the Price datastore and facilitate the update functionality
                    for (final String stationCode : stationCodeToPriceInMillieurosMap.keySet()) {
                        final int priceInMillieuro = stationCodeToPriceInMillieurosMap.get(stationCode);
                        final int oldPriceInMillieuro = oldStationCodeAndFuelTypeToPricesInMillieurosMap.containsKey(stationCode + "-" + fuelType) ?
                                oldStationCodeAndFuelTypeToPricesInMillieurosMap.get(stationCode + "-" + fuelType) : 0;
                        if(priceInMillieuro != oldPriceInMillieuro) {
                            PriceFactory.addOrUpdatePrice(stationCode, fuelType, priceInMillieuro, updateTimestamp);
                        }
                    }
                }
            }

            // update offlines
            // first use the JSON file from mem cache to initialize the 'old' offlines view...
            {
                final JSONArray offlinesJsonArray = jsonObject.getJSONArray("offlines");
                final Map<String,Boolean> oldStationCodesToOfflinesMap = new HashMap<>();
                for(int i = 0; i < offlinesJsonArray.length(); i++) {
                    final JSONObject offlineJsonObject = offlinesJsonArray.getJSONObject(i);
                    final String stationCode = offlineJsonObject.getString("stationCode");
                    final boolean offline = offlineJsonObject.getBoolean("offline");
                    oldStationCodesToOfflinesMap.put(stationCode, offline);
                }
                // ...and then update the offlines as and when needed
                final Offlines offlines = OfflinesFactory.getLatestOfflines();
                final Map<String, Boolean> stationCodeToOfflinesMap = offlines == null ? new HashMap<String, Boolean>() : offlines.getStationCodeToOfflineMap();

                // update the Offline datastore and facilitate the update functionality
                for (final String stationCode : stationCodeToOfflinesMap.keySet()) {
                    final boolean offline = stationCodeToOfflinesMap.get(stationCode);
                    if(!oldStationCodesToOfflinesMap.containsKey(stationCode) || // if the json does not have info on this station...
                            oldStationCodesToOfflinesMap.get(stationCode) != offline) { // ... or if the value has changed...
                        OfflineFactory.addOrUpdateOffline(stationCode, offline, updateTimestamp); // ...update offline
                    }
                }
            }

        } catch (JSONException jsone) {
            printWriter.println("{ \"result\": \"error\", \"message\": \"Invalid cached data: " + jsone.getMessage() + "\" }");
        }
    }
}
