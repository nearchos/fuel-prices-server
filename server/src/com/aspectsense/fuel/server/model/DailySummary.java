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

    public static void main(String[] args) {
        final String json = "{\n" +
                "  \"crudeOilInUsd\": 0.00,\n" +
                "  \"eurUsd\": 1.24,\n" +
                "  \"eurGbp\": 0.90,\n" +
                "  \"stations\": {\n" +
                "    \"ES066\": [1219, 1270, 1232, 815, 865],\n" +
                "    \"PE010\": [1208, 1284, 1214, 821, 873]\n" +
                "  }\n" +
                "}";
        final DailySummary dailySummary = new Gson().fromJson(json, DailySummary.class);
        System.out.println(dailySummary);
    }
}