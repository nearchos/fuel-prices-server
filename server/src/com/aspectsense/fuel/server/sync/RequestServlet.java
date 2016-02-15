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

package com.aspectsense.fuel.server.sync;

import com.aspectsense.fuel.server.data.Parameter;
import com.aspectsense.fuel.server.datastore.ParameterFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         18/12/2015
 *         11:28
 */
public class RequestServlet extends HttpServlet {

    public static final String PARAMETER_NAME_USER_ID = "USER_ID";
    public static final String PARAMETER_NAME_USER_PASSWORD_HASHED = "USER_PASSWORD_HASHED";
    public static final String PARAMETER_NAME_PRODUCTION_URL_QUERY = "PRODUCTION_URL_QUERY";

    public static final String KEY_FUEL_TYPE = "fuelType";
    public static final String DEFAULT_FUEL_TYPE = "1"; // by default, assume '1', i.e. petrol 95

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    private static String postRequestPayload = null;

    private static String userId = null;
    private static String userPasswordHashed = null;
    private static String productionUrlQuery = null;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String magic = request.getParameter("magic");
        if(magic == null || magic.isEmpty() || !ParameterFactory.isMagic(magic)) {
            log.severe("Empty or invalid magic: " + magic);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid magic: " + magic + "\" }"); // normal JSON output
            return; // terminate here
        }

        final boolean debug = request.getParameter("debug") != null;

        final boolean syncStations = "true".equals(request.getParameter("syncStations"));

        // if no fuel type is requested, the default one is used
        final String fuelType = request.getParameter(KEY_FUEL_TYPE) == null ?
                DEFAULT_FUEL_TYPE :
                request.getParameter(KEY_FUEL_TYPE);

        if(userId == null) { // initialize parameter
            userId = getParameter(PARAMETER_NAME_USER_ID);
        }

        if(userPasswordHashed == null) { // initialize parameter
            userPasswordHashed = getParameter(PARAMETER_NAME_USER_PASSWORD_HASHED);
        }

        if(productionUrlQuery == null) { // initialize parameter
            productionUrlQuery = getParameter(PARAMETER_NAME_PRODUCTION_URL_QUERY);
        }

        try {
            final String correlationID = doRequest(productionUrlQuery, userId, userPasswordHashed, fuelType);
            final Queue queue = QueueFactory.getDefaultQueue();
            TaskOptions taskOptions = TaskOptions.Builder
                    .withUrl("/sync/poll")
                    .param("magic", magic)
                    .param("correlationId", correlationID)
                    .param("fuelType", fuelType)
                    .param("syncStations", Boolean.toString(syncStations))
                    .countdownMillis(60000) // wait 60 seconds before the poll
                    .method(TaskOptions.Method.GET);
            queue.add(taskOptions);
            printWriter.println("{ \"result\": \"ok\", " +
                    "\"debug\": " + debug + ", " +
                    "\"request-xml\": \"" + postRequestPayload + "\", " +
                    "\"correlation-id\": \"" + correlationID + "\" }"); // normal JSON output
        } catch (IOException ioe) {
            printWriter.println("{ \"result\": \"Error\", \"message\": \"" + ioe.getMessage() + "\" }"); // normal JSON output
        }
    }

    private String doRequest(final String urlRequest, final String userId, final String userPasswordHashed, final String fuelType) throws IOException {

        final URL productionUrlRequest = new URL(urlRequest);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) productionUrlRequest.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        httpURLConnection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        httpURLConnection.setRequestProperty("Authorization", "Hash " + userPasswordHashed);

        postRequestPayload = POST_REQUEST_PAYLOAD
                .replaceAll("<SenderID></SenderID>", "<SenderID>" + userId + "</SenderID>")
                .replaceAll("<Value></Value>", "<Value>" + userPasswordHashed + "</Value>")
                .replaceAll("<PetroleumType></PetroleumType>", "<PetroleumType>" + fuelType + "</PetroleumType>");

        final OutputStream os = httpURLConnection.getOutputStream();
        os.write(postRequestPayload.getBytes());

        int responseCode = httpURLConnection.getResponseCode();
        if(responseCode != 200) {
            log.severe("RequestServlet @ '" + urlRequest + "' produced response code: " + responseCode);
            log.severe("RequestServlet @ payload: " + postRequestPayload);
            throw new IOException("HTTP response code: " + responseCode);
        }

        final InputStream stdInputStream = httpURLConnection.getInputStream();
        final String receivedMessage = Util.convertStreamToString(stdInputStream);
        return receivedMessage.substring(receivedMessage.indexOf("<CorrelationID>") + "<CorrelationID>".length(), receivedMessage.indexOf("</CorrelationID>"));
    }

    public static final String POST_REQUEST_PAYLOAD =
            "<GovTalkMessage xmlns=\"http://www.govtalk.gov.uk/CM/envelope\">\n" +
            "  <EnvelopeVersion>2.0</EnvelopeVersion>\n" +
            "  <Header>\n" +
            "    <MessageDetails>\n" +
            "      <Class>PBL_MCIT_Petrol_PricesMob</Class>\n" +
            "      <Qualifier>request</Qualifier>\n" +
            "      <Function>submit</Function>\n" +
            "      <CorrelationID></CorrelationID>\n" +
            "    </MessageDetails>\n" +
            "    <SenderDetails>\n" +
            "      <IDAuthentication>\n" +
            "        <SenderID></SenderID>\n" + // must be set
            "        <Authentication>\n" +
            "          <Method>hash</Method>\n" +
            "          <Value></Value>\n" + // must be set
            "        </Authentication>\n" +
            "      </IDAuthentication>\n" +
            "    </SenderDetails>\n" +
            "  </Header>\n" +
            "  <Body>\n" +
            "    <Message xmlns=\"http://gateway.gov/schema/common/v1\">\n" +
            "      <Header>\n" +
            "        <Vendor>Ariadni Team</Vendor>\n" +
            "      </Header>\n" +
            "      <Body>\n" +
            "        <!-- Transaction Body XML Start -->\n" +
            "        <PetroleumPriceRequestMob xmlns=\"http://gateway.gov/schema/mcit/v1\" xmlns:com=\"http://gateway.gov/schema/common/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "          <PetroleumType></PetroleumType>\n" + // must be set
            "        </PetroleumPriceRequestMob>\n" +
            "        <!-- Transaction Body XML End -->\n" +
            "      </Body>\n" +
            "    </Message>\n" +
            "  </Body>\n" +
            "</GovTalkMessage>\n";

    private String getParameter(final String parameterKey) {
        final Parameter parameter = ParameterFactory.getParameterByName(parameterKey);
        if(parameter != null) {
            return parameter.getParameterValue();
        } else {
            log.severe("Could no find parameter with key: " + parameterKey);
            throw new RuntimeException("Could no find parameter with key: " + parameterKey);
        }
    }
}