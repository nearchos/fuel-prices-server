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
import java.util.Vector;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         17/01/2016
 *         10:04
 */
public class Price implements Serializable {

    private final String uuid;
    private String stationCode;
    private String fuelType;
    private long fuelPriceInMillieuros; // in milli euros
    private long lastUpdated;

    public Price(final String uuid, String stationCode, String fuelType, long fuelPriceInMillieuros, long lastUpdated) {
        this.uuid = uuid;
        this.stationCode = stationCode;
        this.fuelType = fuelType;
        this.fuelPriceInMillieuros = fuelPriceInMillieuros;
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public String getStationCode() {
        return stationCode;
    }

    public String getFuelType() {
        return fuelType;
    }

    public long getFuelPriceInMillieuros() {
        return fuelPriceInMillieuros;
    }

    public double getFuelPriceInEuros() {
        return fuelPriceInMillieuros / 1000d;
    }

    public String getFuelPriceFormatted() {
        return String.format("€%5.3f", fuelPriceInMillieuros / 1000d);
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    static public String toJSONObject(final String stationCode, final Vector<Price> prices) {

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ \"stationCode\": \"").append(stationCode).append("\", \"prices\": [ ");
        for(int i = 0; i < prices.size(); i++) {
            final Price price = prices.elementAt(i);
            stringBuilder.append("{ \"type\": \"").append(price.fuelType)
                    .append("\", \"price\": ").append(price.fuelPriceInMillieuros)
                    .append(" }");
            if(i < prices.size() - 1) stringBuilder.append(", ");
        }
        stringBuilder.append("] }");
        return stringBuilder.toString();
    }

//    public static void main(String[] args) {
//        final Vector<Price> prices = new Vector<>();
//        prices.add(new Price("123", "EK007", "1", 1234, 0));
//        prices.add(new Price("234", "EK007", "2", 1345, 0));
//        prices.add(new Price("345", "EK007", "3", 1301, 0));
//        System.out.println(prices);
//        System.out.println(toJSONObject("EK007", prices));
//    }
}