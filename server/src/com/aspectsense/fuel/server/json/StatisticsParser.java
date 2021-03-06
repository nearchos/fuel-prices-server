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

import java.util.*;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         19/03/2017
 */
public class StatisticsParser {

    public static String toStatisticsJson(
            final Map<String, Double> datesToCrudeOilPriceInUsd,
            final Map<String, Double> datesToEurUsd,
            final Map<String, Double> datesToEurGbp,
            final Map<String, Map<FuelType, TimestampedPrices>> stationsToFuelTypeToTimestampedPricesMap,
            final Set<String> selectedStations,
            final Map<String,Double[]> uniqueIncludedDatesToMeans,
            final Map<String,Integer[]> uniqueIncludedDatesToMedians,
            final Map<String,Integer[]> uniqueIncludedDatesTo1stQuartiles,
            final Map<String,Integer[]> uniqueIncludedDatesTo3rdQuartiles,
            final Map<String,Integer[]> uniqueIncludedDatesToMins,
            final Map<String,Integer[]> uniqueIncludedDatesToMaxs,
            final String duration, final String from, final String to) {

        // the generated message will have for each station and each fuel type...
        // stationCode -> timestamps: [date-1, date-2, date-3, ..., date-N], prices: [price-1, price-2, price-3, ..., price-N]
        final StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("  \"from\": \"").append(from).append("\",\n");
        stringBuilder.append("  \"to\": \"").append(to).append("\",\n");
        stringBuilder.append("  \"duration\": \"").append(duration).append("\",\n");

        final Vector<String> allDatesSorted = new Vector<>(uniqueIncludedDatesToMeans.keySet());
        Collections.sort(allDatesSorted);

        // means "date": [1.2, 2.3, 1.3, 2.4, 0.9], ...
        {
            stringBuilder.append("  \"means\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final Double [] means = uniqueIncludedDatesToMeans.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(Arrays.toString(means)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        // medians "date": [1.2, 2.3, 1.3, 2.4, 0.9], ...
        {
            stringBuilder.append("  \"medians\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final Integer [] medians = uniqueIncludedDatesToMedians.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(Arrays.toString(medians)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        // first-quartiles "date": [1.2, 2.3, 1.3, 2.4, 0.9], ...
        {
            stringBuilder.append("  \"first-quartiles\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final Integer [] firstQuartiles = uniqueIncludedDatesTo1stQuartiles.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(Arrays.toString(firstQuartiles)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        // third-quartiles "date": [1.2, 2.3, 1.3, 2.4, 0.9], ...
        {
            stringBuilder.append("  \"third-quartiles\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final Integer [] thirdQuartiles = uniqueIncludedDatesTo3rdQuartiles.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(Arrays.toString(thirdQuartiles)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        // mins "date": [1234, 2333, 1333, 2433, 0933], ...
        {
            stringBuilder.append("  \"mins\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final Integer [] mins = uniqueIncludedDatesToMins.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(Arrays.toString(mins)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        // maxs "date": [1234, 2333, 1333, 2433, 0933], ...
        {
            stringBuilder.append("  \"maxs\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final Integer [] maxs = uniqueIncludedDatesToMaxs.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(Arrays.toString(maxs)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        // crudeOilPriceInUsd "date": 1.23, ...
        {
            stringBuilder.append("  \"crudeOilPricesInUsd\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final double value = datesToCrudeOilPriceInUsd.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(String.format("%.2f", value)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        // eurToUsd "date": 1.23, ...
        {
            stringBuilder.append("  \"eurToUsd\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final double value = datesToEurUsd.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(String.format("%.2f", value)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        // eurToGbp "date": 1.23, ...
        {
            stringBuilder.append("  \"eurToGbp\": {\n");
            int countDates = 0;
            for(final String dateS : allDatesSorted) {
                countDates++;
                final double value = datesToEurGbp.get(dateS);
                stringBuilder.append("    \"").append(dateS).append("\": ").append(String.format("%.2f", value)).append(countDates < allDatesSorted.size() ? ",\n" : "\n");
            }
            stringBuilder.append("  },\n");
        }

        final Set<String> allStations = stationsToFuelTypeToTimestampedPricesMap.keySet();
        // filter selected stations only
        allStations.retainAll(selectedStations);

        stringBuilder.append("  \"numOfStations\": ").append(allStations.size()).append(",\n");
        stringBuilder.append("  \"stations\": {\n");
        int count = 0;
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

        stringBuilder.append("  }\n");
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private static String toJsonArray(final Set<String> strings) {
        final Vector<String> sortedStrings = new Vector<>(strings);
        Collections.sort(sortedStrings);
        final StringBuilder stringBuilder = new StringBuilder("[");
        int counter = 0;
        for(final String s : sortedStrings) {
            counter++;
            stringBuilder.append("\"").append(s).append("\"").append(counter < sortedStrings.size() ? ", " : "]");
        }
        return stringBuilder.toString();
    }
}
