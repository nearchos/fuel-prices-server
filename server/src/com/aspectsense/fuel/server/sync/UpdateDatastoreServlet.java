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

package com.aspectsense.fuel.server.sync;

import com.aspectsense.fuel.server.api.ApiSyncServlet;
import com.aspectsense.fuel.server.data.*;
import com.aspectsense.fuel.server.datastore.*;
import com.aspectsense.fuel.server.json.PriceParser;
import com.aspectsense.fuel.server.json.StationsParser;
import com.aspectsense.fuel.server.model.Price;
import com.aspectsense.fuel.server.model.Station;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String magic = request.getParameter("magic");
        if(magic == null || magic.isEmpty() || !ParameterFactory.isMagic(magic)) {
            log.severe("Empty or invalid magic: " + magic);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid magic: " + magic + "\" }"); // normal JSON output
            return; // terminate here
        }

        final boolean debug = request.getParameter("debug") != null;

        // we use a single timestamp for the full update operation of all the prices
        final long updateTimestamp = System.currentTimeMillis();

        final Stations stations = StationsFactory.getLatestStations();
        final Vector<Station> allStations = stations != null ? StationsParser.fromStationsJson(stations.getJson()) : new Vector<>();
        final Offlines offlines = OfflinesFactory.getLatestOfflines();

        final Map<FuelType, Map<String,Prices.PriceInMillieurosAndTimestamp>> fuelTypeToStationCodeToPriceInMillieurosMap = new HashMap<>();
        for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            final Prices prices = PricesFactory.getLatestPrices(fuelType.getCodeAsString());
            assert prices != null;
            fuelTypeToStationCodeToPriceInMillieurosMap.put(fuelType, prices.getStationCodeToPriceInMillieurosAndTimestampMap());
        }

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("  \"lastUpdated\": ").append(updateTimestamp).append(",");
        stringBuilder.append("  \"stations\": ").append(stations != null ? stations.getJson() : "[]").append(",");
        stringBuilder.append("  \"offlines\": ").append(offlines != null ? offlines.getJson() : "[]").append(",");
        stringBuilder.append("  \"prices\": [");
        final int numOfStations = allStations.size();
        int counter  = 0;
        for(final Station station : allStations) {
            final String stationCode = station.getStationCode();
            final Price price = getPrice(stationCode, fuelTypeToStationCodeToPriceInMillieurosMap);
            stringBuilder.append("    ").append(PriceParser.toJson(price));
            stringBuilder.append(++counter >= numOfStations ? "\n" : ",\n");
        }
        stringBuilder.append("  ]");
        stringBuilder.append("}");

        final String currentJson = stringBuilder.toString();

        int numberOfChanges = 0;

        // compute number of differences since last update and ignore saving the SyncMessageEntity is there are no changes
        final SyncMessageEntity latestSyncMessageEntity = SyncMessageFactory.queryLatestSyncMessage();
        if(latestSyncMessageEntity == null) {
            SyncMessageFactory.addSyncMessage(new Text(currentJson), 0, updateTimestamp);
        } else {
            final String latestJson = latestSyncMessageEntity.getJson();
//            try {
                final ApiSyncServlet.Modifications modifications = ApiSyncServlet.computeModifications(latestJson, currentJson);
                numberOfChanges = modifications.getSize();
                if(numberOfChanges > 0) {
                    SyncMessageFactory.addSyncMessage(new Text(currentJson), numberOfChanges, updateTimestamp);
                } else {
                    log.info("Saving the SyncMessageEntity was not necessary because it has 0 modifications (timestamp: " + updateTimestamp + ")");
                }
//            } catch (JSONException jsone) {
//                log.warning("Error while compiling current JSON with latest one to decide if we will store it: " + jsone.getMessage());
//            }
        }

        // invalidate all sync cache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService(ApiSyncServlet.SYNC_NAMESPACE);
        memcacheService.clearAll();

        // check if there has been an update in the last 24 hours...
        if(latestSyncMessageEntity != null && numberOfChanges == 0) {
            final long lastUpdated = latestSyncMessageEntity.getLastUpdated();
            if(System.currentTimeMillis() - lastUpdated > 24L*60*60*1000) {
                // todo send an email with a warning message if no updates
            }
        }

        printWriter.print("{ \"status\": \"OK\", \"debug\": " + debug + " }\n");
        if(debug) {
            printWriter.print("DEBUG: Generated JSON:\n" + currentJson);
        }
    }

    private Price getPrice(final String stationCode, final Map<FuelType, Map<String,Prices.PriceInMillieurosAndTimestamp>> fuelTypeToStationCodeToPriceInMillieurosMap) {
        final int [] prices = new int[FuelType.ALL_FUEL_TYPES.length];
        final long [] timestamps = new long[FuelType.ALL_FUEL_TYPES.length];
        int counter = 0;
        for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            final Map<String,Prices.PriceInMillieurosAndTimestamp> stationCodeToPriceInMillieurosAndTimestampMap = fuelTypeToStationCodeToPriceInMillieurosMap.get(fuelType);
            final Prices.PriceInMillieurosAndTimestamp priceInMillieurosAndTimestamp = stationCodeToPriceInMillieurosAndTimestampMap.get(stationCode);
            if(priceInMillieurosAndTimestamp != null) {
                prices[counter] = priceInMillieurosAndTimestamp.getPriceInMillieuros();
                timestamps[counter] = priceInMillieurosAndTimestamp.getTimestamp();
            } else {
                prices[counter] = 0;
                timestamps[counter] = 0;
            }
            counter++;
        }
        return new Price(stationCode, prices, timestamps);
    }
}