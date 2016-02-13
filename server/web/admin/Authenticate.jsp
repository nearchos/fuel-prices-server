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

<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.aspectsense.fuel.server.data.UserEntity" %>
<%@ page import="com.aspectsense.fuel.server.datastore.UserEntityFactory" %>

<%--
  User: Nearchos Paspallis
  Date: 18/12/2015
  Time: 11:34
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Fuel Guide - Parameters</title>
</head>
<body>
<%
    final UserService userService = UserServiceFactory.getUserService();
    final User user = userService.getCurrentUser();
    final String userEmail = user == null ? "Unknown" : user.getEmail();
    UserEntity userEntity = null;
    if (user == null) {
%>
<p>You need to <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> to use this service.</p>
<%
    } else {
        userEntity = UserEntityFactory.getUserEntity(user.getEmail());
        if(userEntity == null) {
            userEntity = UserEntityFactory.setUserEntity(user.getEmail(), user.getNickname(), false);
        }
%>
    <span><img src="../favicon.ico" alt="Cyprus Fuel Guide"/> Logged in as: <%= user.getNickname() %> <b> <%= userEntity.isAdmin() ? "(admin)" : "" %> </b> [<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>]</span>
<%
    }
%></body>

</html>