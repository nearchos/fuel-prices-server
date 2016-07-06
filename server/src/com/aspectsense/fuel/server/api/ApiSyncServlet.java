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

package com.aspectsense.fuel.server.api;

import com.aspectsense.fuel.server.data.*;
import com.aspectsense.fuel.server.datastore.*;
import com.aspectsense.fuel.server.json.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
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

    private static final Logger log = Logger.getLogger("cyprusfuelguide");

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
        if(key == null || key.isEmpty()) {
            response.getWriter().println(" { \"status\": \"error\", \"message\": \"undefined  or empty key\" }");
        } else if(!ApiKeyFactory.isActive(key)) {
            response.getWriter().println(" { \"status\": \"error\", \"message\": \"invalid key\" }");
        } else {
            try {
                response.getWriter().println(getSyncMessageAsJSON(fromTimestamp)); // compute reply
            } catch (JSONException jsone) {
                response.getWriter().println(" { \"status\": \"error\", \"message\": \"" + jsone.getMessage() + "\" }");
            }
        }
    }

    public static final String SYNC_NAMESPACE = "sync";

    private String getSyncMessageAsJSON(final long fromTimestamp) throws JSONException {

        // first check if the requested data is already in memcache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService(SYNC_NAMESPACE);
        if(memcacheService.contains(fromTimestamp)) {
            return (String) memcacheService.get(fromTimestamp);
        } else { // key not found in memcache - data must be dynamically generated now (and stored in cache at the end)
            final long start = System.currentTimeMillis();
            final String reply;
            final SyncMessage targetSyncMessage = SyncMessageFactory.queryLatestSyncMessage();
            if(targetSyncMessage == null) {
                log.severe("No SyncMessages in datastore");
                return "{ \"status\": \"error\", \"message\": \"No SyncMessages in datastore\"}";
            } else {
                final Modifications modifications;
                if(fromTimestamp == 0L) { // no need to compute modifications, just return the latest data
                    modifications = computeModifications(targetSyncMessage);
                } else { // must dynamically compute the changes
                    final SyncMessage sourceSyncMessage = SyncMessageFactory.querySyncMessage(fromTimestamp);
                    if(sourceSyncMessage == null) {
                        log.warning("No SyncMessage in datastore for given timestamp:" + fromTimestamp);
                        // revert to sending back the whole Sync message
                        return getSyncMessageAsJSON(0L);
                    } else {
                        modifications = computeModifications(sourceSyncMessage, targetSyncMessage);
                    }
                }

                final long finish = System.currentTimeMillis();

                reply = formReplyMessage(fromTimestamp, modifications, finish-start, targetSyncMessage.getLastUpdated());

                memcacheService.put(fromTimestamp, reply); // store in memcache
            }
            return reply;
        }
    }

    private Modifications computeModifications(final SyncMessage targetSyncMessage) throws JSONException {
        // compute target-related data
        final String targetJson = targetSyncMessage.getJson();
        final JSONObject targetJsonObject = new JSONObject(targetJson);

        final JSONArray targetStationsArray = targetJsonObject.getJSONArray("stations");
        final Vector<Station> targetStations = StationsParser.fromStationsJsonArray(targetStationsArray);

        final JSONArray targetJsonOfflinesArray = targetJsonObject.getJSONArray("offlines");
        final Map<String, Boolean> targetOfflines = OfflinesParser.fromOfflinesJsonArray(targetJsonOfflinesArray);

        final JSONArray targetPricesArray = targetJsonObject.getJSONArray("prices");
        final Map<String, Price> targetPrices = PriceParser.jsonArrayToMap(targetPricesArray);

        // add all stations
        final Vector<Station> modifiedStations = new Vector<>();
        for(final Station station : targetStations) {
            modifiedStations.add(station);
        }

        // no station was removed
        final Vector<Station> removedStations = new Vector<>();

        // add all offlines
        final Vector<Offline> modifiedOfflines = new Vector<>();
        for(final Map.Entry<String, Boolean> targetOfflineEntry : targetOfflines.entrySet()) {
            final String stationCode = targetOfflineEntry.getKey();
            final boolean offline = targetOfflineEntry.getValue();
            modifiedOfflines.add(new Offline(stationCode, offline));
        }

        // add all prices
        final Vector<Price> modifiedPrices = new Vector<>();
        for(final Map.Entry<String,Price> targetPriceEntry : targetPrices.entrySet()) {
            final Price targetPrice = targetPriceEntry.getValue();
            modifiedPrices.add(targetPrice);
        }

        return new Modifications(modifiedStations, removedStations, modifiedOfflines, modifiedPrices);

    }

    public static Modifications computeModifications(final SyncMessage sourceSyncMessage, final SyncMessage targetSyncMessage) throws JSONException {
        final String sourceJson = sourceSyncMessage.getJson();
        final String targetJson = targetSyncMessage.getJson();
        return computeModifications(sourceJson, targetJson);
    }

    public static Modifications computeModifications(final String sourceJson, final String targetJson) throws JSONException {

        // compute source-related data
        final JSONObject sourceJsonObject = new JSONObject(sourceJson);

        final JSONArray sourceStationsArray = sourceJsonObject.getJSONArray("stations");
        final Map<String, Station> sourceCodeToStationsMap = StationsParser.jsonArrayToMap(sourceStationsArray);

        final JSONArray sourceOfflinesArray = sourceJsonObject.getJSONArray("offlines");
        final Map<String, Boolean> sourceOfflines = OfflinesParser.fromOfflinesJsonArray(sourceOfflinesArray);

        final JSONArray sourcePricesArray = sourceJsonObject.getJSONArray("prices");
        final Map<String, Price> sourcePrices = PriceParser.jsonArrayToMap(sourcePricesArray);

        // compute target-related data
        final JSONObject targetJsonObject = new JSONObject(targetJson);

        final JSONArray targetStationsArray = targetJsonObject.getJSONArray("stations");
        final Vector<Station> targetStations = StationsParser.fromStationsJsonArray(targetStationsArray);

        final JSONArray targetJsonOfflinesArray = targetJsonObject.getJSONArray("offlines");
        final Map<String, Boolean> targetOfflines = OfflinesParser.fromOfflinesJsonArray(targetJsonOfflinesArray);

        final JSONArray targetPricesArray = targetJsonObject.getJSONArray("prices");
        final Map<String, Price> targetPrices = PriceParser.jsonArrayToMap(targetPricesArray);

        // compute differences in stations
        final Vector<Station> modifiedStations = new Vector<>();
        for(final Station station : targetStations) {
            if(!station.equals(sourceCodeToStationsMap.get(station.getStationCode()))) {
                modifiedStations.add(station);
            }
        }

        // compute removed stations
        final Vector<Station> removedStations = new Vector<>();
        for(final Station sourceStation : sourceCodeToStationsMap.values()) {
            if(!targetStations.contains(sourceStation)) {
                removedStations.add(sourceStation);
            }
        }

        // compute differences in offlines
        final Vector<Offline> modifiedOfflines = new Vector<>();
        for(final Map.Entry<String, Boolean> targetOfflineEntry : targetOfflines.entrySet()) {
            final String stationCode = targetOfflineEntry.getKey();
            final boolean offline = targetOfflineEntry.getValue();
            if((!sourceOfflines.containsKey(stationCode)) || sourceOfflines.get(stationCode) != offline) {
                modifiedOfflines.add(new Offline(stationCode, offline));
            }
        }

        // compute differences in prices
        final Vector<Price> modifiedPrices = new Vector<>();
        for(final Map.Entry<String,Price> targetPriceEntry : targetPrices.entrySet()) {
            final Price targetPrice = targetPriceEntry.getValue();
            final Price sourcePrice = sourcePrices.get(targetPriceEntry.getKey());
            if(sourcePrice == null || different(targetPrice.getPrices(), sourcePrice.getPrices())) {
                modifiedPrices.add(targetPrice);
            }
        }

        return new Modifications(modifiedStations, removedStations, modifiedOfflines, modifiedPrices);
    }

    public static String formReplyMessage(final long fromTimestamp,
                                    final Modifications modifications,
                                    final long processedInMilliseconds,
                                    final long lastUpdated) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { \"status\": \"ok\", \"from\": ")
                .append(fromTimestamp)
                .append(", \"stations\": [");

        final Vector<Station> modifiedStations = modifications.modifiedStations;
        final Vector<Station> removedStations = modifications.removedStations;
        final Vector<Offline> modifiedOfflines = modifications.modifiedOfflines;
        final Vector<Price> modifiedPrices = modifications.modifiedPrices;
        final int numberOfModifications = modifications.getSize();

        // add station updates
        for(int i = 0; i < modifiedStations.size(); i++) {
            stringBuilder.append(StationParser.toStationJson(modifiedStations.elementAt(i))).append(i < modifiedStations.size() - 1 ? ",\n" : "\n");
        }

        stringBuilder.append("], \"removedStations\": [");

        // add removed stations
        for(int i = 0; i < removedStations.size(); i++) {
            stringBuilder.append(StationParser.toStationJson(removedStations.elementAt(i))).append(i < modifiedStations.size() - 1 ? ",\n" : "\n");
        }

        stringBuilder.append("], \"offlines\": [");

        // add offline updates
        for(int i = 0; i < modifiedOfflines.size(); i++){
            stringBuilder.append(OfflineParser.toJson(modifiedOfflines.elementAt(i))).append(i < modifiedOfflines.size() - 1 ? ",\n" : "\n");
        }

        stringBuilder.append("], \"prices\": [");

        // add price updates
        for(int i = 0; i < modifiedPrices.size(); i++){
            stringBuilder.append(PriceParser.toJson(modifiedPrices.elementAt(i))).append(i < modifiedPrices.size() - 1 ? ",\n" : "\n");
        }
        stringBuilder.append("], " +
                "\"numberOfModifications\": ").append(numberOfModifications).append(", " +
                "\"processedInMilliseconds\": ").append(processedInMilliseconds).append(", " +
                "\"lastUpdated\": ").append(lastUpdated).append(" }");

        return stringBuilder.toString();
    }

    private static boolean different(final int [] left, final int [] right) {
        if(left == null || right == null) throw new NullPointerException("Invalid null argument(s)");
        if(left.length != right.length) return true;
        for(int i = 0; i < left.length; i++) {
            if(left[i] != right[i]) return true;
        }
        return false;
    }

    public static class Modifications implements Serializable {

        private final Vector<Station> modifiedStations;
        private final Vector<Station> removedStations;
        private final Vector<Offline> modifiedOfflines;
        private final Vector<Price> modifiedPrices;

        Modifications(final Vector<Station> modifiedStations, final Vector<Station> removedStations, final Vector<Offline> modifiedOfflines, final Vector<Price> modifiedPrices) {
            this.modifiedStations = modifiedStations;
            this.removedStations = removedStations;
            this.modifiedOfflines = modifiedOfflines;
            this.modifiedPrices = modifiedPrices;
        }

        public int getSize() {
            return modifiedStations.size() + modifiedOfflines.size() + modifiedPrices.size();
        }

        public Vector<Station> getModifiedStations() {
            return modifiedStations;
        }

        public Vector<Station> getRemovedStations() {
            return removedStations;
        }

        public Vector<Offline> getModifiedOfflines() {
            return modifiedOfflines;
        }

        public Vector<Price> getModifiedPrices() {
            return modifiedPrices;
        }
    }
}