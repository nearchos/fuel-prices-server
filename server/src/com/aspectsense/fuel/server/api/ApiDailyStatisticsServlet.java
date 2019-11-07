package com.aspectsense.fuel.server.api;

import com.aspectsense.fuel.server.data.DailyStatistics;
import com.aspectsense.fuel.server.datastore.DailyStatisticsFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class ApiDailyStatisticsServlet extends HttpServlet {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final DailyStatistics dailyStatistics = DailyStatisticsFactory.getLatestDailyStatistics();
        final String json = dailyStatistics.getJson();

        response.getWriter().println(json);
    }
}