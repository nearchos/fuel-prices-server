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

package com.aspectsense.fuel.server.data;

import com.aspectsense.fuel.server.datastore.UserEntityFactory;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         18/12/2015
 *         12:14
 */
public class UserEntity
{
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
        if(!UserEntityFactory.KIND.equals(entity.getKind()))
        {
            throw new IllegalArgumentException("Entity must be of kind: " + UserEntityFactory.KIND + " (found: " + entity.getKind() + ")");
        }

        this.email      = (String) entity.getProperty(UserEntityFactory.PROPERTY_EMAIL);
        this.nickname   = (String) entity.getProperty(UserEntityFactory.PROPERTY_NICKNAME);
        this.isAdmin    = (Boolean) entity.getProperty(UserEntityFactory.PROPERTY_IS_ADMIN);
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
            final UserEntity userEntity = UserEntityFactory.getUserEntity(user.getEmail());
            return userEntity != null && userEntity.isAdmin();
        }
    }
}