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

package com.aspectsense.fuel.server.sync;

import com.aspectsense.fuel.server.admin.AdminSyncServlet;
import com.aspectsense.fuel.server.data.Prices;
import com.aspectsense.fuel.server.datastore.ApiKeyFactory;
import com.aspectsense.fuel.server.datastore.PriceFactory;
import com.aspectsense.fuel.server.datastore.PricesFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         10/01/2016
 *         10:02
 */
public class UpdateDatastoreServlet extends HttpServlet {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String apiKeyCode = request.getParameter("apiKeyCode");
        if(apiKeyCode == null || apiKeyCode.isEmpty() || !ApiKeyFactory.isActive(apiKeyCode)) {
            log.severe("Empty or invalid apiKeyCode: " + apiKeyCode);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid apiKeyCode: " + apiKeyCode + "\" }"); // normal JSON output
            return; // terminate here
        }

        // we use a single timestamp for the full update operation of all the prices
        final long updateTimestamp = System.currentTimeMillis();

        for(final String fuelType : AdminSyncServlet.FUEL_TYPES) {
            // get the latest JSON for each fuelType
            final Prices prices = PricesFactory.getLatestPrices(fuelType);
            final Map<String,Integer> stationCodeToPriceInMillieurosMap = prices == null ? new HashMap<String, Integer>(): prices.getStationCodeToPriceInMillieurosMap();

            // update the Price datastore and facilitate the update functionality
            for(final String stationCode : stationCodeToPriceInMillieurosMap.keySet()) {
                final int priceInMillieuro = stationCodeToPriceInMillieurosMap.get(stationCode);
                PriceFactory.addOrUpdatePrice(stationCode, fuelType, priceInMillieuro, updateTimestamp);
            }
        }
    }
}
