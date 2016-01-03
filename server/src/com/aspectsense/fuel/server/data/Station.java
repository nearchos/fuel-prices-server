package com.aspectsense.fuel.server.data;

import java.io.Serializable;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         29/12/2015
 *         22:39
 */
public class Station implements Serializable {

    private final String uuid;
    private final String fuelCompanyCode;
    private final String fuelCompanyName;
    private final String stationCode;
    private final String stationName;
    private final String stationTelNo;
    private final String stationCity;
    private final String stationDistrict;
    private final String stationAddress;
    private final String stationLatitude;
    private final String stationLongitude;
    private final boolean isOffline;
    private final long lastModified;

    public Station(String uuid, String fuelCompanyCode, String fuelCompanyName, String stationCode, String stationName,
                   String stationTelNo, String stationCity, String stationDistrict, String stationAddress,
                   String stationLatitude, String stationLongitude, boolean isOffline, long lastModified) {
        this.uuid = uuid;
        this.fuelCompanyCode = fuelCompanyCode;
        this.fuelCompanyName = fuelCompanyName;
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.stationTelNo = stationTelNo;
        this.stationCity = stationCity;
        this.stationDistrict = stationDistrict;
        this.stationAddress = stationAddress;
        this.stationLatitude = stationLatitude;
        this.stationLongitude = stationLongitude;
        this.isOffline = isOffline;
        this.lastModified = lastModified;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFuelCompanyCode() {
        return fuelCompanyCode;
    }

    public String getFuelCompanyName() {
        return fuelCompanyName;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationTelNo() {
        return stationTelNo;
    }

    public String getStationCity() {
        return stationCity;
    }

    public String getStationDistrict() {
        return stationDistrict;
    }

    public String getStationAddress() {
        return stationAddress;
    }

    public String getStationLatitude() {
        return stationLatitude;
    }

    public String getStationLongitude() {
        return stationLongitude;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public long getLastModified() {
        return lastModified;
    }
}