package com.aspectsense.fuel.server.datastore;

import com.aspectsense.fuel.server.data.DailyStatistics;
import com.aspectsense.fuel.server.data.DailySummary;
import com.google.appengine.api.datastore.*;

import java.util.Date;
import java.util.logging.Logger;

public class DailyStatisticsFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "DailyStatistics";

    public static final String PROPERTY_JSON = "json";
    public static final String PROPERTY_DATE = "date";

    public static Key addDailyStatistics(final String dateS, final String json) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity dailyStatisticsEntity = new Entity(KIND);

        final Text jsonText = new Text(json);
        dailyStatisticsEntity.setProperty(PROPERTY_JSON, jsonText);
        dailyStatisticsEntity.setProperty(PROPERTY_DATE, dateS);

        // storing in the datastore
        return datastoreService.put(dailyStatisticsEntity);
    }

    static public DailyStatistics getDailyStatistics() {
        return getDailyStatistics(DailySummaryFactory.SIMPLE_DATE_FORMAT.format(new Date()));
    }

    /**
     * Returns the specified {@link DailySummary}.
     * @param dateS the date (in the form of e.g. '2017-03-18') for which the {@link DailySummary} is to be retrieved.
     * @return the specified {@link DailySummary}, or null if that could not be found
     */
    static public DailyStatistics getDailyStatistics(final String dateS) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_DATE, Query.FilterOperator.EQUAL, dateS);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();
        // assert exactly one (or none) is found
        return getFromEntity(entity);
    }

    static public DailyStatistics getLatestDailyStatistics() {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND)
                .addSort(PROPERTY_DATE, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Entity entity = preparedQuery.asList(FetchOptions.Builder.withLimit(1)).get(0);
        // assert exactly one (or none) is found
        return getFromEntity(entity);
    }

    static public DailyStatistics getFromEntity(final Entity entity) {
        return new DailyStatistics(
                KeyFactory.keyToString(entity.getKey()),
                ((Text) entity.getProperty(PROPERTY_JSON)).getValue(),
                (String) entity.getProperty(PROPERTY_DATE));
    }
}