<%@ page import="com.aspectsense.fuel.server.datastore.PricesFactory" %>
<%@ page import="com.aspectsense.fuel.server.datastore.StationsFactory" %>
<%@ page import="com.aspectsense.fuel.server.data.*" %>
<%@ page import="com.aspectsense.fuel.server.json.StationsParser" %>
<%@ page import="com.aspectsense.fuel.server.json.PricesParser" %>
<%@ page import="java.util.*" %>
<%@ page import="com.aspectsense.fuel.server.json.Util" %>
<%@ page import="com.aspectsense.fuel.server.model.Station" %>

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

    <script>

        function getLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(sortCities, showError);
            } else {
                document.getElementById("location").innerHTML="Geolocation is not supported by this browser.";
            }
        }

        function showError(error) {
            switch(error.code) {
                case error.PERMISSION_DENIED:
                    document.getElementById("location").innerHTML="User denied the request for Geolocation."; break;
                case error.POSITION_UNAVAILABLE:
                    document.getElementById("location").innerHTML="Location information is unavailable."; break;
                case error.TIMEOUT:
                    document.getElementById("location").innerHTML="The request to get user location timed out."; break;
                case error.UNKNOWN_ERROR:
                    document.getElementById("location").innerHTML="An unknown error occurred."; break;
            }
        }

        function insertAfter(newNode, referenceNode) {
            referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
        }

        function sortCities(position) {
            var lat = position.coords.latitude;
            var lng = position.coords.longitude;

            nicosia =   {lat: 35.16932,     lng: 33.36014};
            limassol =  {lat: 34.679038,    lng:33.044171};
            larnaca =   {lat: 34.9177,      lng:33.6319};
            paphos =    {lat: 34.75572,     lng:32.41542};
            famagusta = {lat: 35.1174,      lng:33.941};

            var citiesAndDistances = new Array(5);
            citiesAndDistances[0] = {city: "nicosia",   distance: computeDistance(lat,lng,nicosia.lat,nicosia.lng)};
            citiesAndDistances[1] = {city: "limassol",  distance: computeDistance(lat,lng,limassol.lat,limassol.lng)};
            citiesAndDistances[2] = {city: "larnaca",   distance: computeDistance(lat,lng,larnaca.lat,larnaca.lng)};
            citiesAndDistances[3] = {city: "paphos",    distance: computeDistance(lat,lng,paphos.lat,paphos.lng)};
            citiesAndDistances[4] = {city: "famagusta", distance: computeDistance(lat,lng,famagusta.lat,famagusta.lng)};

            citiesAndDistances.sort(function(city1,city2) {return city1.distance - city2.distance});

            var fuelTypes = ["1", "2", "3", "4", "5"];
            var arrayLength = fuelTypes.length;
            var i;
            for (i = 0; i < arrayLength; i++) {
                var city0 = document.getElementById(citiesAndDistances[0].city + "-" + fuelTypes[i]);
                var city1 = document.getElementById(citiesAndDistances[1].city + "-" + fuelTypes[i]);
                var city2 = document.getElementById(citiesAndDistances[2].city + "-" + fuelTypes[i]);
                var city3 = document.getElementById(citiesAndDistances[3].city + "-" + fuelTypes[i]);
                var city4 = document.getElementById(citiesAndDistances[4].city + "-" + fuelTypes[i]);

                insertAfter(city1, city0);
                insertAfter(city2, city1);
                insertAfter(city3, city2);
                insertAfter(city4, city3);
            }
        }

        function sortStations(position) {
            var lat = position.coords.latitude;
            var lng = position.coords.longitude;

            var stationsAndDistances = new Array(5);
            stationsAndDistances[0] = {city: "nicosia",   distance: computeDistance(lat,lng,nicosia.lat,nicosia.lng)};
            stationsAndDistances[1] = {city: "limassol",  distance: computeDistance(lat,lng,limassol.lat,limassol.lng)};
            stationsAndDistances[2] = {city: "larnaca",   distance: computeDistance(lat,lng,larnaca.lat,larnaca.lng)};
            stationsAndDistances[3] = {city: "paphos",    distance: computeDistance(lat,lng,paphos.lat,paphos.lng)};
            stationsAndDistances[4] = {city: "famagusta", distance: computeDistance(lat,lng,famagusta.lat,famagusta.lng)};

            citiesAndDistances.sort(function(city1,city2) {return city1.distance - city2.distance});

            var fuelTypes = ["1", "2", "3", "4", "5"];
            var arrayLength = fuelTypes.length;
            var i;
            for (i = 0; i < arrayLength; i++) {
                var city0 = document.getElementById(citiesAndDistances[0].city + "-" + fuelTypes[i]);
                var city1 = document.getElementById(citiesAndDistances[1].city + "-" + fuelTypes[i]);
                var city2 = document.getElementById(citiesAndDistances[2].city + "-" + fuelTypes[i]);
                var city3 = document.getElementById(citiesAndDistances[3].city + "-" + fuelTypes[i]);
                var city4 = document.getElementById(citiesAndDistances[4].city + "-" + fuelTypes[i]);

                insertAfter(city1, city0);
                insertAfter(city2, city1);
                insertAfter(city3, city2);
                insertAfter(city4, city3);
            }
        }

        function computeDistance(lat1, lng1, lat2, lng2) {
            var R = 6371; // km
            var dLat = (lat2-lat1).toRad();
            var dLng = (lng2-lng1).toRad();
            var latr1 = lat1.toRad();
            var latr2 = lat2.toRad();

            var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.sin(dLng/2) * Math.sin(dLng/2) * Math.cos(latr1) * Math.cos(latr2);
            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            return R * c;
        }

        /** Converts numeric degrees to radians */
        if (typeof(Number.prototype.toRad) === "undefined") {
            Number.prototype.toRad = function() {
                return this * Math.PI / 180;
            }
        }
    </script>

    <%--support for polyfill modal dialogs (https://github.com/GoogleChrome/dialog-polyfill)--%>
    <link rel="stylesheet" type="text/css" href="css/dialog-polyfill.css" />
    <script src="js/dialog-polyfill.js"></script>

</head>
<body onload="updateFuelType(1); getLocation();">

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

<div class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="demo-header mdl-layout__header mdl-color--grey-100 mdl-color-text--grey-600">
        <div class="mdl-layout__header-row">
            <img src="images/favicon.png"/><span class="mdl-layout-title">&nbsp;Cyprus Fuel Guide</span>
            <div class="mdl-layout-spacer"></div>
            <button class="mdl-button mdl-js-button mdl-js-ripple-effect mdl-button--icon" id="hdrbtn">
                <i class="material-icons">more_vert</i>
            </button>
            <ul class="mdl-menu mdl-js-menu mdl-js-ripple-effect mdl-menu--bottom-right" for="hdrbtn">
                <li class="mdl-menu__item" onclick="showAboutDialog()">About</li>
                <li class="mdl-menu__item" onclick="showContactDialog()">Contact</li>
                <li class="mdl-menu__item" onclick="window.open('privacy','_self')">Privacy & Terms</li>
            </ul>
        </div>
        <!-- Tabs -->
        <div class="mdl-layout__tab-bar mdl-js-ripple-effect">
            <%
                boolean first = true;
                for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            %>
                    <a href="#scroll-tab-<%=fuelType.getCodeAsString()%>" class="mdl-layout__tab<%=first ? " is-active" : ""%>" onclick="updateFuelType(<%=fuelType.getCodeAsString()%>)"><%=fuelType.getName()%></a>
            <%
                    first = false;
                }
            %>
        </div>
    </header>
    <%
        final String stationsJson = StationsFactory.getLatestStations().getJson();
        final Map<String,Station> stationsMap = StationsParser.jsonArrayToMap(stationsJson);
        final Map<FuelType, Map<City,Set<String>>> fuelTypeToCheapestStationsPerCity = new HashMap<>();
        final Map<FuelType, Map<String,Integer>> fuelTypeToPricesMap = new HashMap<>();
        for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            final Prices prices = PricesFactory.getLatestPrices(fuelType.getCodeAsString());
            final Map<String,Integer> pricesMap = PricesParser.fromPricesJson(prices.getJson());
            fuelTypeToPricesMap.put(fuelType, pricesMap);
            fuelTypeToCheapestStationsPerCity.put(fuelType, Util.findCheapestStationsPerCity(stationsMap, pricesMap));
        }
    %>
        <main class="mdl-layout__content mdl-color--grey-100">
        <div class="mdl-grid demo-content">
            <div class="demo-updates mdl-shadow--2dp mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                <div class="mdl-card__title mdl-color--brown-300">
                    <h2 class="mdl-card__title-text">Best prices</h2>
                </div>
    <%
        for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
            Map<City,Set<String>> cheapestStationsPerCity = fuelTypeToCheapestStationsPerCity.get(fuelType);
            final City [] allCities = City.ALL_CITIES;
    %>
                <div class="mdl-color--white mdl-color-text--brown-600" style="width: auto" id="cheapestPrices-<%=fuelType.getCodeAsString()%>">
                    <%--<b><%=fuelType.getNameEn().toUpperCase()%></b>--%>

                    <table class="rmdl-data-table mdl-js-data-table mdl-shadow--2dp" style="width: 100%;">
                        <thead>
                        <tr>
                            <%
                                for(final City city : allCities) {
                            %>
                            <th class="cfg-price-header-column"><a href="#<%=city.getNameEn().toLowerCase()%>-<%=fuelType.getCodeAsString()%>"><%=city.getNameEn()%></a></th>
                            <%
                                }
                            %>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="cfg-prices-table-row">
                            <%
                                for(final City city : allCities) {
                                    final Set<String> stationCodes = cheapestStationsPerCity.get(city);
                                    final int price = fuelTypeToPricesMap.get(fuelType).get(stationCodes.iterator().next());
                            %>
                            <td class="cfg-price-value-column"><%=String.format("€%5.3f", price/1000f)%></td>
                            <%
                                }
                            %>
                        </tr>
                        </tbody>
                    </table>

                </div>
    <%
        }
    %>
            </div>
            <div class="cfg-city-prices">
                <%
                    boolean active = true;
                    for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
                %>
                <section class="mdl-layout__tab-panel <%=active ? "is-active" : ""%>" id="scroll-tab-<%=fuelType.getCodeAsString()%>">
                    <%
                        active = false;
                        for(final City city : City.ALL_CITIES) {
                    %>
                    <a name="<%=city.getNameEn().toLowerCase()%>-<%=fuelType.getCodeAsString()%>"></a>
                    <div class="demo-graphs mdl-shadow--2dp mdl-color--white mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop" id="<%=city.getNameEn().toLowerCase()%>-<%=fuelType.getCodeAsString()%>">
                        <div class="page-content">
                            <div class="cfg-city-header">
                                <%=city.getNameEn()%>
                            </div>

                            <%
                                final Map<City,Set<String>> cheapestStationsPerCity = fuelTypeToCheapestStationsPerCity.get(fuelType);
                                final Set<String> cheapestStations = cheapestStationsPerCity.get(city);
                                int counter = 0;
                                for(final String stationCode : cheapestStations) {
                                    counter++;
                                    final Station station = stationsMap.get(stationCode);
                                    final Map<String,Integer> pricesMap = fuelTypeToPricesMap.get(fuelType);
                            %>
                            <div class="cfg-station-header" id="<%=city.getNameEn().toLowerCase()%>-<%=fuelType.getCodeAsString()%>-<%=counter%>" style="display: <%=counter > 3 ? "none": "block"%>">
                                <a href="station?id=<%=station.getStationCode()%>"><span class="cfg-empty-span-trick"></span></a>
                                <img src="images/<%=station.getStationBrand().toLowerCase()%>.png" align="inline">
                                <span class="cfg-price"><%=String.format("€%5.3f", pricesMap.get(stationCode)/1000f)%></span> <a href="http://maps.google.com/maps?z=14&q=loc:<%=station.getStationLatitude()%>+<%=station.getStationLongitude()%>&hl=en" target="_blank"><%=station.getStationAddress()%>, <%=station.getStationDistrict()%></a>
                            </div>
                            <%
                                }

                                if(counter > 3) {
                            %>
                            <div class="cfg-align-right-padding-8" id="showMore-<%=city.getNameEn().toLowerCase()%>-<%=fuelType.getCodeAsString()%>">
                                <!-- Accent-colored raised button with ripple -->
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" onclick="showMore('<%=city.getNameEn().toLowerCase()%>-<%=fuelType.getCodeAsString()%>',<%=counter%>)">
                                    <span id="nicosiaShowMore">Show more</span>
                                </button>
                            </div>

                            <%
                                }
                            %>
                        </div>
                    </div>
                    <%--<div style="height: 16px"></div>--%>
                    <%
                        }
                    %>
                </section>
                <%
                    }
                %>
            </div>
            <div class="demo-cards mdl-cell mdl-cell--4-col mdl-cell--8-col-tablet mdl-grid mdl-grid--no-spacing">
                <div class="demo-updates mdl-card mdl-shadow--2dp mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                    <div class="mdl-card__title mdl-card--expand mdl-color--brown-300">
                        <h2 class="mdl-card__title-text">Get the Android app!</h2>
                    </div>
                    <div class="mdl-card__supporting-text mdl-color-text--grey-600">
                        Sporting a cool Android device? Get the App!
                        <br/>
                        <br/>
                        <a href="https://play.google.com/store/apps/details?id=com.aspectsense.fuelguidecy" target="_blank">
                            <img src="images/cfg-in-nexus-5x.png" alt="Cyprus Fuel Guide - App in device screenshot"/>
                        </a>
                    </div>
                    <div class="mdl-card__actions">
                        <a href="https://play.google.com/store/apps/details?id=com.aspectsense.fuelguidecy" target="_blank">
                            <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="200" title="Android, Google Play and the Google Play logo are trademarks of Google Inc."/>
                        </a>

                        <!--<a href="https://play.google.com/store/apps/details?id=com.aspectsense.fuelguidecy" target="_blank" class="mdl-button mdl-js-button mdl-color-text&#45;&#45;amber mdl-js-ripple-effect">Install</a>-->
                    </div>
                </div>
            </div>
        </div>
        <footer class="mdl-mini-footer">
            <div class="mdl-mini-footer__left-section">
                <div class="mdl-logo">Cyprus Fuel Guide</div>
                <ul class="mdl-mini-footer__link-list">
                    <li><a href="#" onclick="showAboutDialog()">About</a></li>
                    <li><a href="#" onclick="showContactDialog()">Contact</a></li>
                    <li><a href="privacy">Privacy & Terms</a></li>
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

    function updateFuelType(fuelTypeCode) {
        if(fuelTypeCode === undefined) {
            fuelTypeCode = 1;
        }
        var fuelTypes = ["1", "2", "3", "4", "5"];
        var arrayLength = fuelTypes.length;
        var i;
        for (i = 0; i < arrayLength; i++) {
            var id = "cheapestPrices-" + fuelTypes[i];
            var cheapestPrices = document.getElementById(id);
            if(fuelTypes[i] == fuelTypeCode) {
                cheapestPrices.style.display = "block";
            } else {
                cheapestPrices.style.display = "none";
            }
        }

    }

    function showMore(cityNameAndFuelType,numOfStations) {
        for(i = 1; i <= numOfStations; i++) {
            var stationDiv = document.getElementById(cityNameAndFuelType + "-" + i);
            stationDiv.style.display = "block";
        }
        var showMoreDiv = document.getElementById("showMore-" + cityNameAndFuelType);
        showMoreDiv.style.display = "none";
    }
</script>
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