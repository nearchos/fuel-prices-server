package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.Parameter;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String name = request.getParameter(Parameter.PROPERTY_NAME);
        final String value = request.getParameter(Parameter.PROPERTY_VALUE);
        if(name == null || name.isEmpty() || value == null || value.isEmpty()) {
            log.warning("Both name (" + name + ") and value (" + value + ") must be non-null");
        } else {
            Parameter.addParameter(name, value);
        }
        response.sendRedirect("/admin/parameters");
    }
}
