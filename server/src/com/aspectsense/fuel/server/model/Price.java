package com.aspectsense.fuel.server.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16.
 */
public class Price implements Serializable {

    @com.google.gson.annotations.SerializedName("stationCode")
    private String stationCode;
    @com.google.gson.annotations.SerializedName("prices")
    private int [] prices;
    private long [] timestamps;

    public Price() {
    }

    public Price(final String stationCode, final int [] prices, final long [] timestamps) {
        this.stationCode = stationCode;
        this.prices = prices;
        this.timestamps = timestamps;
    }

    public String getStationCode() {
        return stationCode;
    }

    public int[] getPrices() {
        return prices;
    }

    public long[] getTimestamps() {
        return timestamps;
    }

    @Override
    public String toString() {
        return stationCode + " -> " + Arrays.toString(prices) + " (" + Arrays.toString(timestamps) + ")";
    }
}