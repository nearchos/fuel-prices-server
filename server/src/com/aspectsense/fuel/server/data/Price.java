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
 *         22:42
 */
public class Price implements Serializable {

    private final String uuid;
    private final String stationCode;
    private final String priceModificationDate;
    private final String fuelType;
    private final String fuelPrice;
    private final long lastUpdated;

    public Price(String uuid, String stationCode, String priceModificationDate, String fuelType, String fuelPrice, long lastUpdated) {
        this.uuid = uuid;
        this.stationCode = stationCode;
        this.priceModificationDate = priceModificationDate;
        this.fuelType = fuelType;
        this.fuelPrice = fuelPrice;
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public String getStationCode() {
        return stationCode;
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

    public long getLastUpdated() {
        return lastUpdated;
    }
}