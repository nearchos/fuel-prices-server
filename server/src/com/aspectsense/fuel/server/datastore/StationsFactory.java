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

package com.aspectsense.fuel.server.datastore;

import com.aspectsense.fuel.server.data.Stations;
import com.aspectsense.fuel.server.json.StationsParser;
import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.appengine.api.datastore.*;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         03/02/2016
 *         19:26
 */
public class StationsFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "Stations";

    public static final String PROPERTY_JSON            = "json";
    public static final String PROPERTY_LAST_UPDATED    = "last_updated";

    public static void addStations(final Vector<PetroleumPriceDetail> petroleumPriceDetails, final long lastUpdated) {
        // add the Stations to the datastore ONLY IF non empty
        if(!petroleumPriceDetails.isEmpty()) {
            final String json = StationsParser.toStationsJson(petroleumPriceDetails);
            final Text jsonText = new Text(json);
            addStations(jsonText, lastUpdated);
        }
    }

    /**
     * Returns the most recent {@link Stations} instance. It accesses the datastore and updates the memcache
     * accordingly.
     *
     * @return the most recent {@link Stations} instance, or null if that could not be found
     */
    static public Stations getLatestStations() {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_LAST_UPDATED, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // assert exactly one (or none) is found
        final FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1);
        final List<Entity> list = preparedQuery.asList(fetchOptions);
        if(!list.isEmpty()) {
            return getFromEntity(list.get(0));
        } else {
            return null;
        }
    }

    /**
     * Retrieves the latest {@link Stations} with a timestamp not later than the given one.
     * @return the latest {@link Stations} with a timestamp not later than the given one.
     */
    static public Stations getStationsByDate(final long timestamp) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND)
                .setFilter(new Query.FilterPredicate(PROPERTY_LAST_UPDATED, Query.FilterOperator.LESS_THAN_OR_EQUAL, timestamp))
                .addSort(PROPERTY_LAST_UPDATED, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // assert exactly one (or none) is found
        final FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1);
        final List<Entity> list = preparedQuery.asList(fetchOptions);
        if(!list.isEmpty()) {
            return getFromEntity(list.get(0));
        } else {
            return null;
        }
    }

    static public Key addStations(Text json, long lastUpdated) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity stationsEntity = new Entity(KIND);
        stationsEntity.setProperty(PROPERTY_JSON, json);
        stationsEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);
        // storing in the datastore
        return datastoreService.put(stationsEntity);
    }

    static public Stations getFromEntity(final Entity entity) {
        return new Stations(
                KeyFactory.keyToString(entity.getKey()),
                ((Text) entity.getProperty(PROPERTY_JSON)).getValue(),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED));
    }

    static public int deleteStations(final long notNewerThan, final int maxNumOfEntitiesToBeDeleted) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND)
                .setFilter(new Query.FilterPredicate(PROPERTY_LAST_UPDATED, Query.FilterOperator.LESS_THAN_OR_EQUAL, notNewerThan))
                .addSort(PROPERTY_LAST_UPDATED, Query.SortDirection.ASCENDING); // start from oldest
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final List<Entity> entities = preparedQuery.asList(FetchOptions.Builder.withLimit(maxNumOfEntitiesToBeDeleted));
        final List<Key> keys = new Vector<>();
        for(final Entity entity : entities) {
            keys.add(entity.getKey());
        }
        datastoreService.delete(keys);
        return keys.size();
    }
}