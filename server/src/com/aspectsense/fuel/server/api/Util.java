package com.aspectsense.fuel.server.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Util {

    private static final String CRUDE_OIL_PRICE_MEMCACHE_KEY = "LATEST_CRUDE_OIL_PRICE";
    private static final String CRUDE_OIL_PRICE_URL = "https://www.eia.gov/dnav/pet/hist_xls/RBRTEd.xls";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final Gson gson = new Gson();
    public static final Logger log = Logger.getLogger(Util.class.getCanonicalName());

    /**
     * Gets the latest crude oil prices. Retrieves cached value (if of current day) otherwise scrapes new value.
     * @return the latest crude oil prices
     */
    static Map<String,Double> getDateToCrudeOilPriceInUSD() {
        Map<String,Double> dateToCrudeOilPriceInUSD;

        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        final String json = (String) memcacheService.get(CRUDE_OIL_PRICE_MEMCACHE_KEY);
        MemcacheEntry memcacheEntry = gson.fromJson(json, MemcacheEntry.class);
        if(memcacheEntry == null || !memcacheEntry.isCurrent()) {
            // request new data from server
            dateToCrudeOilPriceInUSD = scrapeDateToCrudeOilPriceInUSD();
            // update memcache
            memcacheEntry = new MemcacheEntry(SIMPLE_DATE_FORMAT.format(new Date()), dateToCrudeOilPriceInUSD);
            memcacheService.put(CRUDE_OIL_PRICE_MEMCACHE_KEY, gson.toJson(memcacheEntry));
        } else {
            dateToCrudeOilPriceInUSD = memcacheEntry.getDateToCrudeOilPriceInUSD();
        }

        return dateToCrudeOilPriceInUSD;
    }

    private static class MemcacheEntry implements Serializable {
        private final String date;
        private final Map<String,Double> dateToCrudeOilPriceInUSD;

        private MemcacheEntry(final String date, final Map<String, Double> dateToCrudeOilPriceInUSD) {
            this.date = date;
            this.dateToCrudeOilPriceInUSD = dateToCrudeOilPriceInUSD;
        }

        boolean isCurrent() { // returns true iff the creation date is today
            return SIMPLE_DATE_FORMAT.format(new Date()).equalsIgnoreCase(date);
        }

        public Map<String, Double> getDateToCrudeOilPriceInUSD() {
            return dateToCrudeOilPriceInUSD;
        }
    }

    static Map<String,Double> scrapeDateToCrudeOilPriceInUSD() {
        try {
            final Workbook workbook = doRequestXls(CRUDE_OIL_PRICE_URL);
            return scrapeCrudeOilPrices(workbook);
        } catch (final IOException e) {
            final String error = "scrapeCrudeOilPrices error -> " + e.getMessage();
            log.warning(error);
            return null;
        }
    }

    private static Workbook doRequestXls(final String url) throws IOException {
        final URL requestUrl = new URL(url);
        final HttpURLConnection httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Content-Type", "application/vnd.ms-excel");

        int responseCode = httpURLConnection.getResponseCode();
        if(responseCode != 200) {
            log.severe("RequestServlet @ '" + url + "' produced response code: " + responseCode);
            throw new IOException("HTTP (XML) response code: " + responseCode);
        }

        return getWorkbook(httpURLConnection.getInputStream());
    }

    private static Workbook getWorkbook(final InputStream inputStream) throws IOException {

        final WorkbookSettings workbookSettings = new WorkbookSettings();
        try {
            return Workbook.getWorkbook(inputStream, workbookSettings);
        } catch (BiffException biffe) {
            throw new IOException(biffe);
        }
    }

    private static Map<String,Double> scrapeCrudeOilPrices(final Workbook workbook) {
        final Sheet dataSheet = workbook.getSheet(1);
        final int numOfRows = dataSheet.getRows();
        // Europe Brent Spot Price FOB (Dollars per Barrel)
        final SimpleDateFormat xlsSimpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        final SimpleDateFormat mapSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Map<String,Double> dateToCrudeOilPriceInUSCents = new HashMap<>();
        for(int i = 3; i < numOfRows; i++) {
            try {
                final Date date = xlsSimpleDateFormat.parse(dataSheet.getCell(0, i).getContents());
                final double crudeOilPriceInUSD= Double.parseDouble(dataSheet.getCell(1, i).getContents());
                dateToCrudeOilPriceInUSCents.put(mapSimpleDateFormat.format(date), crudeOilPriceInUSD);
            } catch (ParseException pe) {
                break;
            }
        }
        return dateToCrudeOilPriceInUSCents;
    }
}
