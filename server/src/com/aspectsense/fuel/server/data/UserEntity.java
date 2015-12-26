package com.aspectsense.fuel.server.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;

import java.util.List;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         18/12/2015
 *         12:14
 */
public class UserEntity
{
    public static final Logger log = Logger.getLogger(UserEntity.class.getCanonicalName());

    public static final String KIND = "User";

    public static final String PROPERTY_EMAIL       = "email";
    public static final String PROPERTY_NICKNAME    = "nickname";
    public static final String PROPERTY_IS_ADMIN    = "is_admin";

    private final String email;
    private final String nickname;
    private final boolean isAdmin;

    public UserEntity(final String email, final String nickname, final boolean isAdmin)
    {
        this.email      = email;
        this.nickname   = nickname;
        this.isAdmin    = isAdmin;
    }

    public UserEntity(final Entity entity)
    {
        if(!KIND.equals(entity.getKind()))
        {
            throw new IllegalArgumentException("Entity must be of kind: " + KIND + " (found: " + entity.getKind() + ")");
        }

        this.email      = (String) entity.getProperty(PROPERTY_EMAIL);
        this.nickname   = (String) entity.getProperty(PROPERTY_NICKNAME);
        this.isAdmin    = (Boolean) entity.getProperty(PROPERTY_IS_ADMIN);
    }

    public String getEmail()
    {
        return email;
    }

    public String getNickname()
    {
        return nickname;
    }

    public boolean isAdmin() { return isAdmin; }

    static public boolean isAdmin(final User user) {
        if(user == null) {
            return false;
        } else {
            final UserEntity userEntity = getUserEntity(user.getEmail());
            return userEntity != null && userEntity.isAdmin();
        }
    }

    static public UserEntity getUserEntity(final String email)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).setFilter(new Query.FilterPredicate(UserEntity.PROPERTY_EMAIL, Query.FilterOperator.EQUAL, email));
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