package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.DailySummary;
import com.aspectsense.fuel.server.model.MonthReport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class TestMonthReport {

    private static final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    public static void main(String[] args) throws IOException {
        final Vector<DailySummary> dailySummariesJson = new Vector<>();
        dailySummariesJson.add(new DailySummary("", JSON1, "2018-06-01"));
        dailySummariesJson.add(new DailySummary("", JSON2, "2018-06-02"));
        final String stationsJson = new String(Files.readAllBytes(Paths.get("stations.json")), StandardCharsets.UTF_8);
        final MonthReport report = MonthReportServlet.produceMonthReport(dailySummariesJson, stationsJson);
        System.out.println("report: \n" + gson.toJson(report));
    }

    public static final String JSON1 = "{\"crudeOilInUsd\":0.0,\"eurUsd\":1.166583,\"eurGbp\":0.874168,\"stations\":{\"EK016\":[1339,1389,1349,883,872],\"EK015\":[1339,1379,1349,880,871],\"EK014\":[1339,1390,1349,883,872],\"EK013\":[1349,1399,1359,811,872],\"EK012\":[1316,1387,1346,881,868],\"EK011\":[1319,1389,1334,881,872],\"PE039\":[1338,1380,1348,0,0],\"EK010\":[1313,1389,1349,883,872],\"PE038\":[1338,1380,1348,879,869],\"PE040\":[1337,1387,1348,880,872],\"LU021\":[1312,1345,1322,904,0],\"LU022\":[1318,1378,1335,0,868],\"LU020\":[1318,1378,1335,879,869],\"LU029\":[1339,1387,1350,904,893],\"PE048\":[1333,1385,1346,880,872],\"PE047\":[1328,1387,1345,878,869],\"PE046\":[1339,1388,1349,880,872],\"LU027\":[1337,1382,1348,928,872],\"LU028\":[1338,1398,1348,879,853],\"PE045\":[1324,1365,1329,854,861],\"LU025\":[1337,1382,1348,928,872],\"PE044\":[1348,1388,1353,0,894],\"LU026\":[1339,1345,1342,938,870],\"PE043\":[1335,1360,1344,879,868],\"EK019\":[1313,1389,1349,880,870],\"LU023\":[1328,1388,1345,878,0],\"EK018\":[1339,1393,1349,880,871],\"PE042\":[1329,1375,1339,879,869],\"PE041\":[1329,1375,1339,879,869],\"LU024\":[1338,1384,1348,955,0],\"EK005\":[1337,1389,1349,880,872],\"EK004\":[1339,1389,1349,869,849],\"EK003\":[1313,1373,1333,868,848],\"EK002\":[1337,1389,1349,880,872],\"EK001\":[1337,1389,1349,880,872],\"PE049\":[1342,1387,1349,899,898],\"FG001\":[1303,0,1313,848,848],\"PE051\":[1339,1399,1349,925,885],\"PE050\":[1339,1388,1348,880,872],\"LU030\":[1337,1386,1347,928,872],\"LU031\":[1333,0,1338,0,0],\"PE059\":[1339,1387,1349,880,872],\"PE058\":[1339,1387,1351,890,878],\"PE056\":[1339,1399,1349,895,872],\"EK009\":[1313,1399,1347,0,0],\"PE055\":[1339,1389,1349,879,869],\"PE054\":[1339,1387,1349,880,872],\"EK008\":[1313,1388,1348,883,871],\"EK007\":[1313,1383,1347,898,895],\"PE053\":[1339,1402,1347,901,0],\"PE052\":[1332,1380,1342,880,872],\"EK006\":[1313,1386,1348,0,869],\"LU009\":[1338,1398,1348,878,868],\"PE062\":[1338,1386,1353,909,871],\"PE061\":[1339,1387,1349,880,872],\"PE060\":[1319,1379,1339,880,875],\"LU007\":[1313,1365,1329,0,0],\"LU008\":[1338,1398,1348,878,869],\"PE069\":[1338,1386,1353,889,879],\"PE068\":[1369,1418,1379,849,849],\"LU005\":[1313,1389,1335,925,0],\"LU006\":[1313,1385,1333,925,865],\"PE067\":[1338,1386,1349,0,872],\"LU003\":[1329,1388,1348,885,872],\"PE066\":[1339,1395,1349,967,968],\"PE065\":[1339,1387,1349,880,872],\"LU004\":[1337,1383,1339,928,0],\"LU001\":[1336,1388,1348,854,869],\"PE064\":[1337,1385,1347,883,875],\"LU002\":[1331,1324,1339,854,0],\"PE063\":[1339,1389,1349,0,865],\"LU010\":[1328,1388,1338,885,867],\"ES066\":[1329,1369,1339,874,865],\"PE073\":[1329,1386,1345,879,871],\"LU011\":[1339,1399,1348,880,868],\"PE071\":[1339,1387,1349,0,0],\"ES064\":[1329,1375,1340,879,885],\"PE070\":[1336,1387,1348,880,872],\"ES065\":[1336,1386,1345,879,851],\"LU018\":[1336,1375,1345,890,869],\"LU019\":[1319,1345,1339,928,872],\"PE079\":[1339,1387,1349,904,896],\"LU016\":[1315,1365,1325,817,0],\"PE078\":[1339,1387,1349,904,898],\"LU017\":[1337,1384,1348,932,876],\"PE077\":[1334,1339,1346,862,850],\"LU014\":[1328,1383,1341,0,0],\"PE076\":[1349,1397,1355,889,875],\"LU015\":[1337,1345,1348,928,872],\"PE075\":[1348,1396,1357,889,879],\"LU012\":[1328,1383,1341,885,868],\"PE074\":[1337,1387,1347,878,852],\"LU013\":[1328,1383,1341,878,869],\"ST009\":[1328,1345,1346,878,849],\"ST001\":[1328,1345,1346,878,849],\"ST002\":[1328,1345,1346,878,849],\"ST003\":[1328,1345,1346,878,849],\"ST004\":[1328,1345,1346,878,849],\"ES062\":[1337,1388,1347,879,851],\"ES063\":[1328,1345,1346,878,849],\"ST006\":[1334,1340,1339,876,851],\"ST007\":[1336,1340,1347,0,0],\"ES060\":[1338,1388,1348,0,0],\"ST008\":[1332,1340,1339,880,851],\"ES061\":[1308,1368,1348,887,852],\"ES055\":[1335,1385,1352,865,865],\"ES056\":[1338,1395,1348,895,880],\"ES053\":[1347,1396,1356,889,879],\"ES054\":[1346,1399,1349,893,879],\"ES059\":[1338,1379,1348,882,852],\"ES057\":[1335,1389,1345,897,0],\"ES058\":[1323,1369,1348,882,859],\"PE004\":[1339,1387,1348,879,871],\"PE003\":[1336,1387,1352,880,872],\"PE002\":[1336,1388,1348,877,872],\"PE001\":[1337,1386,1351,879,871],\"PE008\":[1313,1367,1333,879,859],\"PE007\":[1313,1368,1333,880,872],\"PE006\":[1313,1368,1333,868,852],\"PE005\":[1313,1387,1349,880,872],\"ES051\":[1343,1393,1353,886,856],\"ES052\":[1337,1390,1348,880,865],\"ES050\":[1337,1387,1348,882,0],\"ES044\":[1335,1386,1347,882,0],\"ES045\":[1338,1389,1348,881,851],\"ES042\":[1339,1389,1349,888,855],\"ES043\":[1329,1370,1334,859,866],\"ES048\":[1339,1389,1349,882,852],\"ES049\":[1333,1389,1338,886,0],\"ES046\":[1339,1389,1348,881,851],\"ES047\":[1339,1389,1348,881,851],\"PE015\":[1349,1399,1359,889,872],\"PE014\":[1339,1387,1349,880,872],\"PE013\":[1339,1387,1349,880,872],\"PE012\":[1339,1387,1349,880,872],\"PE011\":[1313,1368,1333,880,872],\"PE010\":[1314,1387,1334,880,872],\"PE019\":[1339,1387,1349,885,872],\"PE018\":[1339,1387,1349,880,872],\"PE017\":[1338,1387,1349,880,870],\"PE016\":[1339,1379,1349,885,871],\"ES040\":[1336,1386,1345,879,855],\"ES041\":[1339,1389,1349,0,0],\"ES033\":[1329,1389,1344,879,849],\"ES034\":[1339,1389,1349,880,852],\"ES031\":[1329,1385,1345,878,849],\"ES032\":[1339,1389,1349,883,870],\"ES037\":[1337,1388,1347,880,857],\"ES038\":[1337,1361,1346,880,871],\"ES035\":[1343,1393,1355,886,856],\"ES036\":[1339,1389,1349,0,0],\"PE026\":[1349,1397,1358,891,877],\"PE025\":[1338,1381,1349,880,872],\"PE024\":[1338,1381,1349,880,872],\"ES039\":[1339,1389,1349,885,852],\"PE023\":[1339,1387,1349,880,872],\"PE022\":[1321,1387,1349,880,872],\"PE021\":[1339,1387,1349,880,872],\"PE020\":[1339,1393,1349,880,870],\"IS003\":[1339,1381,1349,948,0],\"PE029\":[1339,1387,1349,880,872],\"PE028\":[1339,1389,1352,881,873],\"PE027\":[1327,1385,1338,879,871],\"IS001\":[1339,1388,1349,882,852],\"ES030\":[1352,1402,1362,895,865],\"ES022\":[1339,1389,1349,882,869],\"ES023\":[1339,1389,1349,889,859],\"ES020\":[1339,1389,1349,889,859],\"ES021\":[1329,1385,1339,882,856],\"ES026\":[1319,1369,1319,899,899],\"ES027\":[1335,1387,1349,883,852],\"ST010\":[1328,1345,1346,878,849],\"ES024\":[1334,1384,1343,885,852],\"ES025\":[1339,1389,1349,882,852],\"PE037\":[1325,1369,1329,879,869],\"PE036\":[1337,1386,1346,878,865],\"ES028\":[1329,1389,1349,882,869],\"PE035\":[1338,1387,1347,879,869],\"ES029\":[1325,1379,1337,889,869],\"PE034\":[1335,1386,1345,880,872],\"PE033\":[1329,1375,1335,880,870],\"PE032\":[1339,1389,1357,896,894],\"PE031\":[1339,1389,1348,880,874],\"EK095\":[1336,1390,1347,882,871],\"EK094\":[1338,1390,1348,880,871],\"EK093\":[1339,1390,1349,884,894],\"EK092\":[1335,0,1345,889,0],\"EK091\":[1337,1389,1347,882,872],\"EK090\":[1338,1389,1348,881,870],\"ES011\":[1329,1399,1348,886,871],\"ES012\":[1339,1389,1348,882,852],\"TO001\":[1313,1378,1333,877,852],\"ES010\":[1329,1389,1349,879,852],\"TO002\":[1338,1389,1348,883,869],\"ES015\":[1313,1373,1333,838,858],\"ES016\":[1336,1386,1347,879,852],\"ES013\":[1335,1389,1349,882,849],\"ES014\":[1339,1389,1349,882,869],\"ES019\":[1328,1389,1339,882,852],\"ES017\":[1328,1345,1346,875,849],\"ES018\":[1313,1373,1333,882,852],\"TO003\":[1347,0,1345,928,875],\"TO004\":[1337,0,1347,879,0],\"TO005\":[1347,0,1357,890,875],\"EK089\":[1339,1391,1349,883,872],\"EK088\":[1339,1389,1349,880,871],\"EK087\":[1339,1389,1347,883,871],\"EK086\":[1339,1389,1348,880,871],\"EK085\":[1339,1390,1339,880,851],\"EK084\":[1340,1390,1350,880,872],\"EK083\":[1339,1389,1349,881,871],\"EK082\":[1339,1389,1349,881,871],\"EK081\":[1338,1378,1349,908,899],\"EK080\":[1339,1389,1352,885,880],\"ES001\":[1313,1376,1347,879,867],\"ES004\":[1338,1388,1349,886,852],\"ES005\":[1339,1388,1347,880,852],\"ES002\":[1339,1389,1349,886,852],\"ES003\":[1339,1389,1349,886,852],\"ES008\":[1318,1389,1347,882,852],\"ES009\":[1339,1389,1349,880,872],\"ES006\":[1313,1368,1333,877,852],\"ES007\":[1339,1389,1349,882,852],\"EK079\":[1336,1389,1349,880,872],\"EK078\":[1338,1390,1349,881,871],\"EK077\":[1339,1389,1349,882,875],\"EK076\":[1347,1397,1358,888,880],\"EK075\":[1339,1389,1352,885,880],\"EK074\":[1347,1390,1358,889,879],\"EK073\":[1337,1389,1349,881,869],\"EK072\":[1339,1389,1350,895,880],\"EK071\":[1339,1387,1348,880,870],\"EK070\":[1338,1390,1349,881,872],\"EK069\":[1337,1389,1347,882,871],\"EK068\":[1338,1386,1353,920,870],\"EK067\":[1327,1387,1349,880,870],\"EK066\":[1336,1377,1349,880,872],\"EK065\":[1333,1383,1344,876,859],\"EK064\":[1339,1389,1349,880,895],\"EK063\":[1333,1385,1344,850,880],\"EK062\":[1315,1338,1338,865,848],\"EK061\":[1322,1364,1328,886,870],\"EK060\":[1339,1389,1350,881,871],\"EK059\":[1336,1389,1348,883,871],\"EK058\":[1339,1388,1350,933,871],\"EK057\":[1339,1389,1348,880,871],\"EK056\":[1336,1389,1348,879,872],\"EK055\":[1339,1389,1349,883,870],\"EK053\":[1339,1389,1349,899,879],\"EK052\":[1328,1385,1339,875,869],\"EK051\":[1339,1389,1349,0,0],\"EK050\":[1315,1389,1349,0,0],\"PE084\":[1339,1387,1349,880,872],\"PE083\":[1338,1386,1349,882,873],\"PE082\":[1339,1387,1349,880,872],\"PE081\":[1337,1386,1347,889,872],\"PE080\":[1339,1387,1349,880,872],\"PE089\":[1327,1374,1332,887,878],\"PE087\":[1329,1377,1339,870,862],\"PE086\":[1339,1387,1349,880,872],\"PE085\":[1318,1386,1348,880,872],\"EK049\":[1364,1414,1375,887,879],\"EK048\":[1339,1389,1349,880,869],\"EK047\":[1335,1389,1349,880,869],\"EK046\":[1349,1389,1359,905,894],\"EK045\":[1329,1389,1347,881,871],\"EK044\":[1314,1399,1359,885,870],\"EK043\":[1325,1375,1329,879,895],\"EK042\":[1321,1417,1378,885,889],\"EK041\":[1329,1388,1345,879,870],\"EK040\":[1342,1393,1352,899,872],\"EK038\":[1315,1379,1348,881,871],\"EK037\":[1339,1389,1349,886,869],\"EK036\":[1329,0,1339,950,0],\"EK035\":[1313,1389,1349,880,873],\"EK034\":[1339,1389,1349,881,870],\"EK033\":[1313,1388,1349,882,871],\"EK032\":[1339,1389,1349,886,869],\"EK031\":[1337,1391,1347,875,870],\"EK030\":[1337,1390,1349,883,872],\"AG013\":[1339,1387,1349,0,872],\"AG010\":[1337,1365,1349,980,880],\"AG011\":[1348,1396,1357,889,879],\"EK039\":[1315,1390,1348,881,872],\"AG012\":[1339,1389,1349,880,872],\"EK027\":[1329,1389,1346,880,872],\"EK026\":[1329,1389,1349,883,871],\"EK025\":[1329,1389,1350,880,872],\"EK024\":[1327,1389,1347,880,871],\"AG006\":[1337,1386,1347,878,869],\"EK023\":[1337,1387,1347,880,870],\"AG007\":[1329,1374,1339,0,0],\"EK022\":[1328,1379,1349,880,870],\"AG008\":[1338,1386,1348,879,870],\"EK021\":[1338,1389,1349,883,871],\"EK020\":[1339,1389,1349,884,872],\"AG009\":[1339,1385,1349,875,867],\"AG002\":[1339,1387,1349,880,872],\"AG003\":[1339,1387,1349,880,872],\"AG004\":[1349,1399,1359,899,0],\"AG005\":[1319,1379,1339,875,865],\"EK029\":[1329,1389,1349,881,871],\"AG001\":[1313,1368,1333,868,848],\"EK028\":[1339,1389,1349,884,873]}}";
    public static final String JSON2 = "{\"crudeOilInUsd\":0.0,\"eurUsd\":1.166583,\"eurGbp\":0.874168,\"stations\":{\"EK016\":[1339,1389,1349,883,872],\"EK015\":[1339,1379,1349,880,871],\"EK014\":[1339,1390,1349,883,872],\"EK013\":[1349,1399,1359,811,872],\"EK012\":[1316,1387,1346,881,868],\"EK011\":[1319,1389,1334,881,872],\"PE039\":[1338,1380,1348,0,0],\"EK010\":[1313,1389,1349,883,872],\"PE038\":[1338,1380,1348,879,869],\"PE040\":[1337,1387,1348,880,872],\"LU021\":[1317,1362,1328,904,0],\"LU022\":[1325,1385,1335,0,868],\"LU020\":[1325,1385,1335,879,869],\"LU029\":[1339,1387,1350,904,893],\"PE048\":[1333,1385,1346,880,872],\"PE047\":[1328,1387,1345,878,869],\"PE046\":[1339,1388,1349,880,872],\"LU027\":[1337,1382,1348,928,872],\"LU028\":[1338,1398,1348,879,853],\"PE045\":[1324,1365,1329,854,861],\"LU025\":[1337,1382,1348,928,872],\"PE044\":[1348,1388,1353,0,894],\"LU026\":[1339,1345,1342,938,870],\"PE043\":[1335,1360,1344,879,868],\"EK019\":[1313,1389,1349,880,870],\"LU023\":[1338,1398,1348,878,0],\"EK018\":[1339,1393,1349,880,871],\"PE042\":[1319,1375,1339,879,869],\"PE041\":[1329,1375,1339,879,869],\"LU024\":[1338,1384,1348,955,0],\"EK005\":[1337,1389,1349,880,872],\"EK004\":[1339,1389,1349,869,849],\"EK003\":[1313,1373,1333,868,848],\"EK002\":[1337,1389,1349,880,872],\"EK001\":[1337,1389,1349,880,872],\"PE049\":[1342,1387,1349,899,898],\"FG001\":[1303,0,1313,848,848],\"PE051\":[1339,1399,1349,925,885],\"PE050\":[1339,1388,1348,880,872],\"LU030\":[1337,1386,1347,928,872],\"LU031\":[1333,0,1338,0,0],\"PE059\":[1339,1387,1349,880,872],\"PE058\":[1339,1387,1351,890,878],\"PE056\":[1339,1399,1349,895,872],\"EK009\":[1313,1399,1347,0,0],\"PE055\":[1339,1389,1349,879,869],\"PE054\":[1339,1387,1349,880,872],\"EK008\":[1313,1388,1348,883,871],\"EK007\":[1313,1393,1349,898,895],\"PE053\":[1339,1402,1347,901,0],\"PE052\":[1332,1380,1342,880,872],\"EK006\":[1313,1386,1348,0,869],\"LU009\":[1338,1398,1348,878,868],\"PE062\":[1338,1386,1353,909,871],\"PE061\":[1339,1387,1349,880,872],\"PE060\":[1319,1379,1339,880,875],\"LU007\":[1313,1365,1329,0,0],\"LU008\":[1338,1398,1348,878,869],\"PE069\":[1338,1386,1353,889,879],\"PE068\":[1369,1418,1379,849,849],\"LU005\":[1313,1389,1335,925,0],\"LU006\":[1313,1385,1333,925,865],\"PE067\":[1338,1386,1349,0,872],\"LU003\":[1329,1388,1348,885,872],\"PE066\":[1339,1395,1349,967,968],\"PE065\":[1339,1387,1349,880,872],\"LU004\":[1337,1383,1339,928,0],\"LU001\":[1336,1388,1348,854,869],\"PE064\":[1337,1385,1347,883,875],\"LU002\":[1331,1324,1339,854,0],\"PE063\":[1339,1389,1349,0,865],\"LU010\":[1338,1398,1348,885,867],\"ES066\":[1329,1369,1339,874,865],\"PE073\":[1329,1386,1345,879,871],\"LU011\":[1339,1399,1348,880,868],\"PE071\":[1339,1387,1349,0,0],\"ES064\":[1329,1375,1340,879,885],\"PE070\":[1336,1377,1348,880,872],\"ES065\":[1336,1386,1345,879,851],\"LU018\":[1336,1375,1345,890,869],\"LU019\":[1329,1375,1347,928,872],\"PE079\":[1339,1387,1349,904,896],\"LU016\":[1315,1365,1325,817,0],\"PE078\":[1339,1387,1349,904,898],\"LU017\":[1337,1384,1348,932,876],\"PE077\":[1334,1339,1346,862,850],\"LU014\":[1328,1383,1341,0,0],\"PE076\":[1349,1397,1355,889,875],\"LU015\":[1337,1345,1348,928,872],\"PE075\":[1348,1396,1357,889,879],\"LU012\":[1328,1383,1341,885,868],\"PE074\":[1337,1387,1347,878,852],\"LU013\":[1328,1383,1341,878,869],\"ST009\":[1328,1345,1346,878,849],\"ST001\":[1328,1345,1346,878,849],\"ST002\":[1328,1345,1346,878,849],\"ST003\":[1328,1345,1346,878,849],\"ST004\":[1328,1345,1346,878,849],\"ES062\":[1337,1388,1347,879,851],\"ES063\":[1328,1345,1346,878,849],\"ST006\":[1334,1340,1339,876,851],\"ST007\":[1336,1340,1347,0,0],\"ES060\":[1338,1388,1348,0,0],\"ST008\":[1332,1340,1339,880,851],\"ES061\":[1308,1368,1348,887,852],\"ES055\":[1335,1385,1352,865,865],\"ES056\":[1348,1395,1348,895,880],\"ES053\":[1347,1396,1356,889,879],\"ES054\":[1346,1399,1349,893,879],\"ES059\":[1338,1379,1348,882,852],\"ES057\":[1335,1389,1345,897,0],\"ES058\":[1323,1379,1348,882,859],\"PE004\":[1339,1387,1348,879,871],\"PE003\":[1336,1387,1352,880,872],\"PE002\":[1336,1388,1348,877,872],\"PE001\":[1337,1386,1351,879,871],\"PE008\":[1313,1367,1333,879,859],\"PE007\":[1313,1368,1333,880,872],\"PE006\":[1313,1368,1333,868,852],\"PE005\":[1313,1387,1349,880,872],\"ES051\":[1343,1393,1353,886,856],\"ES052\":[1337,1390,1348,880,865],\"ES050\":[1337,1387,1348,882,0],\"ES044\":[1335,1386,1347,882,0],\"ES045\":[1338,1389,1348,881,851],\"ES042\":[1339,1389,1349,888,855],\"ES043\":[1329,1370,1334,859,866],\"ES048\":[1339,1389,1349,882,852],\"ES049\":[1333,1389,1338,886,0],\"ES046\":[1339,1389,1348,881,851],\"ES047\":[1339,1389,1348,881,851],\"PE015\":[1349,1399,1359,889,872],\"PE014\":[1339,1387,1349,880,872],\"PE013\":[1339,1387,1349,880,872],\"PE012\":[1339,1387,1349,880,872],\"PE011\":[1313,1368,1333,880,872],\"PE010\":[1314,1387,1334,880,872],\"PE019\":[1339,1387,1349,885,872],\"PE018\":[1339,1387,1349,880,872],\"PE017\":[1338,1387,1349,880,870],\"PE016\":[1339,1379,1349,885,871],\"ES040\":[1336,1386,1345,879,855],\"ES041\":[1339,1389,1349,0,0],\"ES033\":[1329,1389,1344,879,849],\"ES034\":[1339,1389,1349,880,852],\"ES031\":[1329,1385,1345,878,849],\"ES032\":[1339,1389,1349,883,870],\"ES037\":[1337,1388,1347,880,857],\"ES038\":[1337,1361,1346,880,871],\"ES035\":[1343,1393,1355,886,856],\"ES036\":[1339,1389,1349,0,0],\"PE026\":[1349,1397,1358,891,877],\"PE025\":[1338,1381,1349,880,872],\"PE024\":[1338,1381,1349,880,872],\"ES039\":[1339,1389,1349,885,852],\"PE023\":[1339,1387,1349,880,872],\"PE022\":[1329,1387,1349,880,872],\"PE021\":[1339,1387,1349,880,872],\"PE020\":[1339,1393,1349,880,870],\"IS003\":[1339,1381,1349,948,0],\"PE029\":[1339,1387,1349,880,872],\"PE028\":[1339,1389,1352,881,873],\"PE027\":[1327,1385,1338,879,871],\"IS001\":[1339,1388,1349,882,852],\"ES030\":[1352,1402,1362,895,865],\"ES022\":[1339,1389,1349,882,869],\"ES023\":[1339,1389,1349,889,859],\"ES020\":[1339,1389,1349,889,859],\"ES021\":[1329,1385,1339,882,856],\"ES026\":[1319,1369,1319,899,899],\"ES027\":[1335,1387,1349,883,852],\"ST010\":[1328,1345,1346,878,849],\"ES024\":[1334,1384,1343,885,852],\"ES025\":[1339,1389,1349,882,852],\"PE037\":[1325,1369,1329,879,869],\"PE036\":[1337,1386,1346,878,865],\"ES028\":[1329,1389,1349,882,869],\"PE035\":[1338,1387,1347,879,869],\"ES029\":[1325,1379,1337,889,869],\"PE034\":[1335,1386,1345,880,872],\"PE033\":[1329,1375,1335,880,870],\"PE032\":[1339,1389,1357,896,894],\"PE031\":[1339,1389,1348,880,874],\"EK095\":[1336,1390,1347,882,871],\"EK094\":[1338,1390,1348,880,871],\"EK093\":[1339,1390,1349,884,894],\"EK092\":[1335,0,1345,889,0],\"EK091\":[1337,1389,1347,882,872],\"EK090\":[1338,1389,1348,881,870],\"ES011\":[1329,1399,1348,886,871],\"ES012\":[1339,1389,1348,882,852],\"TO001\":[1313,1368,1333,877,852],\"ES010\":[1329,1389,1349,879,852],\"TO002\":[1338,1389,1348,883,869],\"ES015\":[1313,1373,1333,838,858],\"ES016\":[1336,1386,1347,879,852],\"ES013\":[1335,1389,1349,882,849],\"ES014\":[1339,1389,1349,882,869],\"ES019\":[1328,1389,1339,882,852],\"ES017\":[1328,1345,1346,875,849],\"ES018\":[1313,1373,1333,882,852],\"TO003\":[1336,0,1345,928,875],\"TO004\":[1337,0,1347,879,0],\"TO005\":[1347,0,1357,890,875],\"EK089\":[1339,1391,1349,883,872],\"EK088\":[1339,1389,1349,880,871],\"EK087\":[1339,1389,1347,883,871],\"EK086\":[1339,1389,1348,880,871],\"EK085\":[1339,1390,1339,880,851],\"EK084\":[1340,1390,1350,880,872],\"EK083\":[1339,1389,1349,881,871],\"EK082\":[1339,1389,1349,881,871],\"EK081\":[1338,1378,1349,908,899],\"EK080\":[1339,1389,1352,885,880],\"ES001\":[1313,1376,1347,879,867],\"ES004\":[1338,1388,1349,886,852],\"ES005\":[1339,1388,1347,880,852],\"ES002\":[1339,1389,1349,886,852],\"ES003\":[1339,1389,1349,886,852],\"ES008\":[1318,1389,1347,882,852],\"ES009\":[1339,1389,1349,880,872],\"ES006\":[1313,1368,1333,877,852],\"ES007\":[1339,1389,1349,882,852],\"EK079\":[1336,1389,1349,880,872],\"EK078\":[1338,1390,1349,881,871],\"EK077\":[1339,1389,1349,882,875],\"EK076\":[1347,1397,1358,888,880],\"EK075\":[1339,1389,1352,885,880],\"EK074\":[1347,1390,1358,889,879],\"EK073\":[1337,1389,1349,881,869],\"EK072\":[1339,1389,1350,895,880],\"EK071\":[1339,1387,1348,880,870],\"EK070\":[1338,1390,1349,881,872],\"EK069\":[1337,1389,1347,882,871],\"EK068\":[1338,1386,1353,920,870],\"EK067\":[1336,1387,1349,880,870],\"EK066\":[1336,1377,1349,880,872],\"EK065\":[1333,1383,1344,876,859],\"EK064\":[1339,1389,1349,880,895],\"EK063\":[1333,1385,1344,850,880],\"EK062\":[1315,1338,1338,865,848],\"EK061\":[1322,1364,1328,886,870],\"EK060\":[1339,1389,1350,881,871],\"EK059\":[1336,1389,1348,883,871],\"EK058\":[1339,1388,1350,933,871],\"EK057\":[1339,1389,1348,880,871],\"EK056\":[1336,1389,1348,879,872],\"EK055\":[1339,1389,1349,883,870],\"EK053\":[1339,1389,1349,899,879],\"EK052\":[1328,1385,1339,875,869],\"EK051\":[1339,1389,1349,0,0],\"EK050\":[1315,1389,1349,0,0],\"PE084\":[1339,1387,1349,880,872],\"PE083\":[1338,1386,1349,882,873],\"PE082\":[1339,1387,1349,880,872],\"PE081\":[1337,1386,1347,889,872],\"PE080\":[1339,1387,1349,880,872],\"PE089\":[1327,1374,1332,887,878],\"PE087\":[1329,1377,1339,870,862],\"PE086\":[1339,1387,1349,880,872],\"PE085\":[1317,1386,1348,879,872],\"EK049\":[1364,1414,1375,887,879],\"EK048\":[1339,1389,1349,880,869],\"EK047\":[1335,1389,1349,880,869],\"EK046\":[1349,1389,1359,905,894],\"EK045\":[1329,1389,1347,881,871],\"EK044\":[1314,1399,1359,885,870],\"EK043\":[1325,1375,1329,879,895],\"EK042\":[1321,1417,1378,885,889],\"EK041\":[1329,1388,1345,879,870],\"EK040\":[1342,1393,1352,899,872],\"EK038\":[1315,1379,1348,881,871],\"EK037\":[1339,1389,1349,886,869],\"EK036\":[1329,0,1339,950,0],\"EK035\":[1313,1389,1349,880,873],\"EK034\":[1339,1389,1349,881,870],\"EK033\":[1313,1388,1349,882,871],\"EK032\":[1328,1389,1338,886,869],\"EK031\":[1337,1391,1347,875,870],\"EK030\":[1337,1390,1349,883,872],\"AG013\":[1339,1387,1349,0,872],\"AG010\":[1337,1365,1349,980,880],\"AG011\":[1348,1396,1357,889,879],\"EK039\":[1315,1390,1348,881,872],\"AG012\":[1339,1389,1349,880,872],\"EK027\":[1329,1389,1346,880,872],\"EK026\":[1329,1389,1349,883,871],\"EK025\":[1329,1389,1350,880,872],\"EK024\":[1327,1389,1347,880,871],\"AG006\":[1337,1386,1347,878,869],\"EK023\":[1337,1387,1347,880,870],\"AG007\":[1329,1374,1339,0,0],\"EK022\":[1328,1379,1349,880,870],\"AG008\":[1338,1386,1348,879,870],\"EK021\":[1338,1389,1349,883,871],\"EK020\":[1339,1389,1349,884,872],\"AG009\":[1339,1385,1349,875,867],\"AG002\":[1339,1387,1349,880,872],\"AG003\":[1339,1387,1349,880,872],\"AG004\":[1349,1399,1359,899,0],\"AG005\":[1319,1379,1339,875,865],\"EK029\":[1329,1389,1349,881,871],\"AG001\":[1313,1368,1333,868,848],\"EK028\":[1339,1389,1349,884,873]}}";

}