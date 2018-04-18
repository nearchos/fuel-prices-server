package com.aspectsense.fuel.server.datastore;

import com.aspectsense.fuel.server.data.SyncMessageEntity;
import com.aspectsense.fuel.server.data.SyncMessageEntity;
import com.google.appengine.api.datastore.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * @author Nearchos
 * 07-Feb-16
 */
public class SyncMessageFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "SyncMessageEntity";

    public static final String PROPERTY_JSON            = "json";
    public static final String PROPERTY_NUM_OF_CHANGES  = "num_of_changes";
    public static final String PROPERTY_LAST_UPDATED    = "last_updated";

    /**
     * Returns the latest SyncMessageEntity in the datastore, or null if none is found in the datastore
     *
     * @return the latest SyncMessageEntity in the datastore, or null if none is found in the datastore
     */
    static public SyncMessageEntity queryLatestSyncMessage() {
        final Vector<SyncMessageEntity> syncMessageEntities = queryLatestSyncMessages(1);
        if(syncMessageEntities.isEmpty()) {
            return null;
        } else {
            return syncMessageEntities.firstElement();
        }
    }

    /**
     * Returns a vector containing up to numOfMessagesToFetch of {@link SyncMessageEntity}s
     * @param numOfMessagesToFetch the number of {@link SyncMessageEntity}s to be retrieved
     * @return a vector containing up to numOfMessagesToFetch of {@link SyncMessageEntity}s
     */
    static public Vector<SyncMessageEntity> queryLatestSyncMessages(final int numOfMessagesToFetch) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_LAST_UPDATED, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // assert exactly one (or none) is found
        final FetchOptions fetchOptions = FetchOptions.Builder.withLimit(numOfMessagesToFetch);
        final List<Entity> list = preparedQuery.asList(fetchOptions);
        final Vector<SyncMessageEntity> syncMessageEntities = new Vector<>();
        for(final Entity entity : list) {
            syncMessageEntities.add(getFromEntity(entity));
        }
        return syncMessageEntities;
    }

    static public SyncMessageEntity querySyncMessage(final long lastUpdated) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_LAST_UPDATED, Query.FilterOperator.EQUAL, lastUpdated);
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
     * Returns all messages with a 'last_updated' timestamp greater than 'from' and less than or equal to 'to'.
     *
     * @param from
     * @param to
     * @param delete
     * @return
     */
    static public Map<Long, String> querySyncMessage(final long from, final long to, final boolean delete) {
        final Map<Long, String> syncMessages = new HashMap<>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterFrom = new Query.FilterPredicate(PROPERTY_LAST_UPDATED, Query.FilterOperator.GREATER_THAN, from);
        final Query.Filter filterTo = new Query.FilterPredicate(PROPERTY_LAST_UPDATED, Query.FilterOperator.LESS_THAN_OR_EQUAL, to);
        final Query.CompositeFilter filterFromTo = Query.CompositeFilterOperator.and(filterFrom, filterTo);
        final Query query = new Query(KIND).setFilter(filterFromTo).addSort(PROPERTY_LAST_UPDATED, Query.SortDirection.DESCENDING);

        final Vector<Key> allKeys = new Vector<Key>();

        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(Entity entity : preparedQuery.asIterable()) {
            final SyncMessageEntity syncMessageEntity = getFromEntity(entity);
            syncMessages.put(syncMessageEntity.getLastUpdated(), syncMessageEntity.getJson());
            allKeys.add(entity.getKey());
        }
        if(delete)
        {
            datastoreService.delete(allKeys);
        }
        return syncMessages;
    }

    static public Key addSyncMessage(Text json, int numOfChanges, long lastUpdated) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity syncMessageEntity = new Entity(KIND);
        syncMessageEntity.setProperty(PROPERTY_JSON, json);
        syncMessageEntity.setProperty(PROPERTY_NUM_OF_CHANGES, numOfChanges);
        syncMessageEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);
        // storing in the datastore
        return datastoreService.put(syncMessageEntity);
    }

    static public SyncMessageEntity getFromEntity(final Entity entity) {
        return new SyncMessageEntity(
                KeyFactory.keyToString(entity.getKey()),
                ((Text) entity.getProperty(PROPERTY_JSON)).getValue(),
                (entity.hasProperty(PROPERTY_NUM_OF_CHANGES) ? (Long) entity.getProperty(PROPERTY_NUM_OF_CHANGES) : -1L),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED));
    }
}