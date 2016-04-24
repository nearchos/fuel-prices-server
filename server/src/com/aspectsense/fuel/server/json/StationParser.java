package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.data.Station;
import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import static com.aspectsense.fuel.server.json.Util.sanitizeForJSON;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16.
 */
public class StationParser {

    public static String toStationJson(final Station station) {
        return "{ \"code\": \"" + station.getStationCode() +
                "\", \"name\": \"" + station.getStationName() +
                "\", \"telNo\": \"" + station.getStationTelNo() +
                "\", \"address\": \"" + station.getStationAddress() +
                "\", \"district\": \"" + station.getStationDistrict() +
                "\", \"city\": \"" + station.getStationCity() +
                "\", \"lat\": " + station.getStationLatitude() +
                ", \"lng\": " + station.getStationLongitude() +
                " }";
    }

    public static String toStationJson(final PetroleumPriceDetail petroleumPriceDetail) {
        return "{ \"code\": \"" + petroleumPriceDetail.getStationCode() +
                "\", \"name\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationName()) +
                "\", \"telNo\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationTelNo()) +
                "\", \"address\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationAddress()) +
                "\", \"district\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationDistrict()) +
                "\", \"city\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationCity()) +
                "\", \"lat\": " + petroleumPriceDetail.getStationLatitude() +
                ", \"lng\": " + petroleumPriceDetail.getStationLongitude() +
                " }";
    }

    public static Station fromStationJsonObject(final JSONObject stationJsonObject) throws JSONException {
        return new Station(
                stationJsonObject.getString("code"),
                stationJsonObject.getString("name"),
                stationJsonObject.getString("telNo"),
                stationJsonObject.getString("city"),
                stationJsonObject.getString("district"),
                stationJsonObject.getString("address"),
                stationJsonObject.getDouble("lat"),
                stationJsonObject.getDouble("lng")
        );
    }

}
