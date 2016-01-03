<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.aspectsense.fuel.server.data.UserEntity" %>
<%@ page import="com.aspectsense.fuel.server.datastore.UserEntityFactory" %>

<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 18/12/2015
  Time: 11:34
  To change this template use File | Settings | File Templates.
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
    UserEntity userEntity;
    if (user == null) {
%>
<p>You need to <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> to use this service.</p>
<%
    } else {
        userEntity = UserEntityFactory.getUserEntity(user.getEmail());
        if(userEntity == null) {
            userEntity = UserEntityFactory.setUserEntity(user.getEmail(), user.getNickname(), false, false);
        }
%>
    <span><img src="../favicon.ico" alt="Cyprus Fuel Guide"/> Logged in as: <%= user.getNickname() %> <b> <%= userEntity.isAdmin() ? "(admin)" : "" %> </b> [<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>]</span>
<%
    }
%></body>
</html>
