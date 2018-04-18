package com.aspectsense.fuel.server.model;

import java.io.Serializable;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16.
 */
public class Offline implements Serializable {

    @com.google.gson.annotations.SerializedName("stationCode")
    private String stationCode;
    @com.google.gson.annotations.SerializedName("offline")
    private boolean offline;

    public Offline() {
    }

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