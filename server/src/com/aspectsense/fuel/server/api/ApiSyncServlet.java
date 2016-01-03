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

package com.aspectsense.fuel.server.api;

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
 *         21:21
 */
public class ApiSyncServlet extends HttpServlet {

    private final Logger log = Logger.getLogger("cyprusfuelguide");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String from = request.getParameter("from");
        long fromTimestamp = 0L;
        try {
            if(from != null) fromTimestamp = Long.parseLong(from);
        } catch (NumberFormatException nfe) {
            log.info("Could not parse parameter 'from': " + from);
        }
        //todo
        response.getWriter().println(" { \"status\": \"ok\", \"from\": " + fromTimestamp + " }");
    }
}
