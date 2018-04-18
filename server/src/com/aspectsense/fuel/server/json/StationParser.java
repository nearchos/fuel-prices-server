package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.model.Station;
import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;

import static com.aspectsense.fuel.server.json.Util.sanitizeForJSON;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16.
 */
public class StationParser {

    public static String toStationJson(final Station station) {
        return "{ \"code\": \"" + station.getStationCode() +
                "\", \"brand\": \"" + station.getStationBrand() +
                "\", \"name\": \"" + station.getStationName() +
                "\", \"telNo\": \"" + station.getStationTelNo() +
                "\", \"address\": \"" + station.getStationAddress() +
                "\", \"district\": \"" + station.getStationDistrict() +
                "\", \"city\": \"" + station.getStationCity() +
                "\", \"lat\": " + station.getStationLatitude() +
                ", \"lng\": " + station.getStationLongitude() +
                " }";
    }

    static String toStationJson(final PetroleumPriceDetail petroleumPriceDetail) {
        return "{ \"code\": \"" + petroleumPriceDetail.getStationCode() +
                "\", \"brand\": \"" + sanitizeForJSON(petroleumPriceDetail.getFuelCompanyName()) +
                "\", \"name\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationName()) +
                "\", \"telNo\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationTelNo()) +
                "\", \"address\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationAddress()) +
                "\", \"district\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationDistrict()) +
                "\", \"city\": \"" + sanitizeForJSON(petroleumPriceDetail.getStationCity()) +
                "\", \"lat\": " + petroleumPriceDetail.getStationLatitude() +
                ", \"lng\": " + petroleumPriceDetail.getStationLongitude() +
                " }";
    }
}
