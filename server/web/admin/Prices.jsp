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
<%@ page import="com.aspectsense.fuel.server.datastore.PricesFactory" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.aspectsense.fuel.server.datastore.StationsFactory" %>
<%@ page import="com.aspectsense.fuel.server.json.StationsParser" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.fuel.server.data.*" %>

<%--
  User: Nearchos Paspallis
  Date: 18/12/15
  Time: 11:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Cyprus Fuel Guide - Prices</title>
</head>

<body>

<%@ include file="Authenticate.jsp" %>

<%
    final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    if(userEntity == null) {
%>
You are not logged in!
<%
    } else if(!userEntity.isAdmin()) {
%>
<p>You are not admin!</p>
<%
    } else {
        final Map<FuelType,Map<String,Prices.PriceInMillieurosAndTimestamp>> fuelTypesToPricesMap = new HashMap<>();
%>

<h1>Prices</h1>

    <%--allPrices: <%=allPrices%>--%>
    <table border="1">
        <tr>
            <th>STATION CODE</th>
            <th>STATION NAME</th>
            <th>STATION ADDRESS</th>
<%
        for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            final Prices prices = PricesFactory.getLatestPrices(fuelType.getCodeAsString());
            fuelTypesToPricesMap.put(fuelType, prices.getStationCodeToPriceInMillieurosAndTimestampMap());
%>
            <th>
                <%=fuelType.getName()%>
                <br/>
                <%=timestampFormat.format(new Date(prices.getLastUpdated()))%>
            </th>
<%
        }
%>
        </tr>
<%
        final Stations stations = StationsFactory.getLatestStations();
        final Vector<Station> allStations = StationsParser.fromStationsJson(stations.getJson());
        for(final Station station : allStations) {
            final String stationCode = station.getStationCode();
%>
        <tr>
            <td><%=stationCode%></td>
            <td><%=station.getStationName()%></td>
            <td><%=station.getStationAddress()%>, <%=station.getStationDistrict()%>, <%=station.getStationCity()%></td>
<%
            for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
                final Map<String,Prices.PriceInMillieurosAndTimestamp> stationCodeToPriceInMillieurosAndTimestampMap = fuelTypesToPricesMap.get(fuelType);
                final String priceFormatted;
                if(stationCodeToPriceInMillieurosAndTimestampMap == null) {
                    priceFormatted = "unknown";
                } else {
                    if(stationCodeToPriceInMillieurosAndTimestampMap.containsKey(stationCode)) {
                        final Prices.PriceInMillieurosAndTimestamp priceInMillieurosAndTimestamp = stationCodeToPriceInMillieurosAndTimestampMap.get(stationCode);
                        priceFormatted = String.format("€%5.3f", priceInMillieurosAndTimestamp.getPriceInMillieuros() / 1000d);
                    } else {
                        priceFormatted = "unknown station";
                    }
                }
%>
            <td>
                <%=priceFormatted%>
            </td>
<%
            }
%>
        </tr>
<%
        }
%>
    </table>

<hr/>

<p><i>Please note that all times are in <a href="http://en.wikipedia.org/wiki/UTC">UTC (Coordinated Universal Time)</a></i></p>

<%
    }
%>

</body>
</html>