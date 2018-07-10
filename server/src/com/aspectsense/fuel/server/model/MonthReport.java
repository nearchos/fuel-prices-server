package com.aspectsense.fuel.server.model;

import java.io.Serializable;
import java.util.Map;

public class MonthReport implements Serializable {

    private Map<Integer, StationCodeToPrice []> fuelTypeToStationsOrderedByPriceDescending;
    private Map<Integer, CityToPrice []> fuelTypeToCitiesOrderedByPriceDescending;
    private Map<Integer, BrandToPrice []> fuelTypeToBrandsOrderedByPriceDescending;

    public MonthReport(final Map<Integer, StationCodeToPrice[]> fuelTypeToStationsOrderedByPriceDescending,
                       final Map<Integer, CityToPrice[]> fuelTypeToCitiesOrderedByPriceDescending,
                       final Map<Integer, BrandToPrice[]> fuelTypeToBrandsOrderedByPriceDescending) {
        this.fuelTypeToStationsOrderedByPriceDescending = fuelTypeToStationsOrderedByPriceDescending;
        this.fuelTypeToCitiesOrderedByPriceDescending = fuelTypeToCitiesOrderedByPriceDescending;
        this.fuelTypeToBrandsOrderedByPriceDescending = fuelTypeToBrandsOrderedByPriceDescending;
    }

    public Map<Integer, StationCodeToPrice[]> getFuelTypeToStationsOrderedByPriceDescending() {
        return fuelTypeToStationsOrderedByPriceDescending;
    }

    public Map<Integer, CityToPrice[]> getFuelTypeToCitiesOrderedByPriceDescending() {
        return fuelTypeToCitiesOrderedByPriceDescending;
    }

    public Map<Integer, BrandToPrice[]> getFuelTypeToBrandsOrderedByPriceDescending() {
        return fuelTypeToBrandsOrderedByPriceDescending;
    }

    static public class StationCodeToPrice implements Comparable {
        String stationCode;
        String stationName;
        String stationBrand;
        double price;
        int numOfDaysRankedTop;
        String cityName;

        public StationCodeToPrice(String stationCode, String stationName, String stationBrand, double price, int numOfDaysRankedTop, String cityName) {
            this.stationCode = stationCode;
            this.stationName = stationName;
            this.stationBrand = stationBrand;
            this.price = price;
            this.numOfDaysRankedTop = numOfDaysRankedTop;
            this.cityName = cityName;
        }

        public String getStationCode() {
            return stationCode;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public int compareTo(Object other) {
            final double otherPrice = ((StationCodeToPrice) other).price;
            final int otherNumOfDaysRankedTop = ((StationCodeToPrice) other).numOfDaysRankedTop;
            return Double.compare(price, otherPrice) == 0 ?
                    Integer.compare(numOfDaysRankedTop, otherNumOfDaysRankedTop)
                    : Double.compare(price, otherPrice);
        }

        @Override
        public String toString() {
            return stationCode + "->€" + price;
        }
    }

    static public class CityToPrice implements Comparable {
        String cityName;
        double price;

        public CityToPrice(String cityName, double price) {
            this.cityName = cityName;
            this.price = price;
        }

        public String getCity() {
            return cityName;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public int compareTo(Object other) {
            final double otherPrice = ((CityToPrice) other).price;
            return Double.compare(price, otherPrice);
        }
    }

    static public class BrandToPrice implements Comparable {
        String brand;
        double price;

        public BrandToPrice(String brand, double price) {
            this.brand = brand;
            this.price = price;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public int compareTo(Object other) {
            final double otherPrice = ((BrandToPrice) other).price;
            return Double.compare(price, otherPrice);
        }
    }
}