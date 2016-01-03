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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         29/12/2015
 *         22:46
 */
public class PriceFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static final String KIND = "Price";

    public static final String PROPERTY_STATION_CODE            = "station_code";
    public static final String PROPERTY_PRICE_MODIFICATION_DATE = "price_modification_date";
    public static final String PROPERTY_FUEL_TYPE               = "fuel_type";
    public static final String PROPERTY_FUEL_PRICE              = "fuel_price";
    public static final String PROPERTY_LAST_UPDATED            = "last_updated";

    static public Price getPrice(final String uuid) {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(uuid)) {
            return (Price) memcacheService.get(uuid);
        } else {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try {
                final Entity priceEntity = datastoreService.get(KeyFactory.stringToKey(uuid));

                final Price price = getFromEntity(priceEntity);
                memcacheService.put(uuid, price); // add cache entry
                return price;
            } catch (EntityNotFoundException enfe) {
                log.severe("Could not find " + KIND + " with key: " + uuid);
                return null;
            }
        }
    }

    static public Map<String, Price> getPricesByStationCode(final String stationCode) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_STATION_CODE, Query.FilterOperator.EQUAL, stationCode);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Map<String, Price> map = new HashMap<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            final Price price = getFromEntity(entity);
            map.put(price.getFuelType(), price);
        }
        return map;
    }

    static public Map<String, Price> getStationCodesToPricesByFuelType(final String fuelType) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_FUEL_TYPE, Query.FilterOperator.EQUAL, fuelType);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Map<String, Price> map = new HashMap<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            final Price price = getFromEntity(entity);
            map.put(price.getStationCode(), price);
        }
        return map;
    }

    static public Vector<Price> getAllPrices() {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Price> prices = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            prices.add(getFromEntity(entity));
        }
        return prices;
    }

    static public Vector<Price> getAllPrices(final String fuelType) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_FUEL_TYPE, Query.FilterOperator.EQUAL, fuelType);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Price> prices = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            prices.add(getFromEntity(entity));
        }
        return prices;
    }

    static public Key addPrice(String stationCode, String priceModificationDate, String fuelType, String fuelPrice) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity priceEntity = new Entity(KIND);

        priceEntity.setProperty(PROPERTY_STATION_CODE, stationCode);
        priceEntity.setProperty(PROPERTY_PRICE_MODIFICATION_DATE, priceModificationDate);
        priceEntity.setProperty(PROPERTY_FUEL_TYPE, fuelType);
        priceEntity.setProperty(PROPERTY_FUEL_PRICE, fuelPrice);
        priceEntity.setProperty(PROPERTY_LAST_UPDATED, System.currentTimeMillis());

        return datastoreService.put(priceEntity);
    }

    static public void editPrice(String uuid, String stationCode, String priceModificationDate, String fuelType, String fuelPrice) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity priceEntity = datastoreService.get(KeyFactory.stringToKey(uuid));

            priceEntity.setProperty(PROPERTY_STATION_CODE, stationCode);
            priceEntity.setProperty(PROPERTY_PRICE_MODIFICATION_DATE, priceModificationDate);
            priceEntity.setProperty(PROPERTY_FUEL_TYPE, fuelType);
            priceEntity.setProperty(PROPERTY_FUEL_PRICE, fuelPrice);
            priceEntity.setProperty(PROPERTY_LAST_UPDATED, System.currentTimeMillis());

            datastoreService.put(priceEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cache entry
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Price getFromEntity(final Entity entity) {
        return new Price(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_STATION_CODE),
                (String) entity.getProperty(PROPERTY_PRICE_MODIFICATION_DATE),
                (String) entity.getProperty(PROPERTY_FUEL_TYPE),
                (String) entity.getProperty(PROPERTY_FUEL_PRICE),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED));
    }
}