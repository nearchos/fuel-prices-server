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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="Cyprus Fuel Guide helps you find the best value fuel near you">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
    <title>Cyprus Fuel Guide</title>

    <!-- Add to homescreen for Chrome on Android -->
    <meta name="mobile-web-app-capable" content="yes">
    <link rel="icon" sizes="192x192" href="../images/android-desktop.png">

    <!-- Add to homescreen for Safari on iOS -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-title" content="Material Design Lite">
    <link rel="apple-touch-icon-precomposed" href="../images/ios-desktop.png">

    <!-- Tile icon for Win8 (144x144 + tile color) -->
    <meta name="msapplication-TileImage" content="images/windows-desktop.png">
    <meta name="msapplication-TileColor" content="#3372DF">

    <link rel="shortcut icon" href="../images/favicon.png">

    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.brown-orange.min.css">
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
    <link rel="stylesheet" href="../css/styles.css">
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

    <link rel="stylesheet" type="text/css" href="../css/dialog-polyfill.css" />
    <script src="../js/dialog-polyfill.js"></script>

</head>

<body>

<dialog class="mdl-dialog" id="dialog-about">
    <span><img src="http://cyprusfuelguide.com/images/favicon.png" title="Cyprus Fuel Guide"/>&nbsp;<b style="font-size: medium">Cyprus Fuel Guide</b></span>
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
    <span><img src="http://cyprusfuelguide.com/images/favicon.png" title="Cyprus Fuel Guide"/>&nbsp;<b style="font-size: medium">Cyprus Fuel Guide</b></span>
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
            <a href="http://cyprusfuelguide.com"><img src="http://cyprusfuelguide.com/images/favicon.png"/></a><span class="mdl-layout-title">&nbsp;Cyprus Fuel Guide</span>
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

            <a href="#all-fuel-types" class="mdl-layout__tab is-active" onclick="updateFuelType(0)">unleaded 95</a>
            <a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateFuelType(1)">unleaded 98</a>
            <a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateFuelType(2)">diesel</a>
            <a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateFuelType(3)">heating</a>
            <a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateFuelType(4)">kerosene</a>

        </div>
    </header>

    <main class="mdl-layout__content mdl-color--grey-100">

        <div class="mdl-grid demo-content">

            <div class="demo-updates mdl-shadow--2dp mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                <div class="mdl-card__title mdl-color--brown-300">
                    <div class="options__item">
                        <label class="cfg-title" for="periodDropdown">Select Period&nbsp;</label>
                        <select class="option-input option-dropdown" id="periodDropdown">
                        </select>
                    </div>
                </div>
            </div>

            <div class="mdl-shadow--2dp mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop mdl-color--white">

                <div class="mdl-card__title">

                    <div class="page-content" id="report">
                        <div class="cfg-report-title" style="width: 960px;">Report</div>
                        <div class="cfg-report-subtitle" id="periodSubtitle"></div>

                        <div class="cfg-report-text" id="periodBody"></div>

                        <div class="cfg-card-container mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">

                            <div class="cfg-card-square mdl-card mdl-shadow--2dp">
                                <div class="mdl-card__title mdl-card--expand">
                                    <p class="mdl-card__title-text" style="color: saddlebrown; font-weight: normal;">#1 station</p>
                                </div>
                                <div class="mdl-card__supporting-text" id="cheapestStationBody">
                                    Details...
                                </div>
                                <div class="mdl-card__actions mdl-card--border">
                                    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" href="#stations" target="_blank" id="cheapestStationLink">
                                        View station
                                    </a>
                                </div>
                            </div>

                            <div class="cfg-card-square mdl-card mdl-shadow--2dp">
                                <div class="mdl-card__title mdl-card--expand">
                                    <p class="mdl-card__title-text" style="color: saddlebrown; font-weight: normal;">#1 city</p>
                                </div>
                                <div class="mdl-card__supporting-text" id="cheapestCityBody">
                                    Details...
                                </div>
                                <div class="mdl-card__actions mdl-card--border">
                                    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" href="#cities" target="_blank" id="cheapestCityLink">
                                        View city
                                    </a>
                                </div>
                            </div>

                            <div class="cfg-card-square mdl-card mdl-shadow--2dp">
                                <div class="mdl-card__title mdl-card--expand">
                                    <p class="mdl-card__title-text" style="color: saddlebrown; font-weight: normal;">#1 brand</p>
                                </div>
                                <div class="mdl-card__supporting-text" id="cheapestBrandBody">
                                    Details...
                                </div>
                                <div class="mdl-card__actions mdl-card--border">
                                    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" href="#brands" target="_blank" id="cheapestBrandLink">
                                        View brand
                                    </a>
                                </div>
                            </div>

                        </div>


                        <div class="cfg-card-container mdl-cell--2-col mdl-cell--2-col-tablet mdl-cell--12-col-desktop">

                            <div class="cfg-card-city mdl-card mdl-shadow--2dp">
                                <div class="mdl-card__title mdl-card--expand">
                                    <p class="mdl-card__title-text" style="color: saddlebrown; font-weight: normal;" id="nicosiaCard">Nicosia</p>
                                </div>
                                <div class="mdl-card__supporting-text" id="nicosiaCardBody">
                                </div>
                                <div class="mdl-card__actions mdl-card--border">
                                    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" href="#brands" target="_blank" id="cheapestInNicosiaLink">
                                        View station
                                    </a>
                                </div>
                            </div>

                            <div class="cfg-card-city mdl-card mdl-shadow--2dp">
                                <div class="mdl-card__title mdl-card--expand">
                                    <p class="mdl-card__title-text" style="color: saddlebrown; font-weight: normal;" id="limassolCard">Limassol</p>
                                </div>
                                <div class="mdl-card__supporting-text" id="limassolCardBody">
                                    Details...
                                </div>
                                <div class="mdl-card__actions mdl-card--border">
                                    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" href="#brands" target="_blank" id="cheapestInLimassolLink">
                                        View station
                                    </a>
                                </div>
                            </div>

                            <div class="cfg-card-city mdl-card mdl-shadow--2dp">
                                <div class="mdl-card__title mdl-card--expand">
                                    <p class="mdl-card__title-text" style="color: saddlebrown; font-weight: normal;" id="famagustaCard">Famagusta</p>
                                </div>
                                <div class="mdl-card__supporting-text" id="famagustaCardBody">
                                    Details...
                                </div>
                                <div class="mdl-card__actions mdl-card--border">
                                    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" href="#brands" target="_blank" id="cheapestInFamagustaLink">
                                        View station
                                    </a>
                                </div>
                            </div>

                            <div class="cfg-card-city mdl-card mdl-shadow--2dp">
                                <div class="mdl-card__title mdl-card--expand">
                                    <p class="mdl-card__title-text" style="color: saddlebrown; font-weight: normal;" id="larnacaCard">Larnaca</p>
                                </div>
                                <div class="mdl-card__supporting-text" id="larnacaCardBody">
                                    Details...
                                </div>
                                <div class="mdl-card__actions mdl-card--border">
                                    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" href="#brands" target="_blank" id="cheapestInLarnacaLink">
                                        View station
                                    </a>
                                </div>
                            </div>

                            <div class="cfg-card-city mdl-card mdl-shadow--2dp">
                                <div class="mdl-card__title mdl-card--expand">
                                    <p class="mdl-card__title-text" style="color: saddlebrown; font-weight: normal;" id="paphosCard">Paphos</p>
                                </div>
                                <div class="mdl-card__supporting-text" id="paphosCardBody">
                                    Details...
                                </div>
                                <div class="mdl-card__actions mdl-card--border">
                                    <a class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" href="#brands" target="_blank" id="cheapestInPaphosLink">
                                        View station
                                    </a>
                                </div>
                            </div>

                        </div>


                        <!-- Tabs for tables -->
                        <div class="cfg-table-tabs mdl-tabs mdl-js-tabs mdl-js-ripple-effect">
                            <div class="mdl-tabs__tab-bar" style="width: 960px; margin-left: 40px;">
                                <a href="#stations" class="mdl-tabs__tab is-active">Stations</a>
                                <a href="#cities" class="mdl-tabs__tab">Cities</a>
                                <a href="#brands" class="mdl-tabs__tab">Brands</a>
                            </div>
                            <!--<a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateTable(2)">Brands</a>-->
                            <div class="mdl-tabs__panel is-active" id="stations">
                                <div class="cfg-table-container mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                                    <table class="cfg-table mdl-data-table mdl-js-data-table mdl-shadow--2dp">
                                        <thead>
                                        <tr class="mdl-navigation__link">
                                            <th class="mdl-data-table__cell--non-numeric">Station name</th>
                                            <th class="mdl-data-table__cell--non-numeric">City</th>
                                            <th class="mdl-data-table__cell--non-numeric">Brand</th>
                                            <th id="stations-table-price">Price<div class="mdl-tooltip" for="stations-table-price">Average price for month</div></th>
                                            <th id="stations-table-days-ranked">Days ranked top<div class="mdl-tooltip" for="stations-table-days-ranked">Number of days in month where station<br/>had the cheapest price in Cyprus</div></th>
                                        </tr>
                                        </thead>
                                        <tbody id="stations-tbody">
                                        <!--populated in javascript...-->
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="mdl-tabs__panel" id="cities">
                                <div class="cfg-table-container mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                                    <table class="cfg-table mdl-data-table mdl-js-data-table mdl-shadow--2dp">
                                        <thead>
                                        <tr class="mdl-navigation__link">
                                            <th class="mdl-data-table__cell--non-numeric">City</th>
                                            <th class="mdl-data-table__header--sorted-ascending" id="cities-table-price">Price<div class="mdl-tooltip" for="cities-table-price">Average price for month</div></th>
                                            <th class="" id="cities-table-difference">Difference<div class="mdl-tooltip" for="cities-table-difference">Price difference from previous city</div></th>
                                        </tr>
                                        </thead>
                                        <tbody id="cities-tbody">
                                        <!--populated in javascript...-->
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="mdl-tabs__panel" id="brands">
                                <div class="cfg-table-container mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                                    <table class="cfg-table mdl-data-table mdl-js-data-table mdl-shadow--2dp">
                                        <thead>
                                        <tr class="mdl-navigation__link">
                                            <th class="mdl-data-table__cell--non-numeric">Brand</th>
                                            <th class="mdl-data-table__header--sorted-ascending" id="brands-table-price">Price<div class="mdl-tooltip" for="brands-table-price">Average price for month</div></th>
                                            <th class="" id="brands-table-difference">Difference<div class="mdl-tooltip" for="brands-table-difference">Price difference from previous brand</div></th>
                                        </tr>
                                        </thead>
                                        <tbody id="brands-tbody">
                                        <!--populated in javascript...-->
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                    </div>

                    <div class="page-content" style="width: 100%; text-align: center;" id="spinner" hidden>
                        <!-- MDL Spinner Component -->
                        <div class="mdl-spinner mdl-js-spinner is-active"></div>
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
                    <li><a href="#" onclick="showContactDialog()">Contact</a></li>
                    <li><a href="privacy">Privacy & Terms</a></li>
                </ul>
                <div>Developed by <a href="http://aspectsense.com" target="_blank">aspectsense.com</a> &copy; <script>document.write(new Date().getFullYear())</script></div>
            </div>
        </footer>

    </main>

</div>

<script language="javascript">
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

    var fuelType = 0;
    function updateFuelType(type) {
        fuelType = type;
        updateUI();
    }

    var selectedPeriod = 0;
    var selectedYear;
    var selectedMonth;

    document.getElementById("periodDropdown").addEventListener('change', function(e) {
        triggerRequest();
    });

    function triggerRequest() {
        selectedPeriod = document.getElementById("periodDropdown").value; // format is 'yyyy-MM'
        selectedYear = selectedPeriod.substr(0,4);
        selectedMonth = selectedPeriod.substr(5,2);
        updateFromServer();
    }

    var fuelTypeNames = [ "unleaded 95", "unleaded 98", "diesel", "heating", "kerosene" ];

    function updateFromServer() {
        document.getElementById("spinner").hidden = false;
        document.getElementById("report").hidden = true;

        // load from server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                if(this.status === 200) {
                    // set the current dam level
                    handleReply(this.responseText);
                    document.getElementById("spinner").hidden = true;
                    document.getElementById("report").hidden = false;
                } else {
                    console.error("Error " + this.status + " while fetching data from server!");
                    alert("Error " + this.status + " while fetching data from server!");
                }
            }
        };
        xhttp.open("GET", "https://cyprusfuelguide.appspot.com/api/month-report?year=" + selectedYear + "&month=" + selectedMonth, true);
        xhttp.send();
    }

    function median(values) { // assumes values are already sorted
        if(values.length ===0) return 0;

        var half = Math.floor(values.length / 2);

        if (values.length % 2)
            return values[half];
        else
            return (values[half - 1] + values[half]) / 2.0;
    }

    function average(values) {
        var sum = 0;
        for(var i = 0; i < values.length; i++) {
            sum += values[i];
        }

        return sum / values.length;
    }

    var monthReport = null;
    function handleReply(json) {
        monthReport = JSON.parse(json);
        updateUI();
    }

    function updateUI() {
        var orderedStations = monthReport.fuelTypeToStationsOrderedByPriceDescending[fuelType + 1];
        var orderedCities = monthReport.fuelTypeToCitiesOrderedByPriceDescending[fuelType + 1];
        var orderedBrands = monthReport.fuelTypeToBrandsOrderedByPriceDescending[fuelType + 1];

        var numOfCities = Object.keys(orderedCities).length;
        var numOfBrands= Object.keys(orderedBrands).length;

        // compute min, max, median, average
        var prices = [];
        orderedStations.forEach(function(station) {
            if(station.price < 20000) { // ignore prices larger than 20 euro
                prices.push(station.price);
            }
        });
        var numOfStations = prices.length;
        var min = Math.min.apply(null, prices);
        var max = Math.max.apply(null, prices);
        var med = median(prices);
        var avg = average(prices);

        if(!selectedPeriod) {
            document.getElementById("periodSubtitle").innerHTML = "Please select a period first.";
        } else {
            document.getElementById("periodSubtitle").innerHTML = "Covering prices of <span class=\"cfg-bold\">" + fuelTypeNames[fuelType].toUpperCase() + "</span> for the month <span class=\"cfg-bold\">" + selectedPeriod + "</span>.";

            document.getElementById("periodBody").innerHTML =
                "<p>During this period <span class=\"cfg-bold\">" + numOfStations + " stations</span> offered " + fuelTypeNames[fuelType].toUpperCase() + " across <span class=\"cfg-bold\">" + numOfBrands + " brands</span>" +
                " in <span class=\"cfg-bold\">" + numOfCities + " cities</span>.</p>" +
                "<p>The fuel prices had a <span class=\"cfg-bold\">min of €&nbsp;" + (min / 1000).toFixed(4) + "</span> and a <span class=\"cfg-bold\">max of €&nbsp;" +
                (max / 1000).toFixed(4) + "</span>. At the same period, the fuel prices has a <span class=\"cfg-bold\">median of €&nbsp;" + (med / 1000).toFixed(4) + "</span> and an <span class=\"cfg-bold\">average of €&nbsp;"
                + (avg / 1000).toFixed(4) + "</span>.</p>";

            // handle station updates
            var cheapestStation = orderedStations[0];
            document.getElementById("cheapestStationBody").innerHTML =
                "<span>The cheapest fuel station for " + selectedPeriod + " is <b>" + cheapestStation.stationName + "</b> in " + cheapestStation.cityName + ". " +
                "At an average of <b>€&nbsp;" + (cheapestStation.price / 1000).toFixed(4) + "</b> it had the lowest <b>" + fuelTypeNames[fuelType].toUpperCase() + "</b> price in Cyprus for <b>" + cheapestStation.numOfDaysRankedTop + " days</b>.</span>";
            document.getElementById("cheapestStationLink").innerText = "Average price € " + (cheapestStation.price / 1000).toFixed(4);

            // handle city updates
            var cheapestCity = orderedCities[0];
            var mostExpensiveCity = orderedCities[numOfCities-1];
            var cheaperThanMostExpensiveCityDifferenceInCents = (mostExpensiveCity.price - cheapestCity.price) / 10;
            document.getElementById("cheapestCityBody").innerHTML =
                "<span>The city with the cheapest " + fuelTypeNames[fuelType].toUpperCase() + " in Cyprus for " + selectedPeriod + " is <b>" + cheapestCity.cityName + "</b> " +
                "with an average price of <b>€&nbsp;" + (cheapestCity.price / 1000).toFixed(4) + "</b>. " +
                "This is " + cheaperThanMostExpensiveCityDifferenceInCents.toFixed(2) + "&nbsp;¢ cheaper than " + mostExpensiveCity.cityName + ", the most expensive city at €&nbsp;"+ (mostExpensiveCity.price / 1000).toFixed(4) + ".</span>";
            document.getElementById("cheapestCityLink").innerText = "Average price € " + (cheapestCity.price / 1000).toFixed(4);

            // handle brand updates
            var cheapestBrand = orderedBrands[0];
            var mostExpensiveBrand = orderedBrands[numOfBrands-1];
            var cheaperThanMostExpensiveBrandDifferenceInCents = (mostExpensiveBrand.price - cheapestBrand.price) / 10;
            document.getElementById("cheapestBrandBody").innerHTML =
                "<span>The cheapest brand in Cyprus for " + selectedPeriod + " is <b>" + cheapestBrand.brand + "</b> " +
                "with an average price for <b>" + fuelTypeNames[fuelType] + "</b> at <b>€&nbsp;" + (cheapestBrand.price / 1000).toFixed(4) + "</b>. " +
                "This is " + cheaperThanMostExpensiveBrandDifferenceInCents.toFixed(1) + "&nbsp;¢ cheaper than the most expensive brand at €&nbsp;"+ (mostExpensiveBrand.price / 1000).toFixed(4) + ".</span>";
            document.getElementById("cheapestBrandLink").innerText = "Average price € " + (cheapestBrand.price / 1000).toFixed(4);

            // handle individual cities updates
            var cheapestStationInNicosia = null;
            var cheapestStationInLimassol = null;
            var cheapestStationInFamagusta = null;
            var cheapestStationInLarnaca = null;
            var cheapestStationInPaphos = null;
            orderedStations.forEach(function(station) {
                switch(station.cityName) {
                    case "Nicosia":
                        if(cheapestStationInNicosia == null || cheapestStationInNicosia.price > station.price) cheapestStationInNicosia = station;
                        break;
                    case "Limassol":
                        if(cheapestStationInLimassol == null || cheapestStationInLimassol.price > station.price) cheapestStationInLimassol = station;
                        break;
                    case "Famagusta":
                        if(cheapestStationInFamagusta == null || cheapestStationInFamagusta.price > station.price) cheapestStationInFamagusta = station;
                        break;
                    case "Larnaca":
                        if(cheapestStationInLarnaca == null || cheapestStationInLarnaca.price > station.price) cheapestStationInLarnaca = station;
                        break;
                    case "Paphos":
                        if(cheapestStationInPaphos == null || cheapestStationInPaphos.price > station.price) cheapestStationInPaphos = station;
                        break;
                }
            });
            document.getElementById("nicosiaCardBody").innerHTML    = "The cheapest fuel station in Nicosia is <span class=\"cfg-bold\">" + cheapestStationInNicosia.stationName + "</span> with an average price of <span class=\"cfg-bold\">€&nbsp;" + (cheapestStationInNicosia.price / 1000).toFixed(4) + "</span>.";
            document.getElementById("limassolCardBody").innerHTML   = "The cheapest fuel station in Limassol is <span class=\"cfg-bold\">" + cheapestStationInLimassol.stationName + "</span> with an average price of <span class=\"cfg-bold\">€&nbsp;" + (cheapestStationInLimassol.price / 1000).toFixed(4) + "</span>.";
            document.getElementById("famagustaCardBody").innerHTML  = "The cheapest fuel station in Famagusta is <span class=\"cfg-bold\">" + cheapestStationInFamagusta.stationName + "</span> with an average price of <span class=\"cfg-bold\">€&nbsp;" + (cheapestStationInFamagusta.price / 1000).toFixed(4) + "</span>.";
            document.getElementById("larnacaCardBody").innerHTML    = "The cheapest fuel station in Larnaca is <span class=\"cfg-bold\">" + cheapestStationInLarnaca.stationName + "</span> with an average price of <span class=\"cfg-bold\">€&nbsp;" + (cheapestStationInLarnaca.price / 1000).toFixed(4) + "</span>.";
            document.getElementById("paphosCardBody").innerHTML     = "The cheapest fuel station in Paphos is <span class=\"cfg-bold\">" + cheapestStationInPaphos.stationName + "</span> with an average price of <span class=\"cfg-bold\">€&nbsp;" + (cheapestStationInPaphos.price / 1000).toFixed(4) + "</span>.";

            document.getElementById("cheapestInNicosiaLink").href = "http://cyprusfuelguide.com/station?id=" + cheapestStationInNicosia.stationCode;
            document.getElementById("cheapestInLimassolLink").href = "http://cyprusfuelguide.com/station?id=" + cheapestStationInLimassol.stationCode;
            document.getElementById("cheapestInFamagustaLink").href = "http://cyprusfuelguide.com/station?id=" + cheapestStationInFamagusta.stationCode;
            document.getElementById("cheapestInLarnacaLink").href = "http://cyprusfuelguide.com/station?id=" + cheapestStationInLarnaca.stationCode;
            document.getElementById("cheapestInPaphosLink").href = "http://cyprusfuelguide.com/station?id=" + cheapestStationInPaphos.stationCode;

            // handle stations table
            var stationsListHtml = "";
            orderedStations.forEach(function(station) {
                if(station.price < 20000) { // ignore prices larger than 20 euro
                    stationsListHtml += "<tr class=\"mdl-navigation__link\" onclick=\"window.location='http://cyprusfuelguide.com/station?id=" + station.stationCode + "';\">" +
                        "<td class=\"mdl-data-table__cell--non-numeric\">" + station.stationName + "</td>" +
                        "<td class=\"mdl-data-table__cell--non-numeric\">" + station.cityName + "</td>" +
                        "<td class=\"mdl-data-table__cell--non-numeric\">" + station.stationBrand + "</td>" +
                        "<td>€&nbsp;" + (station.price / 1000).toFixed(4) + "</td>" +
                        "<td>" + station.numOfDaysRankedTop + "</td></tr>";
                }
            });
            document.getElementById("stations-tbody").innerHTML = stationsListHtml;

            // handle cities table
            var citiesListHtml = "";
            var topCity = null;
            orderedCities.forEach(function(city) {
                var differenceInCents = topCity == null ? 0 : (city.price - topCity.price) / 10;
                citiesListHtml += "<tr class=\"mdl-navigation__link\" onclick=\"window.location='http://cyprusfuelguide.com/city?nameEn=" + city.cityName + "';\">" +
                    "<td class=\"mdl-data-table__cell--non-numeric\">" + city.cityName + "</td>" +
                    "<td>€&nbsp;" + (city.price / 1000).toFixed(4) + "</td>" +
                    "<td>" + (topCity == null ? "<span class='cfg-green'>&dash;</span>" : "<span class='cfg-red'>+" + differenceInCents.toFixed(2) + "&nbsp¢</span>") + "</td>" +
                    "</tr>";
                if(topCity == null) topCity = city;
            });
            document.getElementById("cities-tbody").innerHTML = citiesListHtml;

            // handle brands table
            var brandsListHtml = "";
            var topBrand = null;
            orderedBrands.forEach(function(brand) {
                var differenceInCents = topBrand == null ? 0 : (brand.price - topBrand.price) / 10;
                brandsListHtml += "<tr class=\"mdl-navigation__link\" onclick=\"window.location='http://cyprusfuelguide.com/brand?brandName=" + brand.brand + "';\">" +
                    "<td class=\"mdl-data-table__cell--non-numeric\">" + brand.brand + "</td>" +
                    "<td>€&nbsp;" + (brand.price / 1000).toFixed(4) + "</td>" +
                    "<td>" + (topBrand == null ? "<span class='cfg-green'>&dash;</span>" : "<span class='cfg-red'>+" + differenceInCents.toFixed(2) + "&nbsp¢</span>") + "</td>" +
                    "</tr>";
                if(topBrand == null) topBrand = brand;
            });
            document.getElementById("brands-tbody").innerHTML = brandsListHtml;
        }
    }

    function setupPeriodDropdown() {
        var periodsHtml = "";
        var currentTime = new Date();
        var currentYear = currentTime.getFullYear();
        var currentMonth = currentTime.getMonth() + 1;

        var monthNames = [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ];

        var startingYear = 2017; // min year is 2017
        var startingMonth = 6; // min month is June
        var firstOption = true;
        while(currentYear > startingYear || (currentYear >= startingYear && currentMonth > startingMonth)) {
            // start by decreasing by 1 month
            currentMonth--;
            if(currentMonth === 0) {
                currentMonth = 12;
                currentYear--;
            }

            var period = currentYear + "-" + (currentMonth < 10 ? "0" : "") + currentMonth;
            periodsHtml += "<option class=\"mdl-card__actions\" " + (firstOption ? "selected=\"selected\"" : "") + " value=\"" + period + "\">" + currentYear + " " + monthNames[currentMonth-1] + "</option>";

            firstOption = false;
        }

        document.getElementById("periodDropdown").innerHTML = periodsHtml;
    }

    setupPeriodDropdown();

    // initial call to server
    triggerRequest();

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