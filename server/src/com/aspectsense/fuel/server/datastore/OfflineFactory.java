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

package com.aspectsense.fuel.server.datastore;

import com.aspectsense.fuel.server.data.Offline;
import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.*;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         17/01/2016
 *         12:35
 */
public class OfflineFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "Offline";

    public static final String PROPERTY_STATION_CODE    = "station_code";
    public static final String PROPERTY_IS_OFFLINE      = "is_offline";
    public static final String PROPERTY_LAST_UPDATED    = "last_updated";

    public static Map<String,Offline> getAllOfflines(final long lastUpdated) {

        final Map<String,Offline> stationCodesToOfflines = new HashMap<>();
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_LAST_UPDATED, Query.FilterOperator.GREATER_THAN, lastUpdated);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // process all entities found
        final Iterator<Entity> iterator = preparedQuery.asIterator();
        while(iterator.hasNext()) {
            final Offline offline = getFromEntity(iterator.next());
            final String stationCode = offline.getStationCode();
            stationCodesToOfflines.put(stationCode, offline);
        }

        return stationCodesToOfflines;
    }

    public static void updateOfflines(final Vector<PetroleumPriceDetail> petroleumPriceDetails, final long updateTimestamp) {
        for(final PetroleumPriceDetail petroleumPriceDetail : petroleumPriceDetails) {
            final String stationCode = petroleumPriceDetail.getStationCode();
            final boolean isOffline = petroleumPriceDetail.isOffline();
            addOrUpdateOffline(stationCode, isOffline, updateTimestamp);
        }
    }

    public static boolean addOrUpdateOffline(final String stationCode, final boolean isOffline, final long lastUpdated) {
        final Offline offline = getLatestOffline(stationCode);
        if(offline == null) { // if this is the first time the offline is stored, just add it
            addOffline(stationCode, isOffline, lastUpdated);
            return true;
        } else { // check if an update is needed
            if(offline.isOffline() != isOffline) {
                Key key = updateOffline(stationCode, isOffline, lastUpdated);
                // clean up mem-cache to force it ti rebuild next time is needed
                MemcacheServiceFactory.getMemcacheService().clearAll();
                return true;
            } else { // else no need to do anything
                return false;
            }
        }
    }

    /**
     * Returns the {@link Offline} which corresponds to the given stationCode. If that is in the memcache, then the
     * cached version is returned. Otherwise the value is fetched from the datastore and the memcache is updated.
     *
     * @param stationCode
     * @return
     */
    static public Offline getLatestOffline(final String stationCode) {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        final String memCacheKey = "offline-" + stationCode;
        if(memcacheService.contains(memCacheKey)) {
            return (Offline) memcacheService.get(memCacheKey);
        } else {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            final Query.Filter filter = new Query.FilterPredicate(PROPERTY_STATION_CODE, Query.FilterOperator.EQUAL, stationCode);
            final Query query = new Query(KIND).setFilter(filter);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            // assert exactly one (or none) is found
            final FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1);
            final List<Entity> list = preparedQuery.asList(fetchOptions);
            Offline offline;
            if(!list.isEmpty()) {
                offline = getFromEntity(list.get(0));
                memcacheService.put(memCacheKey, offline);
            } else {
                offline = null;
            }
            return offline;
        }
    }

    static public Key addOffline(final String stationCode, final boolean isOffline, final long lastUpdated) {

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity offlineEntity = new Entity(KIND);

        offlineEntity.setProperty(PROPERTY_STATION_CODE, stationCode);
        offlineEntity.setProperty(PROPERTY_IS_OFFLINE, isOffline);
        offlineEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

        // clean up mem-cache to force it ti rebuild next time is needed
        MemcacheServiceFactory.getMemcacheService().clearAll();

        // storing in the datastore
        return datastoreService.put(offlineEntity);
    }

    static private Key updateOffline(final String uuid, final boolean isOffline, final long lastUpdated) {

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity offlineEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            offlineEntity.setProperty(PROPERTY_IS_OFFLINE, isOffline);
            offlineEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

            // clean up mem-cache to force it ti rebuild next time is needed
            MemcacheServiceFactory.getMemcacheService().clearAll();

            // storing in the datastore
            return datastoreService.put(offlineEntity);
        } catch (EntityNotFoundException enfe) {
            log.severe(enfe.getMessage());
            return null;
        }
    }

    static public Offline getFromEntity(final Entity entity) {
        return new Offline(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_STATION_CODE),
                (Boolean) entity.getProperty(PROPERTY_IS_OFFLINE),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED));
    }
}