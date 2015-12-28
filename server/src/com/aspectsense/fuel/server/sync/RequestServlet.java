package com.aspectsense.fuel.server.sync;

import com.aspectsense.fuel.server.data.ApiKey;
import com.aspectsense.fuel.server.data.Parameter;
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
import java.util.Vector;
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
    public static final String DEFAULT_FUEL_TYPE = "1"; // by default, assume petrol 95 // todo

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    private static String postRequestPayload = null;

    private static String userId = null;
    private static String userPasswordHashed = null;
    private static String productionUrlQuery = null;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain; charset=utf-8");
        final PrintWriter printWriter = response.getWriter();

        final String apiKey = request.getParameter("apiKey");
        if(apiKey == null || apiKey.isEmpty() || !ApiKey.isActive(apiKey)) {
            log.severe("Empty or invalid apiKey: " + apiKey);
            printWriter.println("{ \"result\": \"error\", \"message\": \"Empty or invalid apiKey: " + apiKey + "\" }"); // normal JSON output
            return; // terminate here
        }

        // if no fuel type is requested, the default one is used
        final String fuelType = request.getParameter(KEY_FUEL_TYPE) == null ?
                DEFAULT_FUEL_TYPE :
                request.getParameter(KEY_FUEL_TYPE);

        if(userId == null) {
            final Parameter parameter = Parameter.getParameterByName(PARAMETER_NAME_USER_ID);
            if(parameter != null) {
                userId = parameter.getParameterValue();
            } else {
                log.severe("Could no find parameter: " + PARAMETER_NAME_USER_ID);
                printWriter.println("{ \"result\": \"Error\", \"message\": \"could not find parameter: " + PARAMETER_NAME_USER_ID + "\" }"); // normal JSON output
                return; // terminate here
            }
        }

        if(userPasswordHashed == null) {
            final Parameter parameter = Parameter.getParameterByName(PARAMETER_NAME_USER_PASSWORD_HASHED);
            if(parameter != null) {
                userPasswordHashed = parameter.getParameterValue();
            } else {
                log.severe("Could no find parameter: " + PARAMETER_NAME_USER_PASSWORD_HASHED);
                printWriter.println("{ \"result\": \"Error\", \"message\": \"could not find parameter: " + PARAMETER_NAME_USER_PASSWORD_HASHED + "\" }"); // normal JSON output
                return; // terminate here
            }
        }

        if(productionUrlQuery == null) {
            final Parameter parameter = Parameter.getParameterByName(PARAMETER_NAME_PRODUCTION_URL_QUERY);
            if(parameter != null) {
                productionUrlQuery = parameter.getParameterValue();
            } else {
                log.severe("Could no find parameter: " + PARAMETER_NAME_PRODUCTION_URL_QUERY);
                printWriter.println("{ \"result\": \"Error\", \"message\": \"could not find parameter: " + PARAMETER_NAME_PRODUCTION_URL_QUERY + "\" }"); // normal JSON output
                return; // terminate here
            }
        }

        try {
            final String correlationID = doRequest(productionUrlQuery, userId, userPasswordHashed, fuelType);
            final Queue queue = QueueFactory.getDefaultQueue();
            TaskOptions taskOptions = TaskOptions.Builder.withUrl("/sync/poll").param("apiKey", apiKey).param("correlationId", correlationID).countdownMillis(30000).method(TaskOptions.Method.GET);
            queue.add(taskOptions);
            printWriter.println("{ \"result\": \"ok\", \"correlation-id\": \"" + correlationID + "\" }"); // normal JSON output
        } catch (IOException ioe) {
            printWriter.println("{ \"result\": \"Error\", \"message\": \"" + ioe.getMessage() + "\" }"); // normal JSON output
        }
//        todo add checks to notify admin if sync fails for too long
    }

    private String doRequest(final String urlRequest, final String userId, final String userPasswordHashed, final String fuelType)
        throws IOException {
        final URL productionUrlRequest = new URL(urlRequest);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) productionUrlRequest.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        httpURLConnection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        httpURLConnection.setRequestProperty("Authorization", "Hash " + userPasswordHashed);

        if(postRequestPayload == null) {
            postRequestPayload = POST_REQUEST_PAYLOAD
                    .replaceAll("<SenderID></SenderID>", "<SenderID>" + userId + "</SenderID>")
                    .replaceAll("<Value></Value>", "<Value>" + userPasswordHashed + "</Value>")
                    .replaceAll("<PetroleumType></PetroleumType>", "<PetroleumType>" + fuelType + "</PetroleumType>");
        }

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
            "        <SenderID>paspfuel</SenderID>\n" +
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