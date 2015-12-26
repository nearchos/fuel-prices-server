package com.aspectsense.fuel.server.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         22/12/2015
 *         21:14
 */
public class ApiKey {
    public static final Logger log = Logger.getLogger(ApiKey.class.getCanonicalName());

    public static final String KIND = "ApiKey";

    public static final String PROPERTY_UUID            = "uuid";
    public static final String PROPERTY_EMAIL_REQUESTER = "email_requester";
    public static final String PROPERTY_NOTE            = "note";
    public static final String PROPERTY_TIME_REQUESTED  = "time_requested";
    public static final String PROPERTY_IS_ACTIVE       = "is_active";
    public static final String PROPERTY_API_KEY         = "api_key";

    private final String uuid;
    private final String emailRequester;
    private final String note;
    private final long timeRequested;
    private final boolean isActive;
    private final String apiKey;

    public ApiKey(final String uuid, final String emailRequester, final String note, final long timeRequested, final boolean isActive, final String apiKey)
    {
        this.uuid = uuid;
        this.emailRequester = emailRequester;
        this.note = note;
        this.timeRequested = timeRequested;
        this.isActive = isActive;
        this.apiKey = apiKey;
    }

    public ApiKey(final String uuid, final String emailRequester, final String note, final long timeRequested, final boolean isActive)
    {
        this(uuid, emailRequester, note, timeRequested, isActive, UUID.randomUUID().toString());
    }

    public String getUuid() {
        return uuid;
    }

    public String getEmailRequester() {
        return emailRequester;
    }

    public String getNote() { return note; }

    public long getTimeRequested() {
        return timeRequested;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getApiKey() {
        return apiKey;
    }

    static public ApiKey getApiKey(final String keyAsString)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(keyAsString)) {
            return (ApiKey) memcacheService.get(keyAsString);
        } else {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            try {
                final Entity apiKeyEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                final ApiKey apiKey = getFromEntity(apiKeyEntity);

                memcacheService.put(keyAsString, apiKey); // add cache entry

                return apiKey;
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                return null;
            }
        }
    }

    static public void enableOrDisable(final String apiKeyS) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_API_KEY, Query.FilterOperator.EQUAL, apiKeyS);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<ApiKey> apiKeys = new Vector<ApiKey>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            apiKeys.add(getFromEntity(entity));
        }
        // assert exactly one is found
        if(apiKeys.size() == 1) {
            final ApiKey apiKey = apiKeys.elementAt(0);
            final boolean active = apiKey.isActive;
            editApiKey(apiKey.uuid, apiKey.emailRequester, apiKey.note, apiKey.timeRequested, !active, apiKey.apiKey);
        } else {
            log.warning("Found " + apiKeys.size() + " apiKeys (instead of exactly 1) for " + apiKeyS);
        }
    }

    static public boolean isActive(final String apiKeyS) {
        // todo use memcache here
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_API_KEY, Query.FilterOperator.EQUAL, apiKeyS);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(final Entity entity : preparedQuery.asIterable())
        {
            ApiKey apiKey = getFromEntity(entity);
            // assert exactly one (or none) is found
            return apiKey.isActive();
        }
        return false;
    }

    static public Vector<ApiKey> getAllApiKeys()
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_TIME_REQUESTED);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<ApiKey> apiKeys = new Vector<ApiKey>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            apiKeys.add(getFromEntity(entity));
        }

        return apiKeys;
    }

    static public Key addApiKey(final String emailRequester, final String note) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity apiKeyEntity = new Entity(KIND);
        apiKeyEntity.setProperty(PROPERTY_EMAIL_REQUESTER, emailRequester);
        apiKeyEntity.setProperty(PROPERTY_NOTE, note);
        apiKeyEntity.setProperty(PROPERTY_TIME_REQUESTED, System.currentTimeMillis());
        apiKeyEntity.setProperty(PROPERTY_IS_ACTIVE, true);
        apiKeyEntity.setProperty(PROPERTY_API_KEY, UUID.randomUUID().toString());

        return datastoreService.put(apiKeyEntity);
    }

    static public void editApiKey(final String uuid, final String emailRequester, final String note, final long timeRequested, final boolean isActive, final String apiKey) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity apiKeyEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            apiKeyEntity.setProperty(PROPERTY_EMAIL_REQUESTER, emailRequester);
            apiKeyEntity.setProperty(PROPERTY_NOTE, note);
            apiKeyEntity.setProperty(PROPERTY_TIME_REQUESTED, timeRequested);
            apiKeyEntity.setProperty(PROPERTY_IS_ACTIVE, isActive);
            apiKeyEntity.setProperty(PROPERTY_API_KEY, apiKey);
            datastoreService.put(apiKeyEntity);

            MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cache entry
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public ApiKey getFromEntity(final Entity entity)
    {
        return new ApiKey(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_EMAIL_REQUESTER),
                (String) entity.getProperty(PROPERTY_NOTE),
                (Long) entity.getProperty(PROPERTY_TIME_REQUESTED),
                (Boolean) entity.getProperty(PROPERTY_IS_ACTIVE),
                (String) entity.getProperty(PROPERTY_API_KEY));
    }
}