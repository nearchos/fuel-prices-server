package com.aspectsense.fuel.server.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Nearchos Paspallis
 * 19-May-17.
 */
public class DailySummary implements Serializable {

    @com.google.gson.annotations.SerializedName("crudeOilInUsd")
    private double crudeOilPriceInUSD;
    @com.google.gson.annotations.SerializedName("eurUsd")
    private double eurToUsd;
    @com.google.gson.annotations.SerializedName("eurGbp")
    private double eurToGbp;
    @com.google.gson.annotations.SerializedName("stations")
    private Map<String, Integer[]> stationCodeToPricesMap;

    public DailySummary() {
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

    public void setEurToUsd(double eurToUsd) {
        this.eurToUsd = eurToUsd;
    }

    public void setEurToGbp(double eurToGbp) {
        this.eurToGbp = eurToGbp;
    }

    public Map<String, Integer[]> getStationCodeToPricesMap() {
        return stationCodeToPricesMap;
    }

    @Override
    public String toString() {
        return "DailySummary{" +
                "crudeOilPriceInUSD=" + crudeOilPriceInUSD +
                ", eurToUsd=" + eurToUsd +
                ", eurToGbp=" + eurToGbp +
                ", stationCodeToPricesMap=" + stationCodeToPricesMap +
                '}';
    }
}