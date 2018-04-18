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

import com.aspectsense.fuel.server.model.DailySummary;
import com.google.gson.Gson;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         19/03/2017
 */
public class DailySummaryParser {

    public static DailySummary fromDailySummaryJson(final String json) {

        return new Gson().fromJson(json, DailySummary.class);
//        final JSONObject jsonObject = new JSONObject(json);
//
//        final double crudeOilPriceInUSD = jsonObject.has("crudeOilInUsd") ? jsonObject.getDouble("crudeOilInUsd") : 0d;
//        final double eurToUsd = jsonObject.has("eurUsd") ? jsonObject.getDouble("eurUsd") : 0d;
//        final double eurToGbp = jsonObject.has("eurGbp") ? jsonObject.getDouble("eurGbp") : 0d;
//
//        final Map<String, Integer[]> stationsToPricesMap = new HashMap<>();
//        final JSONObject stationsObject = jsonObject.has("stations") ? jsonObject.getJSONObject("stations") : jsonObject;
//        final Iterator iterator = stationsObject.keys();
//        while(iterator.hasNext()) {
//            final String station = iterator.next().toString();
//            final JSONArray pricesJsonArray = stationsObject.getJSONArray(station);
//            final Integer [] prices = new Integer[pricesJsonArray.length()];
//            for(int i = 0; i < prices.length; i++) {
//                prices[i] = pricesJsonArray.getInt(i);
//            }
//            stationsToPricesMap.put(station, prices);
//        }
//
//        return new DailySummary(crudeOilPriceInUSD, eurToUsd, eurToGbp, stationsToPricesMap);
    }
}
