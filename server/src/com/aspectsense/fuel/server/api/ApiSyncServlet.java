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

import com.aspectsense.fuel.server.data.*;
import com.aspectsense.fuel.server.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            try {
                response.getWriter().println(getSyncMessageAsJSON(fromTimestamp));
            } catch (JSONException jsone) {
                response.getWriter().println(" { \"status\": \"error\", \"message\": \"" + jsone.getMessage() + "\" }");
            }
        }
    }

    public static final String SYNC_NAMESPACE = "sync";

    private String getSyncMessageAsJSON(final long fromTimestamp) throws JSONException {

        final long start = System.currentTimeMillis();

        // first check if the requested data is already in memcache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService(SYNC_NAMESPACE);
        final String memCacheKey = Long.toString(fromTimestamp);
        if(memcacheService.contains(memCacheKey)) {
            return (String) memcacheService.get(memCacheKey);
        } else { // key not found in mem-cache -- must be created now
            final String latestJson;
            final JSONObject latestJsonObject;
            if(memCacheKey.contains(Long.toString(0L))) {
                latestJson = (String) memcacheService.get(Long.toString(0L));
            } else {
                final SyncMessage latestSyncMessage = SyncMessageFactory.queryLatestSyncMessage();
                if(latestSyncMessage == null) throw new RuntimeException("Latest SyncMessage fetched is null");
                latestJson = latestSyncMessage.getJson();
                memcacheService.put(Long.toString(0L), latestJson);
            }
            latestJsonObject = new JSONObject(latestJson);
            final long lastUpdated = latestJsonObject.getLong("lastUpdated");

            {
                final String oldJson;
                final SyncMessage oldSyncMessage = SyncMessageFactory.querySyncMessage(fromTimestamp);
                if(oldSyncMessage == null) {
                    log.severe("Null SyncMessage for requested timestamp: " + fromTimestamp);
                    // todo return full sync message
                } else {
                    oldJson = oldSyncMessage.getJson();
                }
            }


            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" { \"status\": \"ok\", \"from\": ")
                    .append(fromTimestamp)
                    .append(", \"stations\": [");
            // add station updates
            {

            }
            stringBuilder.append("], \"offlines\": [");
            // add offline updates
            {
            }
            stringBuilder.append("], \"prices\": [");
            // add price updates
            {
            }
            final long finish = System.currentTimeMillis();
            stringBuilder.append("], \"processedInMilliseconds\": ").append(finish - start).append(", \"lastUpdated\": ").append(lastUpdated).append(" }");

            final String reply = stringBuilder.toString();
            memcacheService.put(memCacheKey, reply); // store in memcache

            return reply;
        }
    }
}