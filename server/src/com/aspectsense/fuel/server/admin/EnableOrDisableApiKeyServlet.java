package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.UserEntity;
import com.aspectsense.fuel.server.datastore.ApiKeyFactory;
import com.aspectsense.fuel.server.datastore.UserEntityFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         26/12/2015
 *         22:35
 */
public class EnableOrDisableApiKeyServlet extends HttpServlet {
    Logger log = Logger.getLogger("cyprusfuelguide");
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String apiKey = request.getParameter(ApiKeyFactory.PROPERTY_API_KEY_CODE);
        UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();
        log.info("EnableOrDisableApiKeyServlet - user: " + user);
        if(userService.isUserLoggedIn()) {
            final UserEntity userEntity = UserEntityFactory.getUserEntity(user.getEmail());
            if(userEntity != null && userEntity.isAdmin()) {
                ApiKeyFactory.enableOrDisable(apiKey);
                response.sendRedirect("/admin/parameters");
            } else {
                response.getWriter().println("{ \"status\": \"error\", \"message\": \"logged in user is not admin\" }");
            }
        } else {
            response.getWriter().println("{ \"status\": \"error\", \"message\": \"no user is logged in\" }");
        }
    }
}
