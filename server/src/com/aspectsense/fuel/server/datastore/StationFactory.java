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

import com.aspectsense.fuel.server.data.Station;
import com.google.appengine.api.datastore.*;

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
public class StationFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static final String KIND = "Station";

    public static final String PROPERTY_COMPANY_CODE        = "company_code";
    public static final String PROPERTY_COMPANY_NAME        = "company_name";
    public static final String PROPERTY_STATION_CODE        = "station_code";
    public static final String PROPERTY_STATION_NAME        = "station_name";
    public static final String PROPERTY_STATION_TEL_NO      = "station_tel_no";
    public static final String PROPERTY_STATION_CITY        = "station_city";
    public static final String PROPERTY_STATION_DISTRICT    = "station_district";
    public static final String PROPERTY_STATION_ADDRESS     = "station_address";
    public static final String PROPERTY_STATION_LATITUDE    = "station_latitude";
    public static final String PROPERTY_STATION_LONGITUDE   = "station_longitude";
    public static final String PROPERTY_LAST_UPDATED        = "last_updated";

//    static public Station getStation(final String uuid) {
//        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
//        if(memcacheService.contains(uuid)) {
//            return (Station) memcacheService.get(uuid);
//        } else {
//            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
//            try {
//                final Entity stationEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
//
//                final Station station = getFromEntity(stationEntity);
//
//                memcacheService.put(uuid, station); // add cache entry
//
//                return station;
//            } catch (EntityNotFoundException enfe) {
//                log.severe("Could not find " + KIND + " with key: " + uuid);
//                return null;
//            }
//        }
//    }

//    static public Station getStationByCode(final String stationCode) {
//        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
//        // check memcache first
//        if(memcacheService.contains(stationCode)) {
//            return (Station) memcacheService.get(stationCode);
//        } else {
//            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
//            final Query.Filter filter = new Query.FilterPredicate(PROPERTY_STATION_CODE, Query.FilterOperator.EQUAL, stationCode);
//            final Query query = new Query(KIND).setFilter(filter);
//            final PreparedQuery preparedQuery = datastoreService.prepare(query);
//            // assert exactly one (or none) is found
//            final Iterator<Entity> iterator = preparedQuery.asIterable().iterator();
//            if(iterator.hasNext()) {
//                final Station station = getFromEntity(iterator.next());
//                // update memcache
//                memcacheService.put(stationCode, station);
//                return station;
//            }
//            return null;
//        }
//    }

    static public Vector<Station> getAllStations() {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Station> stations = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            stations.add(getFromEntity(entity));
        }
        return stations;
    }

    /**
     * Retrieve all stations that have changed since the last update
     *
     * @param lastUpdated
     * @return
     */
    static public Map<String,Station> getAllStationCodesToStations(final long lastUpdated) {

        // todo utilize memcache
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_LAST_UPDATED, Query.FilterOperator.GREATER_THAN, lastUpdated);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Map<String,Station> stations = new HashMap<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            final Station station = getFromEntity(entity);
            stations.put(station.getStationCode(), station);
        }
        return stations;
    }

    static public Key addStation(String fuelCompanyCode, String fuelCompanyName, String stationCode, String stationName,
                                 String stationTelNo, String stationCity, String stationDistrict, String stationAddress,
                                 String stationLatitude, String stationLongitude) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity stationEntity = new Entity(KIND);
        stationEntity.setProperty(PROPERTY_COMPANY_CODE, fuelCompanyCode);
        stationEntity.setProperty(PROPERTY_COMPANY_NAME, fuelCompanyName);
        stationEntity.setProperty(PROPERTY_STATION_CODE, stationCode);
        stationEntity.setProperty(PROPERTY_STATION_NAME, sanitizeForJSON(stationName));
        stationEntity.setProperty(PROPERTY_STATION_TEL_NO, sanitizeForJSON(stationTelNo));
        stationEntity.setProperty(PROPERTY_STATION_CITY, sanitizeForJSON(stationCity));
        stationEntity.setProperty(PROPERTY_STATION_DISTRICT, sanitizeForJSON(stationDistrict));
        stationEntity.setProperty(PROPERTY_STATION_ADDRESS, sanitizeForJSON(stationAddress));
        stationEntity.setProperty(PROPERTY_STATION_LATITUDE, stationLatitude);
        stationEntity.setProperty(PROPERTY_STATION_LONGITUDE, stationLongitude);
        stationEntity.setProperty(PROPERTY_LAST_UPDATED, System.currentTimeMillis());

        return datastoreService.put(stationEntity);
    }

    static public void editStation(String uuid, String fuelCompanyCode, String fuelCompanyName, String stationName,
                                   String stationTelNo, String stationCity, String stationDistrict,
                                   String stationAddress, String stationLatitude, String stationLongitude) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity stationEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            stationEntity.setProperty(PROPERTY_COMPANY_CODE, fuelCompanyCode);
            stationEntity.setProperty(PROPERTY_COMPANY_NAME, fuelCompanyName);
            stationEntity.setProperty(PROPERTY_STATION_NAME, sanitizeForJSON(stationName));
            stationEntity.setProperty(PROPERTY_STATION_TEL_NO, sanitizeForJSON(stationTelNo));
            stationEntity.setProperty(PROPERTY_STATION_CITY, sanitizeForJSON(stationCity));
            stationEntity.setProperty(PROPERTY_STATION_DISTRICT, sanitizeForJSON(stationDistrict));
            stationEntity.setProperty(PROPERTY_STATION_ADDRESS, sanitizeForJSON(stationAddress));
            stationEntity.setProperty(PROPERTY_STATION_LATITUDE, stationLatitude);
            stationEntity.setProperty(PROPERTY_STATION_LONGITUDE, stationLongitude);

            datastoreService.put(stationEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Station getFromEntity(final Entity entity) {
        return new Station(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_COMPANY_CODE),
                (String) entity.getProperty(PROPERTY_COMPANY_NAME),
                (String) entity.getProperty(PROPERTY_STATION_CODE),
                (String) entity.getProperty(PROPERTY_STATION_NAME),
                (String) entity.getProperty(PROPERTY_STATION_TEL_NO),
                (String) entity.getProperty(PROPERTY_STATION_CITY),
                (String) entity.getProperty(PROPERTY_STATION_DISTRICT),
                (String) entity.getProperty(PROPERTY_STATION_ADDRESS),
                (String) entity.getProperty(PROPERTY_STATION_LATITUDE),
                (String) entity.getProperty(PROPERTY_STATION_LONGITUDE),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED));
    }

    public static String sanitizeForJSON(String unsanitized) {
        if(unsanitized == null) {
            return null;
        } else {
            while(unsanitized.startsWith("\"")) unsanitized = unsanitized.substring(1, unsanitized.length());
            while(unsanitized.endsWith("\"")) unsanitized = unsanitized.substring(0, unsanitized.length() - 1);
            return unsanitized.trim().replaceAll("\"", "'");
        }
    }
}