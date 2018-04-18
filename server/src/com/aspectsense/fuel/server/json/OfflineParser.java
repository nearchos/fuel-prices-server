package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.model.Offline;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16.
 */
public class OfflineParser {

    public static String toJson(final Offline offline) {
        return "{ \"stationCode\": \"" + offline.getStationCode() + "\", \"offline\": " + offline.isOffline() + " }";
    }
}