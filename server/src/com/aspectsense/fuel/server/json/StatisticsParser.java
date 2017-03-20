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

package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.data.FuelType;
import com.aspectsense.fuel.server.data.TimestampedPrices;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         19/03/2017
 */
public class StatisticsParser {

    public static String toStatisticsJson(final Map<String, Map<FuelType, TimestampedPrices>> stationsToFuelTypeToTimestampedPricesMap,
                                          final double [] averages, final String duration, final String today) throws JSONException {

        // the generated message will have for each station and each fuel type...
        // stationCode -> timestamps: [date-1, date-2, date-3, ..., date-N], prices: [price-1, price-2, price-3, ..., price-N]
        final StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("  \"date\": \"").append(today).append("\",\n");
        stringBuilder.append("  \"duration\": \"").append(duration).append("\",\n");
        stringBuilder.append("  \"stations\": {\n");

        int count = 0;
        final Set<String> allStations = stationsToFuelTypeToTimestampedPricesMap.keySet();
        for(final String station : allStations) {
            count++;
            final Map<FuelType, TimestampedPrices> fuelTypeToTimestampedPricesMap = stationsToFuelTypeToTimestampedPricesMap.get(station);

            stringBuilder.append("    \"").append(station).append("\": {\n");
            int fuelTypeCount = 0;
            for(final FuelType fuelType : FuelType.ALL_FUEL_TYPES) {
                fuelTypeCount++;
                stringBuilder.append("      \"").append(fuelType.getCodeAsString()).append("\": { ");
                final String timestampsAsJsonStringArray = fuelTypeToTimestampedPricesMap.get(fuelType).getDatesAsJsonStringArray();
                stringBuilder.append("\"timestamps\": ").append(timestampsAsJsonStringArray).append(", ");
                final String pricesAsJsonIntArray = fuelTypeToTimestampedPricesMap.get(fuelType).getPricesAsJsonIntArray();
                stringBuilder.append("\"prices\": ").append(pricesAsJsonIntArray);
                stringBuilder.append(fuelTypeCount < FuelType.ALL_FUEL_TYPES.length ? "},\n" : "}\n");
            }
            stringBuilder.append(count < allStations.size() ? "    },\n" : "    }\n");
        }

        stringBuilder.append("  },\n");
        stringBuilder.append("  \"averages\": ").append(Arrays.toString(averages)).append("\n");
        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
