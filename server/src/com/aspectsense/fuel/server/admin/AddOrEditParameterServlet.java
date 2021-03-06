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

import com.aspectsense.fuel.server.data.Parameter;
import com.aspectsense.fuel.server.datastore.ParameterFactory;

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
 *         23:20
 */
public class AddOrEditParameterServlet extends HttpServlet {
    Logger log = Logger.getLogger("cyprusfuelguide");
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String name = request.getParameter(ParameterFactory.PROPERTY_NAME);
        final String value = request.getParameter(ParameterFactory.PROPERTY_VALUE);
        final boolean edit = request.getParameterMap().containsKey("edit");
        log.info("Adding or editing parameter with name (" + name + "), value (" + value + ") and edit flag (" + edit + ")");
        if(name == null || name.isEmpty() || value == null || value.isEmpty()) {
            log.warning("Both name (" + name + ") and value (" + value + ") must be non-null");
        } else {
            if(edit) {
                final Parameter parameter = ParameterFactory.getParameterByName(name);
                if(parameter == null) {
                    log.warning("Could not edit as parameter with name (" + name + ") was not found");
                } else if(value.equals(parameter.getParameterValue())) {
                    log.warning("Could not edit as parameter with name (" + name + ") already has identical value (" + value + ")");
                } else {
                    ParameterFactory.editParameter(parameter.getUuid(), name, value);
                }
            } else { // add
                ParameterFactory.addParameter(name, value);
            }
        }
        response.sendRedirect("/admin/parameters");
    }
}
