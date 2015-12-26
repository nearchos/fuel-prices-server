package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.UserEntity;
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
public class DeleteEntity extends HttpServlet
{
    private Logger log = Logger.getLogger(DeleteEntity.class.toString());

    public static final String PROPERTY_UUID = "uuid";
    public static final String REDIRECT_URL = "redirect-url";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
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
            final UserEntity userEntity = UserEntity.getUserEntity(user.getEmail());
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