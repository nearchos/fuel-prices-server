package com.aspectsense.fuel.server.data;

import java.io.Serializable;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16.
 */
public class Offline implements Serializable {

    private final String stationCode;
    private final boolean offline;

    public Offline(String stationCode, boolean offline) {
        this.stationCode = stationCode;
        this.offline = offline;
    }

    public String getStationCode() {
        return stationCode;
    }

    public boolean isOffline() {
        return offline;
    }

    @Override
    public String toString() {
        return stationCode + "->" + offline;
    }
}