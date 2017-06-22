<%@ page import="com.aspectsense.fuel.server.data.Station" %>
<%@ page import="com.aspectsense.fuel.server.datastore.StationsFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.Stations" %>
<%@ page import="com.aspectsense.fuel.server.json.StationsParser" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.aspectsense.fuel.server.json.PricesParser" %>
<%@ page import="com.aspectsense.fuel.server.datastore.PricesFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.Prices" %>
<%@ page import="com.aspectsense.fuel.server.data.FuelType" %>
<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: Nearchos
  Date: 19-Apr-16
  Time: 8:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="Cyprus Fuel Guide helps you find the best value fuel near you">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
    <title>Cyprus Fuel Guide</title>

    <!-- Add to homescreen for Chrome on Android -->
    <meta name="mobile-web-app-capable" content="yes">
    <link rel="icon" sizes="192x192" href="images/android-desktop.png">

    <!-- Add to homescreen for Safari on iOS -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-title" content="Material Design Lite">
    <link rel="apple-touch-icon-precomposed" href="images/ios-desktop.png">

    <!-- Tile icon for Win8 (144x144 + tile color) -->
    <meta name="msapplication-TileImage" content="images/windows-desktop.png">
    <meta name="msapplication-TileColor" content="#3372DF">

    <link rel="shortcut icon" href="images/favicon.png">

    <!-- SEO: If your mobile URL is different from the desktop URL, add a canonical link to the desktop page https://developers.google.com/webmasters/smartphone-sites/feature-phones -->
    <!--
    <link rel="canonical" href="http://www.example.com/">
    -->

    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.brown-orange.min.css">
    <link rel="stylesheet" href="css/styles.css">
    <style>
        #view-source {
            position: fixed;
            display: block;
            right: 0;
            bottom: 0;
            margin-right: 40px;
            margin-bottom: 40px;
            z-index: 900;
        }
    </style>
    <link rel="alternate" href="android-app://com.aspectsense.fuelguidecy/http/cyprusfuelguide.com/station" />

<%--support for polyfill modal dialogs (https://github.com/GoogleChrome/dialog-polyfill)--%>
    <link rel="stylesheet" type="text/css" href="css/dialog-polyfill.css" />
    <script src="js/dialog-polyfill.js"></script>

</head>
<body>

<dialog class="mdl-dialog" id="dialog-about">
    <span><img src="images/favicon.png" title="Cyprus Fuel Guide"/>&nbsp;<b style="font-size: medium">Cyprus Fuel Guide</b></span>
    <div class="mdl-dialog__content">
        <p>
            The Cyprus Fuel Guide provides an intuitive way to compare fuel prices in your area and find the best deal for you.
        </p>
        <p>
            Website & App developed by <a href="http://aspectsense.com" target="_blank">aspectsense.com</a>.
        </p>
    </div>
    <div class="mdl-dialog__actions">
        <button type="button" class="mdl-button close">Close</button>
    </div>
</dialog>

<dialog class="mdl-dialog" id="dialog-contact">
    <span><img src="images/favicon.png" title="Cyprus Fuel Guide"/>&nbsp;<b style="font-size: medium">Cyprus Fuel Guide</b></span>
    <div class="mdl-dialog__content">
        <p>
            Want to get in touch?
        </p>
        <p>
            Drop us a line at <a href="mailto:hello@cyprusfuelguide.com" target="_blank">hello@cyprusfuelguide.com</a>.
        </p>
    </div>
    <div class="mdl-dialog__actions">
        <button type="button" class="mdl-button close">Close</button>
    </div>
</dialog>

<%
    final String stationCode = request.getParameter("id");
    final Stations stations = StationsFactory.getLatestStations();
    final Map<String,Station> stationsMap = StationsParser.jsonArrayToMap(stations.getJson());
    final Station station = stationsMap.get(stationCode);

    final Map<FuelType, Map<String,Integer>> fuelTypeToPricesMap = new HashMap<>();
    for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
        final Prices prices = PricesFactory.getLatestPrices(fuelType.getCodeAsString());
        final Map<String,Integer> pricesMap = PricesParser.fromPricesJson(prices.getJson());
        fuelTypeToPricesMap.put(fuelType, pricesMap);
    }
%>

<div class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="demo-header mdl-layout__header mdl-color--grey-100 mdl-color-text--grey-600">
        <div class="mdl-layout__header-row">
            <img src="images/favicon.png"/><span class="mdl-layout-title">&nbsp;Cyprus Fuel Guide</span>
            <div class="mdl-layout-spacer"></div>
        </div>
        <!-- Tabs -->
        <div class="mdl-layout__tab-bar mdl-js-ripple-effect">
        </div>
    </header>
    <main class="mdl-layout__content mdl-color--grey-100">
        <div class="mdl-grid demo-content">

            <div class="cfg-city-prices">

                <div class="demo-graphs mdl-shadow--2dp mdl-color--white mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                    <div class="page-content">
                        <div class="cfg-station-header">
                            <img src="images/<%=station.getStationBrand().toLowerCase()%>.png" align="inline">
                            <span class="cfg-station-name"><%=station.getStationName()%></span>
                        </div>

                        <div class="cfg-station-address" style="width: auto">
                            <%=station.getStationAddress()%>, <%=station.getStationDistrict()%><%=station.getStationDistrict().equalsIgnoreCase(station.getStationCity()) ? "" : ", " + station.getStationCity() %>
                        </div>

                    </div>
                </div>

                <div style="height: 16px"></div>

                <div class="demo-graphs mdl-shadow--2dp mdl-color--white mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                    <div class="page-content">
                        <div class="cfg-prices-header">
                            Prices
                        </div>

                        <table class="rmdl-data-table mdl-js-data-table mdl-shadow--2dp" style="width: 100%;">
                            <thead>
                            <tr>
                                <%
                                    for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
                                %>
                                <th class="cfg-price-header-column"><%=fuelType.getNameEn()%></th>
                                <%
                                    }
                                %>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="cfg-prices-table-row">
                                <%
                                    for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
                                        final Map<String,Integer> pricesMap = fuelTypeToPricesMap.get(fuelType);
                                        final String price = pricesMap.containsKey(stationCode) ? String.format("€%5.3f", pricesMap.get(stationCode)/1000f) : "n/a";
                                %>
                                <td class="cfg-price-value-column"><%=price%></td>
                                <%
                                    }
                                %>
                            </tr>
                            </tbody>
                        </table>

                    </div>
                </div>

                <div class="demo-graphs mdl-shadow--2dp mdl-color--white mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                    <div class="page-content">
                        <div class="cfg-location-header">
                            Location
                        </div>

                        <div id="map" class="cfg-map">
                        </div>

                        <div class="cfg-align-right-padding-8">
                            <!-- Accent-colored raised button with ripple -->
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" onclick="window.open('tel:+357<%=station.getStationTelNo()%>','_self')">
                                <i class="material-icons">call</i>
                                <span><%=station.getStationTelNo()%></span>
                            </button>
                            <!-- Accent-colored raised button with ripple -->
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" onclick="window.open('https://maps.google.com/maps?q=<%=station.getStationLatitude()%>,<%=station.getStationLongitude()%>','_self')">
                                <i class="material-icons">navigation</i>
                                <span>Navigate</span>
                            </button>
                        </div>

                    </div>
                </div>

            </div>

            <div class="demo-cards mdl-cell mdl-cell--4-col mdl-cell--8-col-tablet mdl-grid mdl-grid--no-spacing">
                <div class="demo-updates mdl-card mdl-shadow--2dp mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                    <div class="mdl-card__title mdl-card--expand mdl-color--brown-300">
                        <h2 class="mdl-card__title-text">Get the Android app!</h2>
                    </div>
                    <div class="mdl-card__supporting-text mdl-color-text--grey-600">
                        Sporting a cool Android device? Get the App!
                    </div>
                    <div class="mdl-card__actions">
                        <a href="https://play.google.com/store/apps/details?id=com.aspectsense.fuelguidecy" target="_blank">
                            <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="200" title="Android, Google Play and the Google Play logo are trademarks of Google Inc."/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <footer class="mdl-mini-footer">
            <div class="mdl-mini-footer__left-section">
                <div class="mdl-logo">Cyprus Fuel Guide</div>
                <ul class="mdl-mini-footer__link-list">
                    <li><a href="http://cyprusfuelguide.com">Home</a></li>
                    <li><a href="#" onclick="showAboutDialog()">About</a></li>
                    <li><a href="#" onclick="showContactDialog()">Contact us</a></li>
                    <li><a href="privacy">Privacy</a></li>
                </ul>
                <div>Developed by <a href="http://aspectsense.com" target="_blank">aspectsense.com</a> &copy; <script>document.write(new Date().getFullYear())</script></div>
            </div>
        </footer>
    </main>
</div>
<script src="https://code.getmdl.io/1.3.0/material.min.js"></script>
<script>
    function showAboutDialog() {
        var dialog = document.querySelector('#dialog-about');
        dialog.querySelector('.close').addEventListener('click', function() {
            dialog.close();
        });
        dialogPolyfill.registerDialog(dialog);
        dialog.showModal();
    }
    function showContactDialog() {
        var dialog = document.querySelector('#dialog-contact');
        dialog.querySelector('.close').addEventListener('click', function() {
            dialog.close();
        });
        dialogPolyfill.registerDialog(dialog);
        dialog.showModal();
    }
    function initMap() {
        var mapDiv = document.getElementById('map');
        var map = new google.maps.Map(mapDiv, {
            center: {lat: <%=station.getStationLatitude()%>, lng: <%=station.getStationLongitude()%>},
            scrollwheel: false,
            navigationControl: false,
            mapTypeControl: false,
            scaleControl: false,
            draggable: false,
            zoom: 15
        });
        var latLng = new google.maps.LatLng(<%=station.getStationLatitude()%>, <%=station.getStationLongitude()%>);
        var marker = new google.maps.Marker({
            position: latLng,
            map: map,
            icon: 'images/<%=station.getStationBrand().toLowerCase()%>.png'
        });
    }
</script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA71ajdylh0GtnShZRFCQxd-rMvP4Cgmyk&callback=initMap" async defer></script>
<script>
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-319978-22', 'auto');
    ga('send', 'pageview');

</script>
</body>
</html>