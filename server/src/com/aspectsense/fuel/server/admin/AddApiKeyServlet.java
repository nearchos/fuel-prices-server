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
 *         22:00
 */
public class AddApiKeyServlet extends HttpServlet {

    Logger log = Logger.getLogger("cyprusfuelguide");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String note = request.getParameter(ApiKeyFactory.PROPERTY_NOTE);
        UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();
        log.info("AddApiKeyServlet - user: " + user);
        if(userService.isUserLoggedIn()) {
            final UserEntity userEntity = UserEntityFactory.getUserEntity(user.getEmail());
            if(userEntity != null && userEntity.isAdmin()) {
                final String userEmail = user.getEmail();
                ApiKeyFactory.addApiKey(userEmail, note);
                response.sendRedirect("/admin/parameters");
            } else {
                response.getWriter().println("{ \"status\": \"error\", \"message\": \"logged in user is not admin\" }");
            }
        } else {
            response.getWriter().println("{ \"status\": \"error\", \"message\": \"no user is logged in\" }");
        }
    }
}