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
  ~ along with Foobar. If not, see <http://www.gnu.org/licenses/>.
  --%>

<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.fuel.server.datastore.StationFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.Station" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.aspectsense.fuel.server.datastore.PricesFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.Prices" %>
<%@ page import="java.util.HashMap" %>

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
        final String [] fuelTypeCodes = new String [] { "1", "2", "3", "4"};
        final String [] fuelTypeNames = new String [] { "Petrol 95", "Petrol 98", "Diesel", "Heating"};
        final Prices petrol95Prices = PricesFactory.getLatestPrices(fuelTypeCodes[0]);
        final Map<String, String> petrol95StationCodeToPriceMap = petrol95Prices == null ? new HashMap<String, String>() : petrol95Prices.getStationCodeToPriceMap();
        final Prices petrol98Prices = PricesFactory.getLatestPrices(fuelTypeCodes[1]);
        final Map<String, String> petrol98StationCodeToPriceMap = petrol98Prices == null ? new HashMap<String, String>() : petrol98Prices.getStationCodeToPriceMap();
        final Prices dieselPrices = PricesFactory.getLatestPrices(fuelTypeCodes[2]);
        final Map<String, String> dieselStationCodeToPriceMap = dieselPrices == null ? new HashMap<String, String>() : dieselPrices.getStationCodeToPriceMap();
        final Prices heatingPrices = PricesFactory.getLatestPrices(fuelTypeCodes[3]);
        final Map<String, String> heatingStationCodeToPriceMap = heatingPrices == null ? new HashMap<String, String>() : heatingPrices.getStationCodeToPriceMap();
        final Vector<Station> allStations = StationFactory.getAllStations();
%>

<h1>Prices</h1>

    <table border="1">
        <tr>
            <th>STATION UUID</th>
            <th>CODE</th>
            <th><%=fuelTypeNames[0]%></th>
            <th><%=fuelTypeNames[1]%></th>
            <th><%=fuelTypeNames[2]%></th>
            <th><%=fuelTypeNames[3]%></th>
        </tr>
<%
        for(final Station station : allStations) {
            final String stationCode = station.getStationCode();
%>
        <tr>
            <td><%=station.getShortUuid(8)%></td>
            <td><%=station.getStationCode()%></td>
            <td>
                €<%=petrol95StationCodeToPriceMap.get(stationCode)%>
            </td>
            <td>
                €<%=petrol98StationCodeToPriceMap.get(stationCode)%>
            </td>
            <td>
                €<%=dieselStationCodeToPriceMap.get(stationCode)%>
            </td>
            <td>
                €<%=heatingStationCodeToPriceMap.get(stationCode)%>
            </td>
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