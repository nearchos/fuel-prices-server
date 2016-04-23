<%@ page import="com.aspectsense.fuel.server.datastore.PricesFactory" %>
<%@ page import="com.aspectsense.fuel.server.datastore.StationsFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.*" %>
<%@ page import="com.aspectsense.fuel.server.json.StationsParser" %>
<%@ page import="com.aspectsense.fuel.server.json.PricesParser" %>
<%@ page import="static com.google.appengine.api.memcache.MemcacheServicePb.MemcacheService_3.Method.Set" %>
<%@ page import="java.util.*" %>
<%@ page import="com.aspectsense.fuel.server.json.Util" %>
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

<%--
  User: Nearchos Paspallis
  Date: 18/12/2015
  Time: 11:22
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Cyprus Fuel Guide</title>
  </head>
  <body>
    <a href="https://play.google.com/store/apps/details?id=com.aspectsense.cyprusfuelguide&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-AC-global-none-all-co-pr-py-PartBadges-Oct1515-1">
      <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="200" title="Android, Google Play and the Google Play logo are trademarks of Google Inc."/>
    </a>

    <h1>Cyprus Fuel Guide</h1>

    <form>
      <input type="radio" name="fuelType" value="<%=FuelType.UNLEADED_95.getName()%>" checked>Unleaded 95 |
      <input type="radio" name="fuelType" value="<%=FuelType.UNLEADED_98.getName()%>">Unleaded 98 |
      <input type="radio" name="fuelType" value="<%=FuelType.DIESEL.getName()%>">Diesel |
      <input type="radio" name="fuelType" value="<%=FuelType.HEATING.getName()%>">Heating |
      <input type="radio" name="fuelType" value="<%=FuelType.KEROSENE.getName()%>">Kerosene
    </form>

    <%
        final String stationsJson = StationsFactory.getLatestStations().getJson();
        final Map<String,Station> stationsMap = StationsParser.jsonArrayToMap(stationsJson);
        final FuelType selectedFuelType = FuelType.UNLEADED_95;
        final Prices pricesUnleaded95 = PricesFactory.getLatestPrices(selectedFuelType.getCodeAsString());
        final Map<String,Integer> pricesU95 = PricesParser.fromPricesJson(pricesUnleaded95.getJson());
        final Map<City,Set<String>> cheapestStationsPerCityU95 = Util.findCheapestStationsPerCity(stationsMap, pricesU95);

        for(final City city : City.ALL_CITIES) {
    %>
    <div>
      <h1><%=city.getNameEl()%></h1>
    <%
        for(final String stationCode : cheapestStationsPerCityU95.get(city)) {
            final Station station = stationsMap.get(stationCode);
    %>
        <h3> <img src="<%="images/" + station.getStationBrand().toLowerCase() + ".png"%>" width="64"/> <span style="color: green"><%=String.format("€%5.3f", pricesU95.get(stationCode)/1000f)%></span> @ <%=station.getStationBrand()%> </h3>
        <p> <i><a href="http://maps.google.com/maps?z=14&q=loc:<%=station.getStationLatitude()%>+<%=station.getStationLongitude()%>&hl=el" target="_blank"><%=station.getStationAddress()%>, <%=station.getStationDistrict()%></a></i> </p>
    <%
        }
    %>
    </div>
    <%
      }
    %>
  </body>
</html>
