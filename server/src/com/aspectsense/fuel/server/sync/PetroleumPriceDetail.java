/*
 * This file is part of the Cyprus Fuel Guide server.
 *
 * The Cyprus Fuel Guide server is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Cyprus Fuel Guide server is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cyprus Fuel Guide. If not, see <http://www.gnu.org/licenses/>.
 */

package com.aspectsense.fuel.server.sync;

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
        this.fuelCompanyCode = fuelCompanyCode.trim();
        this.fuelCompanyName = fuelCompanyName.trim();
        this.stationCode = stationCode.trim();
        this.stationName = stationName.trim();
        this.stationTelNo = stationTelNo.trim();
        this.stationCity = stationCity.trim();
        this.stationDistrict = stationDistrict.trim();
        this.stationAddress = stationAddress.trim();
        this.stationLatitude = stationLatitude.trim();
        this.stationLongitude = stationLongitude.trim();
        this.priceModificationDate = priceModificationDate.trim();
        this.fuelType = fuelType.trim();
        this.fuelPrice = fuelPrice.trim();
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
}