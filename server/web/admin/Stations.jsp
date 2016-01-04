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

<%@ page import="com.aspectsense.fuel.server.admin.DeleteEntityServlet" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.fuel.server.datastore.StationFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.Station" %>

<%--
  User: Nearchos Paspallis
  Date: 18/12/15
  Time: 11:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Cyprus Fuel Guide - Stations</title>
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
        final Vector<Station> allStations = StationFactory.getAllStations();
%>

<h1>Stations</h1>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>COMPANY</th>
            <th>CODE</th>
            <th>TEL-NO</th>
            <th>ADDRESS</th>
            <th>COORDINATES</th>
            <th>OFFLINE</th>
            <th>LAST MODIFIED</th>
            <th>DELETE</th>
        </tr>
<%
        for(final Station station : allStations) {
%>
        <tr>
            <td><%=station.getShortUuid(8)%></td>
            <td><%=station.getFuelCompanyCode()%> (<%=station.getFuelCompanyName()%>)</td>
            <td><%=station.getStationCode()%></td>
            <td><%=station.getStationTelNo()%></td>
            <td><%=station.getStationAddress()%>, <%=station.getStationDistrict()%>, <%=station.getStationCity()%></td>
            <td><a href="https://www.google.com/maps/@<%=station.getStationLatitude()%>,<%=station.getStationLongitude()%>,18z"><%=station.getStationLatitude()%>, <%=station.getStationLongitude()%></a></td>
            <td><%=station.isOffline()%></td>
            <td><%=timestampFormat.format(new Date(station.getLastModified()))%></td>
            <td>
                <form action="/admin/delete-entity">
                    <div><input type="submit" value="Delete" /></div>
                    <input type="hidden" name="<%= DeleteEntityServlet.PROPERTY_UUID %>" value="<%= station.getUuid() %>"/>
                    <input type="hidden" name="<%= DeleteEntityServlet.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/stations", "UTF-8") %>"/>
                </form>
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