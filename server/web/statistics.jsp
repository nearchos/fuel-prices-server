<%@ page import="com.aspectsense.fuel.server.data.FuelType" %>
<%@ page import="com.aspectsense.fuel.server.api.ApiStatisticsServlet" %>
<%@ page import="com.google.appengine.labs.repackaged.org.json.JSONException" %>
<%@ page import="java.io.IOException" %><%--
  Created by Nearchos Paspallis
  Date: 22-Jun-17
  Time: 5:25 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
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

    <%--support for polyfill modal dialogs (https://github.com/GoogleChrome/dialog-polyfill)--%>
    <link rel="stylesheet" type="text/css" href="css/dialog-polyfill.css" />
    <script src="js/dialog-polyfill.js"></script>

    <script type="javascript">
        <%
        String JSON;
        try {
            JSON = ApiStatisticsServlet.getStatisticsMessageAsJSON(365);
//            JSON = ApiStatisticsServlet.getStatisticsMessageByCityAsJSON(365, "ΛΕΜΕΣΟΣ");
//            JSON = ApiStatisticsServlet.getStatisticsMessageByDistrictAsJSON(365, "Αγία Βαρβάρα");
        } catch (IOException ioe) {
            JSON = "{ \"status\": \"error: \"" + ioe.getMessage() + " }";
        }
        %>
        var STATISTICS_AS_JSON = '<%=JSON%>';
    </script>

</head>

<body onload="init();">

<dialog class="mdl-dialog" id="dialog-about">
    <span><img src="http://cyprusfuelguide.com/images/favicon.png" title="Cyprus Fuel Guide"/>&nbsp;<b style="font-size: medium">Cyprus Fuel Guide</b></span>
    <div class="mdl-dialog__content">
        <p>
            The Cyprus Fuel Guide provides an intuitive way to compare fuel prices in your area and find the best deal for you.
        </p>
        <p>
            Website & App developed by <a href="http://aspectsense.com" target="_blank">aspectsense.com</a>.
        </p>
        <p>
            The research and implementation in the Statistics page was funded by <a href="http://www.uclancyprus.ac.cy" target="_blank">UCLan Cyprus</a>
            and by <a href="http://inspirecenter.org/" target="_blank">Inspire Center</a>.
        </p>
        <p>
            <img src="http://cyprusfuelguide.com/images/uclan-cy.png" title="UCLan Cyprus"/>
            &nbsp;
            <img src="http://cyprusfuelguide.com/images/inspire-center.png" title="Inspire Center"/>
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

            <a href="#all-fuel-types" class="mdl-layout__tab is-active" onclick="updateFuelType(1)">unleaded 95</a>

            <a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateFuelType(2)">unleaded 98</a>

            <a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateFuelType(3)">diesel</a>

            <a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateFuelType(4)">heating</a>

            <a href="#all-fuel-types" class="mdl-layout__tab" onclick="updateFuelType(5)">kerosene</a>

        </div>
    </header>

    <main class="mdl-layout__content mdl-color--grey-100">

        <div class="mdl-grid demo-content">

            <div class="demo-updates mdl-shadow--2dp mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">
                <div class="mdl-card__title mdl-color--brown-300">
                    <div class="options__item">
                        <label class="option-title" for="city-dropdown">City</label>
                        <select class="option-input option-dropdown" id="city-dropdown">
                            <option selected="selected" value="0">All cities</option>
                            <option value="ΛΕΥΚΩΣΙΑ">Nicosia</option>
                            <option value="ΛΕΜΕΣΟΣ">Limassol</option>
                            <option value="ΛΑΡΝΑΚΑ">Larnaca</option>
                            <option value="ΠΑΦΟΣ">Pafos</option>
                            <option value="ΑΜΜΟΧΩΣΤΟΣ">Famagusta</option>
                        </select>


                        <label class="option-title" for="district-dropdown">District</label>
                        <select class="option-input option-dropdown" id="district-dropdown">
                            <option selected="selected" value="0">All districts</option>
                        </select>
                    </div>

                </div>
            </div>

            <section class="mdl-layout__tab-panel " id="all-fuel-types">
            </section>

            <div class="demo-updates mdl-shadow--2dp mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--12-col-desktop">

                <div class="mdl-card__title mdl-color--white">
                    <div class="page-content">
                        <h2 class="mdl-card__title-text">Visualizations go here...</h2>
                    </div>
                </div>

                <div class="mdl-card__title mdl-color--white">
                    <div class="page-content">
                        <ul>
                            <li><b>Fuel type</b>: <span id="fuel-type-name">hello world!</span></li>
                            <li><b>City name</b>: <span id="city-name">All cities</span></li>
                            <li><b>District name</b>: <span id="district-name">All districts</span></li>
                        </ul>

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

    var nicosiaOptions = ["Αγία Βαρβάρα", "Κοκκινοτριμιθιά", "Ακάκι", "Αγλαντζιά", "Κακοπετριά", "Πέρα Ορεινής", "Στρόβολος", "Κάτω Πύργος", "Παλλουριώτισσα", "Λακατάμεια", "Δευτερά", "Περιστερώνα", "Τσέρι", "Δάλι", "Λατσιά", "Αστρομερίτης", "Γαλάτα", "Λευκωσία", "Ακρόπολη", "Δασούπολη", "Ευρύχου", "Εγκωμη", "Λυθροδόντας", "Πέρα Χωριό Νήσου", "Αγιος Δομέτιος", "Αρεδιού", "Πεδουλάς", "Καϊμακλί", "Λύμπια", "Κλήρου", "Παλιομέτοχο", "Γέρι"];
    var limassolOptions = ["Κολόσσι", "Παραμύθα", "Σαϊτάς", "Μονή", "Αγιος Αθανάσιος", "Μέσα Γειτονιά", "Γερμασόγεια", "Τραχώνι", "Παλώδια", "Βάση Επισκοπής", "Παρεκκλησιά", "Κάτω Πολεμίδια", "Τριμίκλινη", "Πάνω Κυβίδες", "Πεντάκωμο", "Τουριστική Περ. Αγίου Τύχωνα", "Ύψωνας", "Κυπερούντα", "Πελένδρι", "Επισκοπή", "Λεμεσός", "Πισσούρι", "Πάνω Πλάτρες"];
    var larnacaOptions = ["Αγγλισίδες", "Κοφίνου", "Δρόμος Λάρνακας - Δεκέλειας", "Αβδελλερό", "Περιφερειακός Δρόμος Ξυλοτύμπου", "Αραδίππου", "Αλεθρικό", "Λεύκαρα", "Δρόμος Καλαβασού - Ζυγίου", "Δρομολαξιά", "Ορμήδεια", "Ξυλοφάγου", "Χοιροκιτία", "Αθιένου", "Καλό Χωριό Λάρνακας", "Κόρνος", "Δεκέλεια", "Λειβάδια", "Λάρνακα", "Μαζωτός", "Μοσφιλωτή", "Πύλα", "Ορόκλινη", "Κίτι", "Καλαβασός"];
    var paphosOptions = ["Χλώρακα", "Στρουμπί", "Τάφοι των Βασιλέων", "Πάφος", "Τίμη", "Πόλης Χρυσοχούς", "Μανδριά", "Γεροσκήπου", "Τρεμιθούσα", "Νικόκλεια", "Εμπα", "Μεσόγη", "Αργάκα", "Πέγεια"];
    var famagustaOptions = ["Βρυσούλες", "Φρέναρος", "Λιοπέτρι", "Πρωταράς", "Αγία Νάπα", "Δασάκι της Άχνας", "Σωτήρα", "Αυγόρου", "Δερύνεια", "Παραλίμνι"];

    function init() {

        updateFuelType(1);

        var districtDropdown = document.getElementById("district-dropdown");
        districtDropdown.disabled = true;

        document.getElementById('city-dropdown').addEventListener('change', function(e) {
            districtDropdown.options.length = 0;
            createOption(districtDropdown, "All districts", "0");
            switch(this.value) {
                case "0":
                    break;
                case "ΛΕΥΚΩΣΙΑ":
                    for(var i = 0; i < nicosiaOptions.length; i++) {
                        createOption(districtDropdown, nicosiaOptions[i], nicosiaOptions[i]);
                    }
                    break;
                case "ΛΕΜΕΣΟΣ":
                    for(var i = 0; i < limassolOptions.length; i++) {
                        createOption(districtDropdown, limassolOptions[i], limassolOptions[i]);
                    }
                    break;
                case "ΛΑΡΝΑΚΑ":
                    for(var i = 0; i < larnacaOptions.length; i++) {
                        createOption(districtDropdown, larnacaOptions[i], larnacaOptions[i]);
                    }
                    break;
                case "ΠΑΦΟΣ":
                    for(var i = 0; i < paphosOptions.length; i++) {
                        createOption(districtDropdown, paphosOptions[i], paphosOptions[i]);
                    }
                    break;
                case "ΑΜΜΟΧΩΣΤΟΣ":
                    for(var i = 0; i < famagustaOptions.length; i++) {
                        createOption(districtDropdown, famagustaOptions[i], famagustaOptions[i]);
                    }
                    break;
            }

            document.getElementById("district-dropdown").disabled = this.value === "0"; //enable when value of city select is changed

            document.getElementById("city-name").innerHTML = this.value;
        });

        document.getElementById('district-dropdown').addEventListener('change', function(e) {
            document.getElementById("district-name").innerHTML = this.value;
        });
    }

    function createOption(ddl, text, value) {
        var opt = document.createElement('option');
        opt.text = text;
        opt.value = value;
        ddl.options.add(opt);
    }

    function updateFuelType(fuelTypeCode) {
        if(fuelTypeCode === undefined) {
            fuelTypeCode = 1;
        }
        var fuelTypes = ["1", "2", "3", "4", "5"];
        var fuelNames = ["unleaded 95", "unleaded 98", "diesel", "kerosene", "heating"];
        var arrayLength = fuelTypes.length;
        var i;
        for (i = 0; i < arrayLength; i++) {
            if(fuelTypes[i] == fuelTypeCode) {
                document.getElementById("fuel-type-name").innerHTML = fuelNames[i];
            }
        }
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
