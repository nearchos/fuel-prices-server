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
        final List<Entity> userEntities = preparedQuery.asList(FetchOptions.Builder.withLimit(1));
        if(userEntities.size() == 0)
        {
            log.info("Could not find user with email: " + email);
            return null;
        }
        else // if(userEntities.size() == 1)
        {
            return new UserEntity(userEntities.get(0));
        }
    }

    static public UserEntity setUserEntity(final String email, final String nickname, final boolean isAdmin)
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