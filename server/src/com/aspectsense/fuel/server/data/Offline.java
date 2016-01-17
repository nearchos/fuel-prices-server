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
 *         17/01/2016
 *         12:31
 */
public class Offline implements Serializable {

    private String uuid;
    private String stationCode;
    private boolean isOffline;
    private long lastUpdated;

    public Offline(final String uuid, final String stationCode, final boolean isOffline, final long lastUpdated) {
        this.uuid = uuid;
        this.stationCode = stationCode;
        this.isOffline = isOffline;
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public String getStationCode() {
        return stationCode;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public String toJSONObject() {
        return "{ \"stationCode\": \"" + stationCode + "\", \"offline\": " + isOffline + " }";
    }
}
