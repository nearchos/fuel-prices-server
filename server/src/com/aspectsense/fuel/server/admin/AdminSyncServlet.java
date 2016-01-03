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

package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.ApiKey;
import com.aspectsense.fuel.server.data.Parameter;
import com.aspectsense.fuel.server.datastore.ApiKeyFactory;
import com.aspectsense.fuel.server.datastore.ParameterFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
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
 *         01/01/2016
 *         15:33
 */
public class AdminSyncServlet extends HttpServlet {

    public static final String PARAMETER_DEFAULT_API_KEY = "DEFAULT-API-KEY";

    public static final String [] FUEL_TYPES = {
            "1" /* petrol 95 */ ,
            "2" /* petrol 98 */,
            "3" /* diesel */,
            "4" /* heating */
    };

    Logger log = Logger.getLogger("cyprusfuelguide");

    private static String apiKeyCode = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean syncStations = request.getRequestURI().endsWith("/admin/sync-stations");

        log.info("Running sync cron task - sync prices");

        if(apiKeyCode == null) {
            final Parameter parameter = ParameterFactory.getParameterByName(PARAMETER_DEFAULT_API_KEY);
            if(parameter != null) {
                apiKeyCode = parameter.getParameterValue();
            } else {
                log.info("Initializing parameter: " + PARAMETER_DEFAULT_API_KEY);
                final UserService userService = UserServiceFactory.getUserService();
                final User user = userService.getCurrentUser();
                final String userEmail = user == null ? "Unknown" : user.getEmail();
                final Key key = ApiKeyFactory.addApiKey(userEmail, PARAMETER_DEFAULT_API_KEY);
                String uuid = KeyFactory.keyToString(key);
                final ApiKey apiKey = ApiKeyFactory.getApiKey(uuid);
                if(apiKey != null) {
                    apiKeyCode = apiKey.getApiKeyCode();
                    ParameterFactory.addParameter(PARAMETER_DEFAULT_API_KEY, apiKeyCode);
                } else {
                    log.severe("Could not get ApiKey with UUID: " + uuid);
                }
            }
        }

        final Queue queue = QueueFactory.getDefaultQueue();

        log.info("Scheduling sync events ...");
        long delay = 0L;
        for(final String fuelType : FUEL_TYPES) {
            TaskOptions taskOptions = TaskOptions.Builder
                    .withUrl("/sync/request")
                    .param("apiKeyCode", apiKeyCode)
                    .param("fuelType", fuelType)
                    .param("syncStations", Boolean.toString(syncStations))
                    .countdownMillis(delay)
                    .method(TaskOptions.Method.GET);
            queue.add(taskOptions);
            delay += 60000; // split individual request tasks by 60 seconds
        }
    }
}
