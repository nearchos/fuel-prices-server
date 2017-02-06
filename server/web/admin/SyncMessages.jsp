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
<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.fuel.server.datastore.SyncMessageFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.SyncMessage" %>

<%--
  User: Nearchos Paspallis
  Date: 18/12/15
  Time: 11:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Cyprus Fuel Guide - Sync Messages</title>
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
        int NUM_OF_ENTRIES_TO_RETURN = 20;
        int NUM_OF_CHARACTERS_TO_SHOW = 128;
        final Vector<SyncMessage> syncMessages = SyncMessageFactory.queryLatestSyncMessages(NUM_OF_ENTRIES_TO_RETURN);
%>

<h1>Sync Messages</h1>

    <table border="1">
        <tr>
            <th>SYNC MESSAGE</th>
            <th>TIMESTAMP</th>
            <th>FORMATTED</th>
            <th>NUM OF CHANGES</th>
            <th>COMPARE</th>
        </tr>
<%
        for(int i = 0; i < syncMessages.size(); i++) {
            final SyncMessage syncMessage = syncMessages.elementAt(i);
            final String json = syncMessage.getJson();
%>
        <tr>
            <td><%=json.substring(Math.max(0, json.length() - NUM_OF_CHARACTERS_TO_SHOW))%></td>
            <td><%=syncMessage.getLastUpdated()%></td>
            <td><%=timestampFormat.format(new Date(syncMessage.getLastUpdated()))%></td>
            <td><%=syncMessage.getNumOfChanges()%></td>
            <td>
<%
            if(i < syncMessages.size() - 1) {
                final SyncMessage nextSyncMessage = syncMessages.elementAt(i+1);
                final long distanceFromPreviousInMilliseconds =  syncMessage.getLastUpdated() - nextSyncMessage.getLastUpdated();
                long hours = distanceFromPreviousInMilliseconds / (60 * 60 * 1000);
                long minutes = (distanceFromPreviousInMilliseconds - (hours * 60 * 60 * 1000)) / (60 * 1000);
                long seconds = (distanceFromPreviousInMilliseconds - (minutes * 60 * 1000)) / 1000L;
%>
                <a href="/admin/difference?from=<%=nextSyncMessage.getLastUpdated()%>&to=<%=syncMessage.getLastUpdated()%>"><%=hours%> hrs, <%=minutes%> mins, <%=seconds%> secs</a>
<%
            }
%>
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