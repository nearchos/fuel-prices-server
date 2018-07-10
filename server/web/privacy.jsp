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
Date: 15/2/2016
Time: 10:19
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="Cyprus Fuel Guide helps you find the best value fuel near you">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
    <title>Cyprus Fuel Guide - Privacy & Terms of Use</title>

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
                        <div class="cfg-prices-header">
                            Privacy & Terms of Use
                        </div>

                        <div>
                            <p class="cfg-privacy-text">
                                Cyprus Fuel Guide takes your privacy very seriously!
                            </p>

                            <p class="cfg-privacy-text">
                                The only permissions required by the Cyprus Fuel Guide app is internet access, which is needed simply to fetch
                                updates regarding fuel stations and prices, and location, which is needed to show you nearby fuel stations.
                                Please note that approximate and anonymous information regarding the device's location at the time of nearby
                                searches might be logged for research purposes.
                            </p>

                            <p class="cfg-privacy-text">
                                We also use <a href="http://www.google.com/analytics/learn/privacy.html">Google Analytics</a> to trace app
                                usage, and <a href="http://www.google.com/policies/privacy/partners/">AdMob</a> to serve ads.
                            </p>

                            <p class="cfg-privacy-text">
                                This app is made available to you in the hope that it provides helpful information and services. The data
                                regarding the fuel prices are collected from the <a href="http://http://www.mcit.gov.cy/" target="_blank">
                                Ministry of Energy, Commerce, Industry and Tourism</a> as uploaded and updated daily from the petrol stations.
                                The app is not responsible for the correctness, validity or non-updating of the information that might arise
                                from incomplete or non-updated information.
                            </p>

                            <p class="cfg-privacy-text">
                                Additional data used in the statistics/visualization pages are
                                the crude oil prices from <a href="https://www.eia.gov" target="_blank">U.S. Energy Information Administration</a>'s site,
                                and EUR/USD rates from <a href="http://fixer.io" target="_blank">fixer.io</a>.
                            </p>

                            <p class="cfg-privacy-text">
                                From a technical perspective, this service is built with Java on Google App Engine, along with a healthy amount
                                of JavaScript. We periodically fetch the updated prices from the Ministry's server and update a Google
                                Datastore-based database. Our apps
                                (<a href="https://play.google.com/store/apps/details?id=com.aspectsense.fuelguidecy">Android</a> and
                                <a href="http://cyprusfuelguide.com/">Web</a>) use that data to provide the latest prices to the users.
                            </p>

                            <p class="cfg-privacy-text">
                                The copying or reproduction of this website or its content is prohibited.
                            </p>
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
                    <%--<li><a href="#">Privacy</a></li>--%>
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

    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-319978-22', 'auto');
    ga('send', 'pageview');

</script>
</body>
</html>