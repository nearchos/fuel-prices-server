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

import com.aspectsense.fuel.server.data.FuelType;
import com.aspectsense.fuel.server.data.Parameter;
import com.aspectsense.fuel.server.datastore.ParameterFactory;
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

    public static final String PARAMETER_MAGIC = "MAGIC";
    public static final String PARAMETER_SYNC_ON_CRON = "SYNC_ON_CRON";

    private Logger log = Logger.getLogger("cyprusfuelguide");

    private static String magic = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("Running sync cron task - sync stations or sync prices");

        boolean syncStations = request.getRequestURI().endsWith("/admin/sync-stations");

        if(magic == null) {
            final Parameter parameter = ParameterFactory.getParameterByName(PARAMETER_MAGIC);
            if(parameter != null) {
                magic = parameter.getParameterValue();
            } else {
                final UserService userService = UserServiceFactory.getUserService();
                final User user = userService.getCurrentUser();
                final String userEmail = user == null ? "Unknown" : user.getEmail();
                log.severe("Could not get magic for userEmail: " + userEmail);
            }
        }

        boolean forceSync = request.getParameter("forceSync") != null;

        final Parameter syncOnCronParameter = ParameterFactory.getParameterByName(PARAMETER_SYNC_ON_CRON);
        final boolean syncOnCron = syncOnCronParameter != null && "true".equalsIgnoreCase(syncOnCronParameter.getParameterValue());

        if(forceSync || syncOnCron) { // only proceed if either: 1. the 'forceSync' parameter was set via the URL, or 2. the syncOnCron parameter is set to true
            log.info("Scheduling sync events ...");

            final Queue queue = QueueFactory.getDefaultQueue();

            long delay = 0L;
            boolean oneTimeFire = syncStations; // this is used so that the syncStations is executed only the first time (if at any)

            // schedule the request/poll servlets for each fuel type
            for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
                TaskOptions taskOptions = TaskOptions.Builder
                        .withUrl("/sync/request")
                        .param("magic", magic)
                        .param("fuelType", fuelType.getCodeAsString())
                        .param("syncStations", Boolean.toString(oneTimeFire))
                        .countdownMillis(delay)
                        .method(TaskOptions.Method.GET);
                queue.add(taskOptions);
                delay += 60000; // put 60 seconds between individual request tasks
                oneTimeFire = false;
            }

            delay += 60000; // put an additional 60 seconds before the update call (as the last poll will be executed at +90, the update is scheduled for +120)
            // finally, schedule the datastore update servlet -- this will normally be scheduled 30 seconds after the last poll
            TaskOptions taskOptions = TaskOptions.Builder
                    .withUrl("/sync/updateDatastore")
                    .param("magic", magic)
                    .countdownMillis(delay)
                    .method(TaskOptions.Method.GET);
            queue.add(taskOptions);
        } else {
            log.info("SKIPPING: Scheduling sync events (neither parameter 'forceSync' in URL or 'SYNC_ON_CRON' were true)");
        }
    }
}