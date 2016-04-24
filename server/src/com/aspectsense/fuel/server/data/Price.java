package com.aspectsense.fuel.server.data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Nearchos Paspallis
 * 13-Feb-16.
 */
public class Price implements Serializable {

    private final String stationCode;
    private final int [] prices;

    public Price(String stationCode, int [] prices) {
        this.stationCode = stationCode;
        this.prices = prices;
    }

    public String getStationCode() {
        return stationCode;
    }

    public int[] getPrices() {
        return prices;
    }

    @Override
    public String toString() {
        return stationCode + "-> " + Arrays.toString(prices);
    }
}