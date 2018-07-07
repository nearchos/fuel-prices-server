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
 * @author Nearchos Paspallis
 * 18-Mar-17
 */
public class DailySummary implements Serializable {

    private final String uuid;
    private final String json; // JSON-formatted message having the latest prices of all stations
    private final String date; // e.g. 2017-03-17

    public DailySummary(final String uuid, final String json, final String date) {
        this.uuid = uuid;
        this.json = json;
        this.date = date;
    }

    public String getUuid() {
        return uuid;
    }

    public String getJson() {
        return json;
    }

    public String getDate() {
        return date;
    }
}