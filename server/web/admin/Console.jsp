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
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.aspectsense.fuel.server.datastore.PricesFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.Prices" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.aspectsense.fuel.server.data.FuelType" %>

<%--
  User: Nearchos Paspallis
  Date: 18/12/15
  Time: 11:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Cyprus Fuel Guide - Console</title>
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
        Map<Integer,FuelType> codeToFuelTypeMap = FuelType.getCodeToFuelTypeMap();
        int NUM_OF_ENTRIES_TO_RETURN = 20;
        final Vector<Prices> pricesVector = PricesFactory.queryLatestPrices(NUM_OF_ENTRIES_TO_RETURN);
        int maxSize = 0;
        final Map<String,Vector<Prices>> fuelTypesToPrices = new HashMap<>();
        for(final Prices prices : pricesVector) {
            final String fuelType = prices.getFuelType();
            if(!fuelTypesToPrices.containsKey(fuelType)) fuelTypesToPrices.put(fuelType, new Vector<Prices>());
            fuelTypesToPrices.get(fuelType).add(prices);
            maxSize = Math.max(maxSize, fuelTypesToPrices.get(fuelType).size());
        }

%>

<h1>Console</h1>

    <table border="1">
        <tr>
            <th>FUEL TYPE</th>
            <th>TIMESTAMP</th>
            <th>FORMATTED</th>
        </tr>
<%
        for(final Prices prices : pricesVector) {
            int fuelTypeCode = Integer.parseInt(prices.getFuelType());
%>
        <tr>
            <td><%=fuelTypeCode%> (<%=codeToFuelTypeMap.get(fuelTypeCode)%>)</td>
            <td><%=prices.getLastUpdated()%></td>
            <td><%=timestampFormat.format(new Date(prices.getLastUpdated()))%></td>
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