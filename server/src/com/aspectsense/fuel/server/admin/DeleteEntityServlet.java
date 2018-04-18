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
 * along with Cyprus Fuel Guide. If not, see <http://www.gnu.org/licenses/>.
 */

package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.UserEntity;
import com.aspectsense.fuel.server.datastore.UserEntityFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         22/12/2015
 *         21:30
 */
public class DeleteEntityServlet extends HttpServlet
{
    private Logger log = Logger.getLogger(DeleteEntityServlet.class.toString());

    public static final String PROPERTY_UUID = "uuid";
    public static final String REDIRECT_URL = "redirect-url";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        response.setContentType("text/html");

        if(user == null)
        {
            response.getWriter().print("You must sign in first");
        }
        else
        {
            final UserEntity userEntity = UserEntityFactory.getUserEntity(user.getEmail());
            if(userEntity == null || !userEntity.isAdmin())
            {
                response.getWriter().print("User '" + user.getEmail() + "' is not an admin");
            }
            else
            {
                final String key = request.getParameter(PROPERTY_UUID);
                final String redirectUrl = request.getParameter(REDIRECT_URL);

                log.warning("Deleting entity with UUID: " + key);

                deleteEntity(key);

                response.sendRedirect(URLDecoder.decode(redirectUrl, "UTF-8"));
            }
        }
    }

    static public void deleteEntity(final String uuid)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.delete(KeyFactory.stringToKey(uuid));

        MemcacheServiceFactory.getMemcacheService().delete(uuid); // invalidate cache entry
    }
}