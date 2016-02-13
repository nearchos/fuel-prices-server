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

<%@ page import="com.aspectsense.fuel.server.admin.DeleteEntityServlet" %>
<%@ page import="com.aspectsense.fuel.server.data.ApiKey" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.fuel.server.data.Parameter" %>
<%@ page import="com.aspectsense.fuel.server.datastore.ApiKeyFactory" %>
<%@ page import="com.aspectsense.fuel.server.datastore.ParameterFactory" %>

<%--
  User: Nearchos Paspallis
  Date: 18/12/15
  Time: 11:33
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Cyprus Fuel Guide - Parameters</title>
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
        final Vector<ApiKey> allApiKeys = ApiKeyFactory.getAllApiKeys();
        final Vector<Parameter> allParameters = ParameterFactory.getAllParameters();
%>

<h1>Sync Parameters</h1>

    <table border="1">
        <tr>
            <th>NAME</th>
            <th>VALUE</th>
            <th>EDIT</th>
            <th>DELETE</th>
        </tr>
<%
        for(final Parameter parameter : allParameters) {
%>
        <tr>
            <td><%=parameter.getParameterName()%></td>
            <td><%=parameter.getParameterValue()%></td>
            <td>
                <form action="/admin/add-or-edit-parameter" method="post" onsubmit="submitButton.disabled = true; return true;">
                    <input type="text" name="<%= ParameterFactory.PROPERTY_VALUE%>" value="<%=parameter.getParameterValue()%>" required/>
                    <div>
                        <input type="submit" name="submitButton" value="Save" />
                    </div>
                </form>
            </td>
            <td>
                <form action="/admin/delete-entity">
                    <div><input type="submit" value="Delete" /></div>
                    <input type="hidden" name="<%= DeleteEntityServlet.PROPERTY_UUID %>" value="<%= parameter.getUuid() %>"/>
                    <input type="hidden" name="<%= DeleteEntityServlet.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/parameters", "UTF-8") %>"/>
                </form>
            </td>
        </tr>
<%
        }
%>
    </table>

<form action="/admin/add-or-edit-parameter" method="post" onsubmit="submitButton.disabled = true; return true;">
    <table>
        <tr>
            <td>NAME</td>
            <td><input type="text" name="<%= ParameterFactory.PROPERTY_NAME%>" required/></td>
        </tr>
        <tr>
            <td>VALUE</td>
            <td><input type="text" name="<%= ParameterFactory.PROPERTY_VALUE%>" required/></td>
        </tr>
    </table>
    <div><input type="submit" name="submitButton" value="Create Parameter" /></div>
</form>

<h1>API Keys</h1>

<table border="1">
    <tr>
        <th>EMAIL OF REQUESTER</th>
        <th>NOTE</th>
        <th>TIME REQUESTED</th>
        <th>IS ACTIVE</th>
        <th>API KEY</th>
        <th>ACTIVATE/DEACTIVATE?</th>
        <th>DELETE?</th>
    </tr>
    <%
        if(allApiKeys != null)
        {
            for(final ApiKey apiKeyCode : allApiKeys)
            {
    %>
    <tr>
        <td><%= apiKeyCode.getEmailRequester() %></td>
        <td><%= apiKeyCode.getNote() %></td>
        <td><%= timestampFormat.format(new Date(apiKeyCode.getTimeRequested())) %></td>
        <td><%= apiKeyCode.isActive() %></td>
        <td><%= apiKeyCode.getApiKeyCode() %></td>
        <td>
            <form action="/admin/enable-or-disable-api-key">
                <div><input type="submit" value="Toggle" /></div>
                <input type="hidden" name="<%= ApiKeyFactory.PROPERTY_API_KEY_CODE %>" value="<%= apiKeyCode.getApiKeyCode() %>"/>
            </form>
        </td>
        <td>
            <form action="/admin/delete-entity">
                <div><input type="submit" value="Delete" /></div>
                <input type="hidden" name="<%= DeleteEntityServlet.PROPERTY_UUID %>" value="<%= apiKeyCode.getUuid() %>"/>
                <input type="hidden" name="<%= DeleteEntityServlet.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/parameters", "UTF-8") %>"/>
            </form>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>

<hr/>

<form action="/admin/add-api-key" method="post" onsubmit="submitButton.disabled = true; return true;">
    <table>
        <tr>
            <td>EMAIL OF REQUESTER</td>
            <td><%= userEmail %></td>
        </tr>
        <tr>
            <td>NOTE</td>
            <td><input type="text" name="<%= ApiKeyFactory.PROPERTY_NOTE%>" required/></td>
        </tr>
    </table>
    <div><input type="submit" name="submitButton" value="Create API key" /></div>
</form>

<p><i>Please note that all times are in <a href="http://en.wikipedia.org/wiki/UTC">UTC (Coordinated Universal Time)</a></i></p>

<%
    }
%>

</body>
</html>