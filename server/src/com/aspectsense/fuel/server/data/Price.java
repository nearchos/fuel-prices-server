package com.aspectsense.fuel.server.data;

import java.io.Serializable;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         29/12/2015
 *         22:42
 */
public class Price implements Serializable {

    private final String uuid;
    private final String stationCode;
    private final String priceModificationDate;
    private final String fuelType;
    private final String fuelPrice;
    private final long lastUpdated;

    public Price(String uuid, String stationCode, String priceModificationDate, String fuelType, String fuelPrice, long lastUpdated) {
        this.uuid = uuid;
        this.stationCode = stationCode;
        this.priceModificationDate = priceModificationDate;
        this.fuelType = fuelType;
        this.fuelPrice = fuelPrice;
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getPriceModificationDate() {
        return priceModificationDate;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getFuelPrice() {
        return fuelPrice;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}