<%--
  ~ This file is part of the Cyprus Fuel Guide server.
  ~
  ~ The Cyprus Fuel Guide server is free software: you can redistribute it
  ~ and/or modify it under the terms of the GNU General Public License as
  ~ published by the Free Software Foundation, either version 3 of
  ~ the License, or (at your option) any later version.
  ~
  ~ The Cyprus Fuel Guide server is distributed in the hope that it will be
  ~ useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
  ~ Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with Cyprus Fuel Guide. If not, see <http://www.gnu.org/licenses/>.
  --%>

<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.aspectsense.fuel.server.api.ApiSyncServlet" %>
<%@ page import="com.aspectsense.fuel.server.datastore.SyncMessageFactory" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.fuel.server.data.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.aspectsense.fuel.server.model.Station" %>
<%@ page import="com.aspectsense.fuel.server.model.Offline" %>
<%@ page import="com.aspectsense.fuel.server.model.Price" %>
<%@ page import="com.aspectsense.fuel.server.model.SyncMessage" %>
<%@ page import="com.google.gson.Gson" %>

<%--
  User: Nearchos Paspallis
  Date: 18/12/15
  Time: 11:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Cyprus Fuel Guide - Difference</title>
</head>

<body>

<%@ include file="Authenticate.jsp" %>

<%
    final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if(userEntity == null)
    {
%>
You are not logged in!
<%
    }
    else if(!userEntity.isAdmin())
    {
%>
<p>You are not admin!</p>
<%
    }
    else
    {
        final long start = System.currentTimeMillis();

        final String fromTimestampS = request.getParameter("from");
        final String toTimestampS = request.getParameter("to");
        if(fromTimestampS == null || fromTimestampS.isEmpty() || toTimestampS == null || toTimestampS.isEmpty()) {
%>
<p>You must define a from and to parameters in the URL (e.g. /admin/difference?from=...&to=...)</p>
<%
        } else {
            final long fromTimestamp = Long.parseLong(fromTimestampS);
            final long toTimestamp = Long.parseLong(toTimestampS);
%>

<h1>Difference</h1>

<p>From: <%=fromTimestampS%> (<%=timestampFormat.format(new Date(fromTimestamp))%>)</p>
<p>To: <%=toTimestampS%> (<%=timestampFormat.format(new Date(toTimestamp))%>)</p>
<hr/>
<%
            final com.aspectsense.fuel.server.data.SyncMessage fromSyncMessage = SyncMessageFactory.querySyncMessage(fromTimestamp);
            final com.aspectsense.fuel.server.data.SyncMessage toSyncMessage = SyncMessageFactory.querySyncMessage(toTimestamp);
            try {
                final ApiSyncServlet.Modifications modifications = ApiSyncServlet.computeModifications(fromSyncMessage, toSyncMessage);
                final String reply = ApiSyncServlet.formReplyMessage(fromTimestamp, modifications, System.currentTimeMillis() - start, toSyncMessage.getLastUpdated());
%>
<p><%=reply%></p>
<hr/>
    <table>
        <tr>
            <th>Original</th>
            <th>Updated</th>
        </tr>
<%
                final String sourceJson = fromSyncMessage.getJson();
                final Gson gson = new Gson();
                final SyncMessage sourceSyncMessage = gson.fromJson(sourceJson, SyncMessage.class);
//                final JSONObject sourceJsonObject = new JSONObject(sourceJson);
//                final Map<String,Station> sourceStations = StationsParser.jsonArrayToMap(sourceJsonObject.getJSONArray("stations"));
                final Map<String,Station> sourceStations = sourceSyncMessage.getCodeToStationsMap();
//                final Map<String,Boolean> sourceOfflines = OfflinesParser.fromOfflinesJsonArray(sourceJsonObject.getJSONArray("offlines"));
                final Map<String,Boolean> sourceOfflines = sourceSyncMessage.getOfflinesMap();
//                final Map<String, Price> sourcePrices = PriceParser.jsonArrayToMap(sourceJsonObject.getJSONArray("prices"));
                final Map<String, Price> sourcePrices = sourceSyncMessage.getPricesMap();

                final String targetJson = toSyncMessage.getJson();
//                final JSONObject targetJsonObject = new JSONObject(targetJson);
                final SyncMessage targetSyncMessage = gson.fromJson(targetJson, SyncMessage.class);
//                final Map<String,Station> targetStations = StationsParser.jsonArrayToMap(targetJsonObject.getJSONArray("stations"));
                final Map<String,Station> targetStations = targetSyncMessage.getCodeToStationsMap();
//                final Map<String,Boolean> targetOfflines = OfflinesParser.fromOfflinesJsonArray(targetJsonObject.getJSONArray("offlines"));
                final Map<String,Boolean> targetOfflines = targetSyncMessage.getOfflinesMap();
//                final Map<String, Price> targetPrices = PriceParser.jsonArrayToMap(targetJsonObject.getJSONArray("prices"));
                final Map<String, Price> targetPrices = targetSyncMessage.getPricesMap();

                final Vector<Station> modifiedStations = modifications.getModifiedStations();
                final Vector<Station> removedStations = modifications.getRemovedStations();
                final Vector<Offline> modifiedOfflines= modifications.getModifiedOfflines();
                final Vector<Price> modifiedPrices = modifications.getModifiedPrices();
                for(final Station station : modifiedStations) {
%>
        <tr bgcolor="aqua">
            <td><%=sourceStations.get(station.getStationCode())%></td>
            <td><%=targetStations.get(station.getStationCode())%></td>
        </tr>
<%
                }

                for(final Station station : removedStations) {
%>
        <tr bgcolor="#8b0000">
            <td><%=sourceStations.get(station.getStationCode())%></td>
            <td>REMOVED</td>
        </tr>
<%
                }

                for(final Offline offline : modifiedOfflines) {
%>
        <tr bgcolor="#ffe4c4">
            <td><%=sourceOfflines.get(offline.getStationCode())%></td>
            <td><%=targetOfflines.get(offline.getStationCode())%></td>
        </tr>
<%
                }

                for(final Price price : modifiedPrices) {
%>
        <tr bgcolor="#adff2f">
            <td><%=sourcePrices.get(price.getStationCode())%></td>
            <td><%=targetPrices.get(price.getStationCode())%></td>
        </tr>
<%
                }
%>
    </table>
<hr/>
<p><i>Please note that all times are in <a href="http://en.wikipedia.org/wiki/UTC">UTC (Coordinated Universal Time)</a></i></p>

<%
            } catch (Exception e) {
%>
<p>JSON Exception: <%=e.getMessage()%></p>
<%
            }
        }
    }
%>

</body>
</html>