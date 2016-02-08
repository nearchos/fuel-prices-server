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

import com.aspectsense.fuel.server.data.Parameter;
import com.aspectsense.fuel.server.datastore.*;

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
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         28/12/2015
 *         21:55
 */
public class PollServlet extends HttpServlet {

    public static final String PARAMETER_NAME_USER_ID = "USER_ID";
    public static final String PARAMETER_NAME_USER_PASSWORD_HASHED = "USER_PASSWORD_HASHED";
    public static final String PARAMETER_NAME_PRODUCTION_URL_POLL = "PRODUCTION_URL_POLL";

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    private static String userId = null;
    private static String userPasswordHashed = null;
    private static String productionUrlPoll = null;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final long start = System.currentTimeMillis();
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final boolean syncStations = "true".equals(request.getParameter("syncStations"));

        final boolean debug = request.getParameter("debug") != null;

        final String magic = request.getParameter("magic");
        if(magic == null || magic.isEmpty() || !ParameterFactory.isMagic(magic)) {
            log.severe("Empty or invalid magic: " + magic);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid magic: " + magic + "\" }"); // normal JSON output
            return; // terminate here
        }

        final String fuelType = request.getParameter("fuelType");
        if(fuelType == null || fuelType.isEmpty()) {
            log.severe("Empty or invalid fuelType: " + fuelType);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid fuelType: " + fuelType + "\" }"); // normal JSON output
            return; // terminate here
        }

        final String correlationId = request.getParameter("correlationId");
        if(correlationId == null || correlationId.isEmpty()) {
            log.severe("Empty or invalid correlationId: " + correlationId);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid correlationId: " + correlationId + "\" }"); // normal JSON output
            return; // terminate here
        }

        if(userId == null) {
            userId = getParameter(PARAMETER_NAME_USER_ID);
        }

        if(userPasswordHashed == null) {
            userPasswordHashed = getParameter(PARAMETER_NAME_USER_PASSWORD_HASHED);
        }

        if(productionUrlPoll == null) {
            productionUrlPoll = getParameter(PARAMETER_NAME_PRODUCTION_URL_POLL);
        }

        try {
            final String xml = doPoll(correlationId, productionUrlPoll, userId, userPasswordHashed);
            if(debug) {
                log.info("xml: \n" +xml);
            }

            // handle xml
            final Vector<PetroleumPriceDetail> petroleumPriceDetails = Util.parseXmlPollResponse(xml, fuelType);
//            final int numOfChanges = Util.updateDatastore(petroleumPriceDetails, fuelType, syncStations);
            final int numOfChanges = updateDatastore(petroleumPriceDetails, fuelType, syncStations);
            log.info("Util.updateDatastore(petroleumPriceDetails, fuelType) -> " + numOfChanges + ", " +
                    "elapsed: " + (System.currentTimeMillis() - start));

            printWriter.println("{ \"result\": \"ok\", " +
                    "\"numOfChanges\": " + numOfChanges + ", " +
                    "\"fuelType\": \"" + fuelType + "\", " +
                    "\"debug\":" + debug + ", " +
                    (debug ? "\"xml\": \"" + xml + "\", " : "") +
                    "\"elapsed\": " + (System.currentTimeMillis() - start) + " }");

        } catch (IOException ioe) {
            printWriter.println("{ \"result\": \"Error\", \"message\": \"" + ioe.getMessage() + "\" }");
        }
    }

    /**
     * @param correlationId used to determine which data to poll
     * @return <code>null</code> when the result is not ready yet
     */
    private static String doPoll(final String correlationId, final String urlPoll, final String userId, final String userPasswordHashed)
            throws IOException {

        String reply;

        // this is the XML-based request message to be submitted via POST - loaded from file
        String postPollPayload = POST_POLL_PAYLOAD
                .replaceAll("<SenderID></SenderID>", "<SenderID>" + userId + "</SenderID>")
                .replaceAll("<Value></Value>", "<Value>" + userPasswordHashed + "</Value>")
                .replace("<CorrelationID></CorrelationID>", "<CorrelationID>" + correlationId + "</CorrelationID>");

        final URL u = new URL(urlPoll);

        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");

        final OutputStream os = conn.getOutputStream();
        os.write(postPollPayload.getBytes());

//        int responseCode = conn.getResponseCode();

        final InputStream stdInputStream = conn.getInputStream();
        reply = Util.convertStreamToString(stdInputStream);
        return reply;
    }

    public static final String POST_POLL_PAYLOAD =
            "<GovTalkMessage xmlns=\"http://www.govtalk.gov.uk/CM/envelope\">\n" +
            "  <EnvelopeVersion>2.0</EnvelopeVersion>\n" +
            "  <Header>\n" +
            "    <MessageDetails>\n" +
            "      <Class>PBL_MCIT_Petrol_PricesMob</Class>\n" +
            "      <Qualifier>poll</Qualifier>\n" +
            "      <Function>submit</Function>\n" +
            "      <CorrelationID></CorrelationID>\n" +
            "    </MessageDetails>\n" +
            "    <SenderDetails>\n" +
            "      <IDAuthentication>\n" +
            "        <SenderID></SenderID>\n" +
            "        <Authentication>\n" +
            "          <Method>hash</Method>\n" +
            "          <Value></Value>\n" +
            "        </Authentication>\n" +
            "      </IDAuthentication>\n" +
            "    </SenderDetails>\n" +
            "  </Header>\n" +
            "  <GovTalkDetails>\n" +
            "    <Keys>\n" +
            "      <Key Type=\"\" />\n" +
            "    </Keys>\n" +
            "  </GovTalkDetails>\n" +
            "  <Body />\n" +
            "</GovTalkMessage>";

    private String getParameter(final String parameterKey) {
        final Parameter parameter = ParameterFactory.getParameterByName(parameterKey);
        if(parameter != null) {
            return parameter.getParameterValue();
        } else {
            log.severe("Could no find parameter with key: " + parameterKey);
            throw new RuntimeException("Could no find parameter with key: " + parameterKey);
        }
    }

    private int updateDatastore(final Vector<PetroleumPriceDetail> petroleumPriceDetails, final String fuelType, final boolean syncStations) {

        int numOfChanges = 0;

        // update the data for offline stations
        final long updateTimestamp = System.currentTimeMillis();

        // sync stations, if needed (as indicated by syncStations boolean value)
        if(syncStations) {
            StationsFactory.addStations(petroleumPriceDetails, updateTimestamp);
        }

        // sync prices
//        final Prices prices =
        PricesFactory.addPrices(petroleumPriceDetails, fuelType, updateTimestamp);

        // sync offliens
        OfflinesFactory.addOfflines(petroleumPriceDetails, updateTimestamp);

        return numOfChanges;
    }
}