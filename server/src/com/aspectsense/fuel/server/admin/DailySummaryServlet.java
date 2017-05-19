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
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static com.aspectsense.fuel.server.json.Util.SIMPLE_DATE_FORMAT;
import static com.aspectsense.fuel.server.util.Util.convertStreamToString;

/**
 * @author Nearchos Paspallis
 * 18-Mar-17
 */
public class DailySummaryServlet extends HttpServlet {

    private static final String CRUDE_OIL_PRICE_SERVICE_URL = "http://finance.yahoo.com/d/quotes.csv?s=CL=F&f=a";
    private static final String EXCHANGE_RATE_SERVICE_URL = "http://api.fixer.io/latest?base=EUR&symbols=USD,GBP";

    public static final Logger log = Logger.getLogger(DailySummaryServlet.class.getCanonicalName());

    public static final long MILLISECONDS_IN_A_DAY = 24L * 60 * 60 * 1000L;
    public static final long MILLISECONDS_IN_A_WEEK = 7L * MILLISECONDS_IN_A_DAY;

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

        // get latest crude oil price
        double crudeOilPrice = 0d; // default is zero (i.e. unknown)
        try {
            final String receivedPriceS = makeRequest(CRUDE_OIL_PRICE_SERVICE_URL);
            crudeOilPrice = Double.parseDouble(receivedPriceS.trim());
        } catch (final IOException | NumberFormatException e) {
            final String error = "SyncCrudeOilPrice error -> " + e.getMessage();
            log(error);
        }

        // get latest EURUSD and EURGBP rates
        double eurUsd = 0; // default is zero (i.e. unknown)
        double eurGbp = 0; // default is zero (i.e. unknown)
        try {
            final String exchangeRatesJson = makeRequest(EXCHANGE_RATE_SERVICE_URL);
            final JSONObject replyJsonObject = new JSONObject(exchangeRatesJson);
            eurUsd = replyJsonObject.getJSONObject("rates").getDouble("USD");
            eurGbp = replyJsonObject.getJSONObject("rates").getDouble("GBP");
        } catch (final IOException | JSONException e) {
            final String error = "SyncExchangeRates error -> " + e.getMessage();
            System.out.println(error); // todo log
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

        final StringBuilder json = new StringBuilder("{\n");
        json.append("  \"crudeOilInUsd\": ").append(String.format("%.2f", crudeOilPrice)).append(",\n");
        json.append("  \"eurUsd\": ").append(String.format("%.2f", eurUsd)).append(",\n");
        json.append("  \"eurGbp\": ").append(String.format("%.2f", eurGbp)).append(",\n");
        json.append("  \"stations\": {").append("\n");
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
            json.append("    \"").append(station).append("\": ").append(Arrays.toString(prices)).append(i < numOfStations ? ",\n" : "\n");
        }
        json.append("  }\n");
        json.append("}");

        DailySummaryFactory.addDailySummary(dateS, json.toString());

        printWriter.println("Done");
    }

    private static String makeRequest(final String url) throws IOException {
        final URL urlRequest = new URL(url);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) urlRequest.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("GET");

        int responseCode = httpURLConnection.getResponseCode();
        if(responseCode != 200) {
            final String error = "'" + urlRequest + "' produced response code: " + responseCode;
            throw new IOException(error);
        } else {
            final InputStream stdInputStream = httpURLConnection.getInputStream();
            return convertStreamToString(stdInputStream);
        }
    }
}