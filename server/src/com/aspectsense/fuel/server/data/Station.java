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
 * along with Foobar. If not, see <http://www.gnu.org/licenses/>.
 */

package com.aspectsense.fuel.server.data;

import java.io.Serializable;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         29/12/2015
 *         22:39
 */
public class Station implements Serializable {

    private final String uuid;
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
    private final boolean isOffline;
    private final long lastModified;

    public Station(String uuid, String fuelCompanyCode, String fuelCompanyName, String stationCode, String stationName,
                   String stationTelNo, String stationCity, String stationDistrict, String stationAddress,
                   String stationLatitude, String stationLongitude, boolean isOffline, long lastModified) {
        this.uuid = uuid;
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
        this.isOffline = isOffline;
        this.lastModified = lastModified;
    }

    public String getUuid() {
        return uuid;
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

    public boolean isOffline() {
        return isOffline;
    }

    public long getLastModified() {
        return lastModified;
    }
}