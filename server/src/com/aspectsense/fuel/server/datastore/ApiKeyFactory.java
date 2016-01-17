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

import com.aspectsense.fuel.server.data.ApiKey;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         01/01/2016
 *         14:42
 */
public class ApiKeyFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static final String KIND = "ApiKey";

    public static final String PROPERTY_EMAIL_REQUESTER = "email_requester";
    public static final String PROPERTY_NOTE            = "note";
    public static final String PROPERTY_TIME_REQUESTED  = "time_requested";
    public static final String PROPERTY_IS_ACTIVE       = "is_active";
    public static final String PROPERTY_API_KEY_CODE    = "api_key";

    static public ApiKey getApiKey(final String uuid) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity apiKeyEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            return getFromEntity(apiKeyEntity);
        } catch (EntityNotFoundException enfe) {
            log.severe("Could not find " + KIND + " with key: " + uuid);
            return null;
        }
    }

    static public ApiKey getApiKeyByCode(final String apiKeyCode)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains("ApiKeyCode-" + apiKeyCode)) {
            return (ApiKey) memcacheService.get("ApiKeyCode-" + apiKeyCode);
        } else {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            final Query.Filter filter = new Query.FilterPredicate(PROPERTY_API_KEY_CODE, Query.FilterOperator.EQUAL, apiKeyCode);
            final Query query = new Query(KIND).setFilter(filter);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            // assert exactly one (or none) is found
            log.info("looking up apiKey with apiKeyCode: " + apiKeyCode);
            final Iterator<Entity> iterator = preparedQuery.asIterable().iterator();
            if(iterator.hasNext()) {
                final Entity apiKeyEntity = iterator.next();
                final ApiKey apiKey = getFromEntity(apiKeyEntity);
                memcacheService.put("ApiKeyCode-" + apiKeyCode, apiKey); // add cache entry
                return apiKey;
            } else {
                log.severe("Could not find " + KIND + " with apiKeyCode: " + apiKeyCode);
                return null;
            }
        }
    }

    static public void enableOrDisable(final String apiKeyCode) {
        final ApiKey apiKey = getApiKeyByCode(apiKeyCode);
        if(apiKey != null) {
            final boolean active = apiKey.isActive();
            editApiKey(apiKey.getEmailRequester(), apiKey.getNote(), apiKey.getTimeRequested(), !active, apiKey.getApiKeyCode());
        } else {
            log.warning("Could not find " + KIND + " for apiKeyCode: " + apiKeyCode);
        }
    }

    static public boolean isActive(final String apiKeyCode) {
        final ApiKey apiKey = getApiKeyByCode(apiKeyCode);
        return apiKey != null && apiKey.isActive();
    }

    static public Vector<ApiKey> getAllApiKeys()
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_TIME_REQUESTED);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<ApiKey> apiKeys = new Vector<>();
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
        apiKeyEntity.setProperty(PROPERTY_API_KEY_CODE, UUID.randomUUID().toString());

        // clean up mem-cache to force it ti rebuild next time is needed
        MemcacheServiceFactory.getMemcacheService().clearAll();

        return datastoreService.put(apiKeyEntity);
    }

    static public void editApiKey(final String emailRequester, final String note, final long timeRequested, final boolean isActive, final String apiKeyCode) {
        final ApiKey apiKey = getApiKeyByCode(apiKeyCode);
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        if(apiKey != null) {
            try {
                final Entity apiKeyEntity = datastoreService.get(KeyFactory.stringToKey(apiKey.getUuid()));
                apiKeyEntity.setProperty(PROPERTY_EMAIL_REQUESTER, emailRequester);
                apiKeyEntity.setProperty(PROPERTY_NOTE, note);
                apiKeyEntity.setProperty(PROPERTY_TIME_REQUESTED, timeRequested);
                apiKeyEntity.setProperty(PROPERTY_IS_ACTIVE, isActive);
                apiKeyEntity.setProperty(PROPERTY_API_KEY_CODE, apiKey);
                datastoreService.put(apiKeyEntity);

                // clean up mem-cache to force it to rebuild next time is needed
                MemcacheServiceFactory.getMemcacheService().clearAll();
            } catch (EntityNotFoundException enfe) {
                log.severe("Could not find " + KIND + " with key: " + apiKey.getUuid());
            }
        } else {
            log.severe("Could not find " + KIND + " with apiKeyCode: " + apiKeyCode);
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
                (String) entity.getProperty(PROPERTY_API_KEY_CODE));
    }
}