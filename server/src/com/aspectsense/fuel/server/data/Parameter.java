package com.aspectsense.fuel.server.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         26/12/2015
 *         22:59
 */
public class Parameter {
    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static final String KIND = "Parameter";

    public static final String PROPERTY_UUID    = "uuid";
    public static final String PROPERTY_NAME    = "name";
    public static final String PROPERTY_VALUE   = "value";

    private final String uuid;
    private final String name;
    private final String value;

    public Parameter(final String uuid, final String name, final String value) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public String getParameterName() {
        return name;
    }

    public String getParameterValue() {
        return value;
    }

    static public Vector<Parameter> getAllParameters() {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_NAME);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Parameter> parameters = new Vector<Parameter>();
        for(final Entity entity : preparedQuery.asIterable()) {
            parameters.add(getFromEntity(entity));
        }

        return parameters;
    }

    static public Parameter getParameterByName(final String name) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_NAME, Query.FilterOperator.EQUAL, name);
        query.setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Parameter> parameters = new Vector<Parameter>();
        for(final Entity entity : preparedQuery.asIterable()) {
            parameters.add(getFromEntity(entity));
        }

        if(parameters.size() < 1) {
            return null;
        } else {
            return parameters.elementAt(0);
        }
    }

    static public Key addParameter(final String name, final String value) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity parameterEntity = new Entity(KIND);
        parameterEntity.setProperty(PROPERTY_NAME, name);
        parameterEntity.setProperty(PROPERTY_VALUE, value);

        return datastoreService.put(parameterEntity);
    }

    static public void editParameter(final String uuid, final String name, final String value) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity parameterEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            parameterEntity.setProperty(PROPERTY_NAME, name);
            parameterEntity.setProperty(PROPERTY_VALUE, value);
            datastoreService.put(parameterEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cache entry
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Parameter getFromEntity(final Entity entity) {
        return new Parameter(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_NAME),
                (String) entity.getProperty(PROPERTY_VALUE));
    }
}