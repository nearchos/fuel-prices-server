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