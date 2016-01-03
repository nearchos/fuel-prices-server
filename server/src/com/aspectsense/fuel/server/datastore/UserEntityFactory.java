package com.aspectsense.fuel.server.datastore;

import com.aspectsense.fuel.server.data.UserEntity;
import com.google.appengine.api.datastore.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         03/01/2016
 *         22:05
 */
public class UserEntityFactory {
    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static final String KIND = "User";

    public static final String PROPERTY_EMAIL       = "email";
    public static final String PROPERTY_NICKNAME    = "nickname";
    public static final String PROPERTY_IS_ADMIN    = "is_admin";

    static public UserEntity getUserEntity(final String email)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).setFilter(new Query.FilterPredicate(UserEntityFactory.PROPERTY_EMAIL, Query.FilterOperator.EQUAL, email));
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final List<Entity> userEntities = preparedQuery.asList(FetchOptions.Builder.withDefaults());
        if(userEntities.size() == 0)
        {
            log.info("Could not find user with email: " + email);
            return null;
        }
        else if(userEntities.size() == 1) // todo
        {
            return new UserEntity(userEntities.get(0));
        }
        else
        {
            log.severe("More than 1 entities for email: " + email);
            return new UserEntity(userEntities.get(0));
        }
    }

    static public UserEntity setUserEntity(final String email, final String nickname, final boolean isAdmin, final boolean isTrainer)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity userEntity = new Entity(KIND);
        userEntity.setProperty(PROPERTY_EMAIL, email);
        userEntity.setProperty(PROPERTY_NICKNAME, nickname);
        userEntity.setProperty(PROPERTY_IS_ADMIN, isAdmin);

        datastoreService.put(userEntity);

        return new UserEntity(email, nickname, isAdmin);
    }
}