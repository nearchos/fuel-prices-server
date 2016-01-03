package com.aspectsense.fuel.server.sync;

import com.aspectsense.fuel.server.data.Parameter;
import com.aspectsense.fuel.server.datastore.ApiKeyFactory;
import com.aspectsense.fuel.server.datastore.ParameterFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

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

    public static final String MEMCACHE_KEY_LAST_HASHCODE = "last-hashcode";

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

        final String apiKeyCode = request.getParameter("apiKeyCode");
        if(apiKeyCode == null || apiKeyCode.isEmpty() || !ApiKeyFactory.isActive(apiKeyCode)) {
            log.severe("Empty or invalid apiKeyCode: " + apiKeyCode);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid apiKeyCode: " + apiKeyCode + "\" }"); // normal JSON output
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
            final Parameter parameter = ParameterFactory.getParameterByName(PARAMETER_NAME_USER_ID);
            if(parameter != null) {
                userId = parameter.getParameterValue();
            } else {
                log.severe("Could no find parameter: " + PARAMETER_NAME_USER_ID);
                printWriter.println("{ \"result\": \"Error\", \"message\": \"could not find parameter: " + PARAMETER_NAME_USER_ID + "\" }"); // normal JSON output
                return; // terminate here
            }
        }

        if(userPasswordHashed == null) {
            final Parameter parameter = ParameterFactory.getParameterByName(PARAMETER_NAME_USER_PASSWORD_HASHED);
            if(parameter != null) {
                userPasswordHashed = parameter.getParameterValue();
            } else {
                log.severe("Could no find parameter: " + PARAMETER_NAME_USER_PASSWORD_HASHED);
                printWriter.println("{ \"result\": \"Error\", \"message\": \"could not find parameter: " + PARAMETER_NAME_USER_PASSWORD_HASHED + "\" }"); // normal JSON output
                return; // terminate here
            }
        }

        if(productionUrlPoll == null) {
            final Parameter parameter = ParameterFactory.getParameterByName(PARAMETER_NAME_PRODUCTION_URL_POLL);
            if(parameter != null) {
                productionUrlPoll = parameter.getParameterValue();
            } else {
                log.severe("Could no find parameter: " + PARAMETER_NAME_PRODUCTION_URL_POLL);
                printWriter.println("{ \"result\": \"error\", \"message\": \"could not find parameter: " + PARAMETER_NAME_PRODUCTION_URL_POLL + "\" }"); // normal JSON output
                return; // terminate here
            }
        }

        try {
            final String xml = doPoll(correlationId, productionUrlPoll, userId, userPasswordHashed);
            if(debug) {
                log.info("xml: \n" +xml);
            }

            int lastHashcode = 0;
            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
            if(memcacheService.contains(MEMCACHE_KEY_LAST_HASHCODE + "-" + fuelType)) {
                lastHashcode = (Integer) memcacheService.get(MEMCACHE_KEY_LAST_HASHCODE + "-" + fuelType);
            }

            int hashcode = xml.hashCode();
            final boolean xmlHasChanged = hashcode != lastHashcode;
            memcacheService.put(MEMCACHE_KEY_LAST_HASHCODE + "-" + fuelType, hashcode);

            if(xmlHasChanged) {
                // handle xml
                Vector<PetroleumPriceDetail> petroleumPriceDetails = Util.parseXmlPollResponse(xml, fuelType);
                int numOfChanges = Util.updateDatastore(petroleumPriceDetails, fuelType, syncStations);
                log.info("Util.updateDatastore(petroleumPriceDetails, fuelType) -> " + numOfChanges + ", " +
                        "elapsed: " + (System.currentTimeMillis() - start));
                printWriter.println("{ \"result\": \"ok\", " +
                        "\"numOfChanges\": " + numOfChanges + ", " +
                        "\"hashXmlChanged\": true, " +
                        "\"fuelType\": \"" + fuelType + "\", " +
                        "\"elapsed\": " + (System.currentTimeMillis() - start) + " }");
            }

            printWriter.println("{ \"result\": \"ok\", " +
                    "\"numOfChanges\": 0, " +
                    "\"hashXmlChanged\": false, " +
                    "\"fuelType\": \"" + fuelType + "\", " +
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
}