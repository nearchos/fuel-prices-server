package com.aspectsense.fuel.server.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SyncMessage implements Serializable {

    @com.google.gson.annotations.SerializedName("lastUpdated")
    private long lastUpdated;

    @com.google.gson.annotations.SerializedName("stations")
    private Station [] stations;

    @com.google.gson.annotations.SerializedName("offlines")
    private Offline [] offlines;

    @com.google.gson.annotations.SerializedName("prices")
    private Price [] prices;

    public SyncMessage() {
    }

    public SyncMessage(long lastUpdated, Station[] stations, Offline[] offlines, Price[] prices) {
        this.lastUpdated = lastUpdated;
        this.stations = stations;
        this.offlines = offlines;
        this.prices = prices;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public Station[] getStations() {
        return stations;
    }

    public Map<String,Station> getCodeToStationsMap() {
        final Map<String,Station> codeToStationsMap = new HashMap<>();
        for(final Station station : stations) {
            codeToStationsMap.put(station.getStationCode(), station);
        }
        return codeToStationsMap;
    }

    public Offline[] getOfflines() {
        return offlines;
    }

    public Map<String, Boolean> getOfflinesMap() {
        final Map<String, Boolean> offlinesMap = new HashMap<>();
        for(final Offline offline : offlines) {
            offlinesMap.put(offline.getStationCode(), offline.isOffline());
        }
        return offlinesMap;
    }

    public Price[] getPrices() {
        return prices;
    }

    public Map<String, Price> getPricesMap() {
        final Map<String, Price> pricesMap = new HashMap<>();
        for(final Price price : prices) {
            pricesMap.put(price.getStationCode(), price);
        }
        return pricesMap;
    }
}