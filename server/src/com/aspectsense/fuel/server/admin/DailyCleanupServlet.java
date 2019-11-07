package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.Parameter;
import com.aspectsense.fuel.server.datastore.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DailyCleanupServlet extends HttpServlet {

    public static final int DEFAULT_MAX_NUM_OF_ENTITIES_TO_BE_DELETED = 100;
    public static final long PROTECTED_PERIOD_IN_MILLISECONDS = 90L * 24 * 60 * 60 * 1000; // 90 days

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final PrintWriter printWriter = response.getWriter();

        // check if daily summaries are turned on
        final Parameter parameterMaxNumOfEntitiesToBeDeleted = ParameterFactory.getParameterByName("MAX_NUM_OF_ENTITIES_TO_BE_DELETED");
        int maxNumOfEntitiesToBeDeleted = DEFAULT_MAX_NUM_OF_ENTITIES_TO_BE_DELETED;
        if(parameterMaxNumOfEntitiesToBeDeleted != null) {
            maxNumOfEntitiesToBeDeleted = parameterMaxNumOfEntitiesToBeDeleted.getValueAsInteger();
        } else {
            printWriter.println("Daily summary reports are not set (add parameter 'DAILY_SUMMARY_REPORT' and set it to 'true'");
        }

        final long now = System.currentTimeMillis();
        final long notNewerThan = now - PROTECTED_PERIOD_IN_MILLISECONDS; // now - 90 days

        final int numOfPricesDeleted = PricesFactory.deletePrices(notNewerThan, maxNumOfEntitiesToBeDeleted);
        final int numOfStationsDeleted = StationsFactory.deleteStations(notNewerThan, maxNumOfEntitiesToBeDeleted);
        final int numOfOfflinesDeleted = OfflinesFactory.deleteOfflines(notNewerThan, maxNumOfEntitiesToBeDeleted);
        final int numOfSyncMessagesDeleted =  SyncMessageFactory.deleteSyncMessages(now - 4 * PROTECTED_PERIOD_IN_MILLISECONDS, maxNumOfEntitiesToBeDeleted); // now - 360 days

        printWriter.println(
                "{ \"numOfPricesDeleted\": " + numOfPricesDeleted + ",\n" +
                " \"numOfStationsDeleted\": " + numOfStationsDeleted + ",\n" +
                " \"numOfOfflinesDeleted\": " + numOfOfflinesDeleted + ",\n" +
                " \"numOfSyncMessagesDeleted\": " + numOfSyncMessagesDeleted + "}");
    }
}