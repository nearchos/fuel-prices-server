package com.aspectsense.fuel.server.sync;

import com.aspectsense.fuel.server.data.Price;
import com.aspectsense.fuel.server.data.Station;

import java.io.Serializable;

/**
 * @author Nearchos Paspallis
 *         29/12/2015
 *         13:57
 */
public class PetroleumPriceDetail implements Serializable {

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
    private final String priceModificationDate;
    private final String fuelType;
    private final String fuelPrice;
    private final boolean isOffline;

    public PetroleumPriceDetail(
            String fuelCompanyCode,
            String fuelCompanyName,
            String stationCode,
            String stationName,
            String stationTelNo,
            String stationCity,
            String stationDistrict,
            String stationAddress,
            String stationLatitude,
            String stationLongitude,
            String priceModificationDate,
            String fuelType,
            String fuelPrice,
            boolean isOffline) {
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
        this.priceModificationDate = priceModificationDate;
        this.fuelType = fuelType;
        this.fuelPrice = fuelPrice;
        this.isOffline = isOffline;
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

    public String getPriceModificationDate() {
        return priceModificationDate;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getFuelPrice() {
        return fuelPrice;
    }

    public boolean isOffline() {
        return isOffline;
    }

    @Override
    public String toString() {
        return "PetroleumPriceDetail{" +
                "fuelCompanyCode='" + fuelCompanyCode + '\'' +
                ", fuelCompanyName='" + fuelCompanyName + '\'' +
                ", stationCode='" + stationCode + '\'' +
                ", stationName='" + stationName + '\'' +
                ", stationTelNo='" + stationTelNo + '\'' +
                ", stationCity='" + stationCity + '\'' +
                ", stationDistrict='" + stationDistrict + '\'' +
                ", stationAddress='" + stationAddress + '\'' +
                ", stationLatitude=" + stationLatitude +
                ", stationLongitude=" + stationLongitude +
                ", priceModificationDate='" + priceModificationDate + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", fuelPrice='" + fuelPrice + '\'' +
                ", isOffline=" + isOffline +
                '}';
    }

    public boolean hasChanges(final Station station) {
        if(!fuelCompanyCode.equals(station.getFuelCompanyCode())) return true;
        if(!fuelCompanyName.equals(station.getFuelCompanyName())) return true;
        // it must be assumed that the station code does not change
        if(!stationName.equals(station.getStationName())) return true;
        if(!stationTelNo.equals(station.getStationTelNo())) return true;
        if(!stationCity.equals(station.getStationCity())) return true;
        if(!stationDistrict.equals(station.getStationDistrict())) return true;
        if(!stationAddress.equals(station.getStationAddress())) return true;
        if(!stationLatitude.equals(station.getStationLatitude())) return true;
        if(!stationLongitude.equals(station.getStationLongitude())) return true;
        if(isOffline != station.isOffline()) return true;

        return false;
    }

    public boolean hasChanges(final Price price) {
        if(!priceModificationDate.equals(price.getPriceModificationDate())) return true;
        if(!fuelPrice.equals(price.getFuelPrice())) return true;
        // it must be assumed that the station code does not change

        return false;
    }

}