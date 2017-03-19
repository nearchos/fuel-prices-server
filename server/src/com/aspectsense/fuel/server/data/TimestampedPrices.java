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
import java.util.Arrays;
import java.util.Vector;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         19/03/2017
 */
public class TimestampedPrices implements Serializable {

    final Vector<String> dates;
    final Vector<Integer> prices;

    public TimestampedPrices() {
        dates = new Vector<>();
        prices = new Vector<>();
    }

    public TimestampedPrices(final String date, final Integer price) {
        this();
        add(date, price);
    }

    public void add(final String date, final Integer price) {
        dates.add(date);
        prices.add(price);
    }

    public String getDatesAsJsonStringArray() {
        final StringBuilder stringBuilder = new StringBuilder("[");
        int counter = 0;
        for(final String date : dates) {
            counter++;
            stringBuilder.append("\"").append(date).append("\"").append(counter < dates.size() ? ", " : "]");
        }
        return stringBuilder.toString();
    }

    public String getPricesAsJsonIntArray() {
        final Integer [] pricesAsArray = new Integer[prices.size()];
        for(int i = 0; i < pricesAsArray.length; i++) {
            pricesAsArray[i] = prices.get(i);
        }
        return Arrays.toString(pricesAsArray);
    }
}