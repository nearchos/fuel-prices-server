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
import com.aspectsense.fuel.server.json.StationsParser;
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
        final Vector<Station> allStations = stations != null ? StationsParser.fromStationsJson(stations.getJson()) : new Vector<Station>();
        final Offlines offlines = OfflinesFactory.getLatestOfflines();

        final Map<FuelType, Map<String,Integer>> fuelTypeToStationCodeToPriceInMillieurosMap = new HashMap<>();
        for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            final Prices prices = PricesFactory.getLatestPrices(fuelType.getCodeAsString());
            fuelTypeToStationCodeToPriceInMillieurosMap.put(fuelType, prices.getStationCodeToPriceInMillieurosMap());
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
            stringBuilder.append("    { \"stationCode\": \"").append(station.getStationCode()).append("\", \"prices\": [ ");
            int fuelTypeCounter = 0;
            for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
                final Map<String,Integer> stationCodeToPriceInMillieurosMap = fuelTypeToStationCodeToPriceInMillieurosMap.get(fuelType);
                final int priceInMillieuros;
                if(stationCodeToPriceInMillieurosMap == null) {
                    priceInMillieuros = 0;
                } else if(stationCodeToPriceInMillieurosMap.containsKey(station.getStationCode())) {
                    priceInMillieuros = stationCodeToPriceInMillieurosMap.get(station.getStationCode());
                } else {
                    priceInMillieuros = 0;
                }
                stringBuilder.append(priceInMillieuros).append(fuelTypeCounter++ < FuelType.ALL_FUEL_TYPES.length - 1 ? ", " : "");
            }
            stringBuilder.append(++counter >= numOfStations ? " ] }\n" : " ] },\n");
        }
        stringBuilder.append("  ]");
        stringBuilder.append("}");

        final String json = stringBuilder.toString();
        SyncMessageFactory.addSyncMessage(new Text(json), updateTimestamp);

        // invalidate all sync cache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService(ApiSyncServlet.SYNC_NAMESPACE);
        memcacheService.clearAll();

        // todo check if there has been an update in the last couple of hours, and email an error message if not

        // todo delete everything that is older than say 7 days from prices, offlines, and stations (perhaps email them first)

        printWriter.print("{ \"status\": \"OK\", \"debug\": " + debug + " }\n");
        if(debug) {
            printWriter.print("DEBUG: Generated JSON:\n" + json);
        }
    }
}
