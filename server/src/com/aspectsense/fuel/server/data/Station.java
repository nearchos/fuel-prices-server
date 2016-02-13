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

    private final String stationCode;
    private final String stationName;
    private final String stationTelNo;
    private final String stationCity;
    private final String stationDistrict;
    private final String stationAddress;
    private final double stationLatitude;
    private final double stationLongitude;

    public Station(String stationCode, String stationName, String stationTelNo, String stationCity,
                   String stationDistrict, String stationAddress, double stationLatitude, double stationLongitude) {
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.stationTelNo = stationTelNo;
        this.stationCity = stationCity;
        this.stationDistrict = stationDistrict;
        this.stationAddress = stationAddress;
        this.stationLatitude = stationLatitude;
        this.stationLongitude = stationLongitude;
    }

    public String getStationBrand() {
        if(stationCode.startsWith("AG")) return "Agip";
        else if(stationCode.startsWith("EK")) return "Eko";
        else if(stationCode.startsWith("ES")) return "Esso";
        else if(stationCode.startsWith("IS")) return "Independent";
        else if(stationCode.startsWith("LU")) return "Lukoil";
        else if(stationCode.startsWith("PE")) return "Petrolina";
        else if(stationCode.startsWith("ST")) return "Staroil";
        else if(stationCode.startsWith("TO")) return "Total";
        else return "Unknown";
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

    public double getStationLatitude() {
        return stationLatitude;
    }

    public double getStationLongitude() {
        return stationLongitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (Double.compare(station.stationLatitude, stationLatitude) != 0) return false;
        if (Double.compare(station.stationLongitude, stationLongitude) != 0) return false;
        if (!stationCode.equals(station.stationCode)) return false;
        if (!stationName.equals(station.stationName)) return false;
        if (!stationTelNo.equals(station.stationTelNo)) return false;
        if (!stationCity.equals(station.stationCity)) return false;
        if (!stationDistrict.equals(station.stationDistrict)) return false;
        return stationAddress.equals(station.stationAddress);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = stationCode.hashCode();
        result = 31 * result + stationName.hashCode();
        result = 31 * result + stationTelNo.hashCode();
        result = 31 * result + stationCity.hashCode();
        result = 31 * result + stationDistrict.hashCode();
        result = 31 * result + stationAddress.hashCode();
        temp = Double.doubleToLongBits(stationLatitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stationLongitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}