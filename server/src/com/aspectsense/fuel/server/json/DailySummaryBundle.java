package com.aspectsense.fuel.server.json;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Nearchos Paspallis
 * 19-May-17.
 */
public class DailySummaryBundle implements Serializable {

    private double crudeOilPriceInUSD;
    private double eurToUsd;
    private double eurToGbp;
    private Map<String, Integer[]> stationCodeToPricesMap;

    public DailySummaryBundle(final double crudeOilPriceInUSD, final double eurToUsd, final double eurToGbp, final Map<String, Integer[]> stationCodeToPricesMap) {
        this.crudeOilPriceInUSD = crudeOilPriceInUSD;
        this.eurToUsd = eurToUsd;
        this.eurToGbp = eurToGbp;
        this.stationCodeToPricesMap = stationCodeToPricesMap;
    }

    public double getCrudeOilPriceInUSD() {
        return crudeOilPriceInUSD;
    }

    public double getEurToUsd() {
        return eurToUsd;
    }

    public double getEurToGbp() {
        return eurToGbp;
    }

    public Map<String, Integer[]> getStationCodeToPricesMap() {
        return stationCodeToPricesMap;
    }
}