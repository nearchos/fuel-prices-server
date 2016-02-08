package com.aspectsense.fuel.server.json;

import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by Nearchos on 08-Feb-16.
 */
public class PricesParser {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static String toPricesJson(final Vector<PetroleumPriceDetail> petroleumPriceDetails) {

        final StringBuilder jsonStringBuilder = new StringBuilder("[\n");
        int count = 0;
        for(final PetroleumPriceDetail petroleumPriceDetail : petroleumPriceDetails) {
            String priceInMillieuros = "0";
            try {
                final Double price = Double.parseDouble(petroleumPriceDetail.getFuelPrice());
                priceInMillieuros = String.format("%5d", (int) (price * 1000)).trim();
            } catch (NumberFormatException nfe) {
                log.warning("Could not parse '" + petroleumPriceDetail.getFuelPrice() + "' to Double: " + nfe.getMessage());
            }
            boolean isLastElement = ++count == petroleumPriceDetails.size();
            jsonStringBuilder.append("  { \"stationCode\": \"").append(petroleumPriceDetail.getStationCode())
                    .append("\", \"price\": ").append(priceInMillieuros).append(" }")
                    .append(isLastElement ? "\n" : ",\n");
        }
        jsonStringBuilder.append("]\n");

        return jsonStringBuilder.toString();
    }
}