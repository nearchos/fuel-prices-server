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

import com.aspectsense.fuel.server.data.Prices;
import com.aspectsense.fuel.server.json.PricesParser;
import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.appengine.api.datastore.*;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         04/01/2016
 *         20:12
 */
public class PricesFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "Prices";

    public static final String PROPERTY_FUEL_TYPE       = "fuel_type";
    public static final String PROPERTY_JSON            = "json";
    public static final String PROPERTY_LAST_UPDATED    = "last_updated";

    /**
     * @param petroleumPriceDetails
     * @param fuelType
     * @return true if the data contain some actual updates for the given fuelType
     */
    public static void addPrices(final Vector<PetroleumPriceDetail> petroleumPriceDetails, final String fuelType, final long lastUpdated) {
        // add the Prices to the datastore ONLY IF non empty
        if(!petroleumPriceDetails.isEmpty()) {
            final String json = PricesParser.toPricesJson(petroleumPriceDetails);
            final Text jsonText = new Text(json);
            addPrices(fuelType, jsonText, lastUpdated);
        }
    }

    /**
     * Returns the most recent {@link Prices} instance. It first searches the memcache, and if it finds it there it
     * retrieves it. Otherwise it accesses the datastore and updates the memcache accordingly.
     * @param fuelType the fuelType for which the {@link Prices} are to be retrieved.
     * @return the most recent {@link Prices} instance, or null if that could not be found
     */
    static public Prices getLatestPrices(final String fuelType) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_FUEL_TYPE, Query.FilterOperator.EQUAL, fuelType);
        final Query query = new Query(KIND).setFilter(filter).addSort(PROPERTY_LAST_UPDATED, Query.SortDirection.DESCENDING);
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
     * Returns the most recent {@link Prices} instances.
     * @return the most recent {@link Prices} instance, or null if that could not be found
     */
    static public Vector<Prices> queryLatestPrices(final int numOfEntriesToReturn) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_LAST_UPDATED, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // assert exactly one (or none) is found
        final FetchOptions fetchOptions = FetchOptions.Builder.withLimit(numOfEntriesToReturn);
        final List<Entity> list = preparedQuery.asList(fetchOptions);
        if(!list.isEmpty()) {
            final Vector<Prices> prices = new Vector<>(numOfEntriesToReturn);
            for(final Entity entity : list) {
                prices.add(getFromEntity(entity));
            }
            return prices;
        } else {
            return null;
        }
    }

    static public Key addPrices(String fuelType, Text json, long lastUpdated) {

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity pricesEntity = new Entity(KIND);

        pricesEntity.setProperty(PROPERTY_FUEL_TYPE, fuelType);
        pricesEntity.setProperty(PROPERTY_JSON, json);
        pricesEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

        // storing in the datastore
        return datastoreService.put(pricesEntity);
    }

    static public Prices getFromEntity(final Entity entity) {
        return new Prices(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_FUEL_TYPE),
                ((Text) entity.getProperty(PROPERTY_JSON)).getValue(),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED));
    }
}