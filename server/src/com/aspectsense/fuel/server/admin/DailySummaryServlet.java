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
import com.aspectsense.fuel.server.data.Prices;
import com.aspectsense.fuel.server.data.Stations;
import com.aspectsense.fuel.server.datastore.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis
 * 18-Mar-17
 */
public class DailySummaryServlet extends HttpServlet {

    public static final Logger log = Logger.getLogger(DailySummaryServlet.class.getCanonicalName());

    public static final long MILLISECONDS_IN_A_DAY = 24L * 60 * 60 * 1000L;
    public static final long MILLISECONDS_IN_A_WEEK = 7L * MILLISECONDS_IN_A_DAY;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final String dateS = SIMPLE_DATE_FORMAT.format(new Date());

        final PrintWriter printWriter = response.getWriter();

        // check if daily summaries are turned on
        final Parameter parameterWeeklyReports = ParameterFactory.getParameterByName("DAILY_SUMMARY_REPORT");
        if(parameterWeeklyReports != null && !parameterWeeklyReports.getValueAsBoolean()) {
            printWriter.println("Daily summary reports are not set (add parameter 'DAILY_SUMMARY_REPORT' and set it to 'true'");
            return;
        }

        final Map<FuelType, Map<String,Prices.PriceInMillieurosAndTimestamp>> fuelTypeToStationCodeToPriceInMillieurosMap = new HashMap<>();
        for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            final Prices prices = PricesFactory.getLatestPrices(fuelType.getCodeAsString());
            assert prices != null;
            fuelTypeToStationCodeToPriceInMillieurosMap.put(fuelType, prices.getStationCodeToPriceInMillieurosAndTimestampMap());
        }

        final Set<String> stations = new HashSet<>();
        for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            stations.addAll(fuelTypeToStationCodeToPriceInMillieurosMap.get(fuelType).keySet());
        }

        final StringBuilder json = new StringBuilder("{");
        final int numOfStations = stations.size();
        int i = 0;
        for(final String station : stations) {
            i++;
            int [] prices = new int[FuelType.ALL_FUEL_TYPES.length];
            int j = 0;
            for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
                Prices.PriceInMillieurosAndTimestamp priceInMillieurosAndTimestamp =  fuelTypeToStationCodeToPriceInMillieurosMap.get(fuelType).get(station);
                prices[j] = priceInMillieurosAndTimestamp == null ? 0 : priceInMillieurosAndTimestamp.getPriceInMillieuros();
                j++;
            }
            json.append(" \"").append(station).append("\": ").append(Arrays.toString(prices)).append(i < numOfStations ? ",\n" : "\n");
        }
        json.append("}");

        DailySummaryFactory.addDailySummary(dateS, json.toString());

        printWriter.println("Done");
    }
}