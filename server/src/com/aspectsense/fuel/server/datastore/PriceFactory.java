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

import com.aspectsense.fuel.server.data.Price;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.*;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         04/01/2016
 *         20:12
 */
public class PriceFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "Price";

    public static final String PROPERTY_STATION_CODE                = "station_code";
    public static final String PROPERTY_FUEL_TYPE                   = "fuel_type";
    public static final String PROPERTY_FUEL_PRICE_IN_MILLIEUROS    = "fuel_price";
    public static final String PROPERTY_LAST_UPDATED                = "last_updated";

    /**
     * Replaces the stored value only if the price is changed. Also updates the memcache.
     * @param stationCode
     * @param fuelType
     * @param fuelPriceInMillieuro
     * @param lastUpdated
     * @return true if and only if the price has changed
     */
    public static boolean addOrUpdatePrice(final String stationCode, final String fuelType, final int fuelPriceInMillieuro, final long lastUpdated) {
        final Price price = getLatestPrice(stationCode, fuelType);
        if(price == null) { // if this is the first time the price is stored, just add it
            addPrice(stationCode, fuelType, fuelPriceInMillieuro, lastUpdated);
            return true;
        } else { // check if an update is needed
            if(price.getFuelPriceInMillieuros() != fuelPriceInMillieuro) {
                updatePrice(price.getUuid(), fuelPriceInMillieuro, lastUpdated);

                // clean up mem-cache to force it to rebuild next time is needed
                MemcacheServiceFactory.getMemcacheService().clearAll();

                return true;
            } else { // else no need to do anything
                return false;
            }
        }
    }

    /**
     * Returns the {@link Price} which corresponds to the given stationCode and fuelType. If that is in the memcache,
     * then the cached version is returned. Otherwise the value is fetched from the datastore and the memcache is
     * updated.
     *
     * @param stationCode
     * @param fuelType
     * @return
     */
    static public Price getLatestPrice(final String stationCode, final String fuelType) {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        final String memCacheKey = stationCode + "-" + fuelType;
        if(memcacheService.contains(memCacheKey)) {
            return (Price) memcacheService.get(memCacheKey);
        } else {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            final Query.Filter filterStationCode = new Query.FilterPredicate(PROPERTY_STATION_CODE, Query.FilterOperator.EQUAL, stationCode);
            final Query.Filter filterFuelType = new Query.FilterPredicate(PROPERTY_FUEL_TYPE, Query.FilterOperator.EQUAL, fuelType);
            final Query.CompositeFilter compositeFilter = Query.CompositeFilterOperator.and(filterStationCode, filterFuelType);
            final Query query = new Query(KIND).setFilter(compositeFilter);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            // assert exactly one (or none) is found
            final FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1);
            final List<Entity> list = preparedQuery.asList(fetchOptions);
            Price price;
            if(!list.isEmpty()) {
                price = getFromEntity(list.get(0));
                memcacheService.put(memCacheKey, price);
            } else {
                price = null;
            }
            return price;
        }
    }

    static public Map<String,Vector<Price>> getAllPrices(final long lastUpdated) {

        final Map<String,Vector<Price>> stationCodesToPrices = new HashMap<>();
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_LAST_UPDATED, Query.FilterOperator.GREATER_THAN, lastUpdated);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // process all entities found
        final Iterator<Entity> iterator = preparedQuery.asIterator();
        while(iterator.hasNext()) {
            final Price price = getFromEntity(iterator.next());
            final String stationCode = price.getStationCode();
            Vector<Price> prices = stationCodesToPrices.get(stationCode);
            if(prices == null) {
                prices = new Vector<>();
                stationCodesToPrices.put(stationCode, prices);
            }
            prices.add(price);
        }

        return stationCodesToPrices;
    }

    static public Key addPrice(final String stationCode, final String fuelType, final int priceInMillieuros, final long lastUpdated) {

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity priceEntity = new Entity(KIND);

        priceEntity.setProperty(PROPERTY_STATION_CODE, stationCode);
        priceEntity.setProperty(PROPERTY_FUEL_TYPE, fuelType);
        priceEntity.setProperty(PROPERTY_FUEL_PRICE_IN_MILLIEUROS, priceInMillieuros);
        priceEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

        // clean up mem-cache to force it ti rebuild next time is needed
        MemcacheServiceFactory.getMemcacheService().clearAll();

        // storing in the datastore
        return datastoreService.put(priceEntity);
    }

    static private Key updatePrice(final String uuid, final int priceInMillieuros, final long lastUpdated) {

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity pricesEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            pricesEntity.setProperty(PROPERTY_FUEL_PRICE_IN_MILLIEUROS, priceInMillieuros);
            pricesEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

            // storing in the datastore
            return datastoreService.put(pricesEntity);
        } catch (EntityNotFoundException enfe) {
            log.severe(enfe.getMessage());
            return null;
        }
    }

    static public Price getFromEntity(final Entity entity) {
        return new Price(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_STATION_CODE),
                (String) entity.getProperty(PROPERTY_FUEL_TYPE),
                (Long) entity.getProperty(PROPERTY_FUEL_PRICE_IN_MILLIEUROS),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED));
    }
}