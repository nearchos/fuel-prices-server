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

package com.aspectsense.fuel.server.api;

import com.aspectsense.fuel.server.data.Offline;
import com.aspectsense.fuel.server.data.Price;
import com.aspectsense.fuel.server.data.Station;
import com.aspectsense.fuel.server.datastore.ApiKeyFactory;
import com.aspectsense.fuel.server.datastore.OfflineFactory;
import com.aspectsense.fuel.server.datastore.PriceFactory;
import com.aspectsense.fuel.server.datastore.StationFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         26/12/2015
 *         21:21
 */
public class ApiSyncServlet extends HttpServlet {

    private final Logger log = Logger.getLogger("cyprusfuelguide");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final long start = System.currentTimeMillis();

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final String from = request.getParameter("from");
        long fromTimestamp = 0L;
        try {
            if(from != null) fromTimestamp = Long.parseLong(from);
        } catch (NumberFormatException nfe) {
            log.info("Could not parse parameter 'from': " + from);
        }

        final String key = request.getParameter("key");
        if(key == null || ! ApiKeyFactory.isActive(key)) {
            response.getWriter().println(" { \"status\": \"error\", \"message\": \"undefined  or unknown key\" }");
        } else {
            // first check if the requested data is already in memcache
            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
            final String memcacheKey = "updates-" + from;
            if(memcacheService.contains(memcacheKey)) {
                response.getWriter().println(memcacheService.get(memcacheKey));
            } else { // key not found in mem-cache

                // get updated data
                final Map<String, Station> updatedStations = StationFactory.getAllStationCodesToStations(fromTimestamp);
                final Map<String, Offline> updatedOfflines = OfflineFactory.getAllOfflines(fromTimestamp);
                final Map<String, Vector<Price>> updatedPrices = PriceFactory.getAllPrices(fromTimestamp);

                long maxTimestamp = fromTimestamp;

                // form JSON reply

                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(" { \"status\": \"ok\", \"from\": ").append(fromTimestamp);
                stringBuilder.append(", \"stations\": [");

                // add station updates
                {
                    final Set<String> stationCodes = updatedStations.keySet();
                    int numOfStations = stationCodes.size();
                    int i = 0;
                    for (final String stationCode : stationCodes) {
                        final Station station = updatedStations.get(stationCode);
                        if (maxTimestamp < station.getLastUpdated()) maxTimestamp = station.getLastUpdated();
                        stringBuilder.append(station.toJSONObject());
                        if (i++ < numOfStations - 1) stringBuilder.append(", ");
                    }
                }
                stringBuilder.append("], \"offlines\": [");

                // add offline updates
                {
                    final Set<String> offlineStationCodes = updatedOfflines.keySet();
                    final int numOfOfflines = updatedOfflines.size();
                    int i = 0;
                    for (final String stationCode : offlineStationCodes) {
                        final Offline offline = updatedOfflines.get(stationCode);
                        if (maxTimestamp < offline.getLastUpdated()) maxTimestamp = offline.getLastUpdated();
                        stringBuilder.append(offline.toJSONObject());
                        if (i++ < numOfOfflines - 1) stringBuilder.append(", ");
                    }
                }
                stringBuilder.append("], \"prices\": [");

                // add price updates
                {
                    final Set<String> priceStationCodes = updatedPrices.keySet();
                    final int numOfPrices = priceStationCodes.size();
                    int i = 0;
                    for (final String stationCode : priceStationCodes) {
                        final Vector<Price> prices = updatedPrices.get(stationCode);
                        for (final Price price : prices) {
                            if (maxTimestamp < price.getLastUpdated()) maxTimestamp = price.getLastUpdated();
                        }
                        stringBuilder.append(Price.toJSONObject(stationCode, prices));
                        if (i++ < numOfPrices - 1) stringBuilder.append(", ");
                    }
                }

                final long finish = System.currentTimeMillis();
                stringBuilder.append("], \"processedInMilliseconds\": ").append(finish - start).append(", \"lastUpdated\": ").append(maxTimestamp).append(" }");

                final String reply = stringBuilder.toString();
                memcacheService.put(memcacheKey, reply); // store in memcache
                response.getWriter().println(reply);
            }
        }
    }
}
