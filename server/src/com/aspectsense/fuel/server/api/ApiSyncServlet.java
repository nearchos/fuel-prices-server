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

import com.aspectsense.fuel.server.admin.AdminSyncServlet;
import com.aspectsense.fuel.server.data.Offline;
import com.aspectsense.fuel.server.data.Prices;
import com.aspectsense.fuel.server.data.Station;
import com.aspectsense.fuel.server.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
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
//            // first check if the requested data is already in memcache
//            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
//            final String memCacheKey = "updates-" + from;
//            if(memcacheService.contains(memCacheKey)) {
//                response.getWriter().println(memcacheService.get(memCacheKey));
//            } else { // key not found in mem-cache
//
//                // get updated data
//                final Map<String, Station> updatedStations = StationFactory.getAllStationCodesToStations(fromTimestamp);
//                final Map<String, Offline> updatedOfflines = OfflineFactory.getAllOfflines(fromTimestamp);
//                final Map<String, Vector<Price>> updatedPrices = PriceFactory.getAllPrices(fromTimestamp);
//
//                long maxTimestamp = fromTimestamp;
//
//                // form JSON reply
//
//                final StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(" { \"status\": \"ok\", \"from\": ").append(fromTimestamp);
//                stringBuilder.append(", \"stations\": [");
//
//                // add station updates
//                {
//                    final Set<String> stationCodes = updatedStations.keySet();
//                    int numOfStations = stationCodes.size();
//                    int i = 0;
//                    for (final String stationCode : stationCodes) {
//                        final Station station = updatedStations.get(stationCode);
//                        if (maxTimestamp < station.getLastUpdated()) maxTimestamp = station.getLastUpdated();
//                        stringBuilder.append(station.toJSONObject());
//                        if (i++ < numOfStations - 1) stringBuilder.append(", ");
//                    }
//                }
//                stringBuilder.append("], \"offlines\": [");
//
//                // add offline updates
//                {
//                    final Set<String> offlineStationCodes = updatedOfflines.keySet();
//                    final int numOfOfflines = updatedOfflines.size();
//                    int i = 0;
//                    for (final String stationCode : offlineStationCodes) {
//                        final Offline offline = updatedOfflines.get(stationCode);
//                        if (maxTimestamp < offline.getLastUpdated()) maxTimestamp = offline.getLastUpdated();
//                        stringBuilder.append(offline.toJSONObject());
//                        if (i++ < numOfOfflines - 1) stringBuilder.append(", ");
//                    }
//                }
//                stringBuilder.append("], \"prices\": [");
//
//                // add price updates
//                {
//                    final Set<String> priceStationCodes = updatedPrices.keySet();
//                    final int numOfPrices = priceStationCodes.size();
//                    int i = 0;
//                    for (final String stationCode : priceStationCodes) {
//                        final Vector<Price> prices = updatedPrices.get(stationCode);
//                        for (final Price price : prices) {
//                            if (maxTimestamp < price.getLastUpdated()) maxTimestamp = price.getLastUpdated();
//                        }
//                        stringBuilder.append(Price.toJSONObject(stationCode, prices));
//                        if (i++ < numOfPrices - 1) stringBuilder.append(", ");
//                    }
//                }
//
//                final long finish = System.currentTimeMillis();
//                stringBuilder.append("], \"processedInMilliseconds\": ").append(finish - start).append(", \"lastUpdated\": ").append(maxTimestamp).append(" }");
//
//                final String reply = stringBuilder.toString();
//                memcacheService.put(memCacheKey, reply); // store in memcache
//                response.getWriter().println(reply);

//                response.getWriter().println(getJSON(fromTimestamp));

                response.getWriter().println(getSummaryJSON(fromTimestamp));
//            }
        }
    }

//    static public String getJSON(final long fromTimestamp) {
//
//        final long start = System.currentTimeMillis();
//
//        // first check if the requested data is already in memcache
//        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
//        final String memCacheKey = "updates-" + fromTimestamp;
//        if(memcacheService.contains(memCacheKey)) {
//            return (String) memcacheService.get(memCacheKey);
//        } else { // key not found in mem-cache
//
//            // get updated data
//            final Map<String, Station> updatedStations = StationFactory.getAllStationCodesToStations(fromTimestamp);
//            final Map<String, Offline> updatedOfflines = OfflineFactory.getAllOfflines(fromTimestamp);
//            final Map<String, Vector<Price>> updatedPrices = PriceFactory.getAllPrices(fromTimestamp);
//
//            long maxTimestamp = fromTimestamp;
//
//            // form JSON reply
//
//            final StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(" { \"status\": \"ok\", \"from\": ").append(fromTimestamp);
//            stringBuilder.append(", \"stations\": [");
//
//            // add station updates
//            {
//                final Set<String> stationCodes = updatedStations.keySet();
//                int numOfStations = stationCodes.size();
//                int i = 0;
//                for (final String stationCode : stationCodes) {
//                    final Station station = updatedStations.get(stationCode);
//                    if (maxTimestamp < station.getLastUpdated()) maxTimestamp = station.getLastUpdated();
//                    stringBuilder.append(station.toJSONObject());
//                    if (i++ < numOfStations - 1) stringBuilder.append(", ");
//                }
//            }
//            stringBuilder.append("], \"offlines\": [");
//
//            // add offline updates
//            {
//                final Set<String> offlineStationCodes = updatedOfflines.keySet();
//                final int numOfOfflines = updatedOfflines.size();
//                int i = 0;
//                for (final String stationCode : offlineStationCodes) {
//                    final Offline offline = updatedOfflines.get(stationCode);
//                    if (maxTimestamp < offline.getLastUpdated()) maxTimestamp = offline.getLastUpdated();
//                    stringBuilder.append(offline.toJSONObject());
//                    if (i++ < numOfOfflines - 1) stringBuilder.append(", ");
//                }
//            }
//            stringBuilder.append("], \"prices\": [");
//
//            // add price updates
//            {
//                final Set<String> priceStationCodes = updatedPrices.keySet();
//                final int numOfPrices = priceStationCodes.size();
//                int i = 0;
//                for (final String stationCode : priceStationCodes) {
//                    final Vector<Price> prices = updatedPrices.get(stationCode);
//                    for (final Price price : prices) {
//                        if (maxTimestamp < price.getLastUpdated()) maxTimestamp = price.getLastUpdated();
//                    }
//                    stringBuilder.append(Price.toJSONObject(stationCode, prices));
//                    if (i++ < numOfPrices - 1) stringBuilder.append(", ");
//                }
//            }
//
//            final long finish = System.currentTimeMillis();
//            stringBuilder.append("], \"processedInMilliseconds\": ").append(finish - start).append(", \"lastUpdated\": ").append(maxTimestamp).append(" }");
//
//            final String reply = stringBuilder.toString();
//            memcacheService.put(memCacheKey, reply); // store in memcache
//
//            return reply;
//        }
//    }

    static public String getSummaryJSON(final long fromTimestamp) {

        final long start = System.currentTimeMillis();

        // first check if the requested data is already in memcache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        final String memCacheKey = "updates-" + fromTimestamp;
        if(memcacheService.contains(memCacheKey)) {
            return (String) memcacheService.get(memCacheKey);
        } else { // key not found in mem-cache

            // get updated data
            final Map<String, Station> updatedStations = StationFactory.getAllStationCodesToStations(fromTimestamp);

            final Map<String, Offline> updatedOfflines = OfflineFactory.getAllOfflines(fromTimestamp);

            final Prices pricesPetrol95 = PricesFactory.getLatestPrices(AdminSyncServlet.FUEL_TYPES[0]);
            final Prices pricesPetrol98 = PricesFactory.getLatestPrices(AdminSyncServlet.FUEL_TYPES[1]);
            final Prices pricesDiesel   = PricesFactory.getLatestPrices(AdminSyncServlet.FUEL_TYPES[2]);
            final Prices pricesHeating  = PricesFactory.getLatestPrices(AdminSyncServlet.FUEL_TYPES[3]);

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

            // { "stationCode": "EK055", "prices":
            //   [
            //     { "type": "2", "price": 1179 }, { "type": "4", "price": 649 }, { "type": "1", "price": 1129 }, { "type": "3", "price": 1078 }
            //   ]
            // }

            final Map<String,Integer> stationCodeToPriceInMillieurosMapPetrol95 = pricesPetrol95 == null ? new HashMap<String, Integer>() :  pricesPetrol95.getStationCodeToPriceInMillieurosMap();
            final Map<String,Integer> stationCodeToPriceInMillieurosMapPetrol98 = pricesPetrol98 == null ? new HashMap<String, Integer>() :  pricesPetrol98.getStationCodeToPriceInMillieurosMap();
            final Map<String,Integer> stationCodeToPriceInMillieurosMapDiesel   = pricesDiesel   == null ? new HashMap<String, Integer>() :  pricesDiesel.getStationCodeToPriceInMillieurosMap();
            final Map<String,Integer> stationCodeToPriceInMillieurosMapHeating  = pricesHeating  == null ? new HashMap<String, Integer>() :  pricesHeating.getStationCodeToPriceInMillieurosMap();

            final Set<String> allStationCodes = new HashSet<>();
            allStationCodes.addAll(stationCodeToPriceInMillieurosMapPetrol95.keySet());
            allStationCodes.addAll(stationCodeToPriceInMillieurosMapPetrol98.keySet());
            allStationCodes.addAll(stationCodeToPriceInMillieurosMapDiesel.keySet());
            allStationCodes.addAll(stationCodeToPriceInMillieurosMapHeating.keySet());

            if (pricesPetrol95 != null && maxTimestamp < pricesPetrol95.getLastUpdated()) maxTimestamp = pricesPetrol95.getLastUpdated();
            if (pricesPetrol98 != null && maxTimestamp < pricesPetrol98.getLastUpdated()) maxTimestamp = pricesPetrol98.getLastUpdated();
            if (pricesDiesel != null   && maxTimestamp < pricesDiesel.getLastUpdated())   maxTimestamp = pricesDiesel.getLastUpdated();
            if (pricesHeating != null  && maxTimestamp < pricesHeating.getLastUpdated())  maxTimestamp = pricesHeating.getLastUpdated();

            // add price updates
            {
                final int numOfPrices = allStationCodes.size();
                int i = 0;
                for (final String stationCode : allStationCodes) {
                    stringBuilder.append("{ \"stationCode\": \"").append(stationCode).append("\", \"prices\": [ ");
                    boolean commaNeeded = false;
                    if(stationCodeToPriceInMillieurosMapPetrol95.containsKey(stationCode)) {
                        stringBuilder.append("\"type\": \"").append("1").append("\", \"price\": ").append(stationCodeToPriceInMillieurosMapPetrol95.get(stationCode));
                        commaNeeded = true;
                    }
                    if(stationCodeToPriceInMillieurosMapPetrol98.containsKey(stationCode)) {
                        stringBuilder.append(commaNeeded ? ", " : "").append("\"type\": \"").append("2").append("\", \"price\": ").append(stationCodeToPriceInMillieurosMapPetrol98.get(stationCode));
                        commaNeeded = true;
                    }
                    if(stationCodeToPriceInMillieurosMapDiesel.containsKey(stationCode)) {
                        stringBuilder.append(commaNeeded ? ", " : "").append("\"type\": \"").append("3").append("\", \"price\": ").append(stationCodeToPriceInMillieurosMapDiesel.get(stationCode));
                        commaNeeded = true;
                    }
                    if(stationCodeToPriceInMillieurosMapHeating.containsKey(stationCode)) {
                        stringBuilder.append(commaNeeded ? ", " : "").append("\"type\": \"").append("4").append("\", \"price\": ").append(stationCodeToPriceInMillieurosMapHeating.get(stationCode));
                    }
                    stringBuilder.append((i++ < numOfPrices - 1) ? " ], " : " ] ");
                }
            }

            final long finish = System.currentTimeMillis();
            stringBuilder.append("], \"processedInMilliseconds\": ").append(finish - start).append(", \"lastUpdated\": ").append(maxTimestamp).append(" }");

            final String reply = stringBuilder.toString();
            memcacheService.put(memCacheKey, reply); // store in memcache

            return reply;
        }
    }
}