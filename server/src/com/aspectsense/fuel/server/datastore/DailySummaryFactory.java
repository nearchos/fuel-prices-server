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

package com.aspectsense.fuel.server.datastore;

import com.aspectsense.fuel.server.data.DailySummary;
import com.google.appengine.api.datastore.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         18/03/2017
 */
public class DailySummaryFactory {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "DailySummary";

    public static final String PROPERTY_JSON        = "json";
    public static final String PROPERTY_DATE        = "date";

    public static Key addDailySummary(final String dateS, final String json) {

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity dailySummaryEntity = new Entity(KIND);

        final Text jsonText = new Text(json);
        dailySummaryEntity.setProperty(PROPERTY_JSON, jsonText);
        dailySummaryEntity.setProperty(PROPERTY_DATE, dateS);

        // storing in the datastore
        return datastoreService.put(dailySummaryEntity);
    }

    /**
     * Returns the specified {@link DailySummary}.
     * @param dateS the date (in the form of e.g. '2017-03-18') for which the {@link DailySummary} is to be retrieved.
     * @return the specified {@link DailySummary}, or null if that could not be found
     */
    static public DailySummary getDailySummary(final String dateS) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_DATE, Query.FilterOperator.EQUAL, dateS);
        final Query query = new Query(KIND).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // assert exactly one (or none) is found
        final FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1);
        final List<Entity> list = preparedQuery.asList(fetchOptions);
        if(!list.isEmpty()) {
            return getFromEntity(list.get(0));
        } else {
            return null;
        }
    }

    /**
     * Returns the {@link DailySummary} for the given date, or if not available, the latest one before that date.
     * @param dateS the date (in the form of e.g. '2017-03-18') for which the {@link DailySummary} is to be retrieved.
     * @return the specified {@link DailySummary}, or null if none exists fot that or any earlier date
     */
    static public DailySummary getLatestDailySummaryOnOrBeforeBeforeDate(final String dateS) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_DATE, Query.FilterOperator.LESS_THAN_OR_EQUAL, dateS);
        final Query query = new Query(KIND).setFilter(filter).addSort(PROPERTY_DATE, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // assert exactly one (or none) is found
        final FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1);
        final List<Entity> list = preparedQuery.asList(fetchOptions);
        if(!list.isEmpty()) {
            return getFromEntity(list.get(0));
        } else {
            return null;
        }
    }

    /**
     * Returns a map of {@link Date}s to {@link DailySummary} for all days in the specified month.
     * @param fromS the starting date and year in the form 'yyyy-MM-dd'
     * @param toS the ending date and year in the form 'yyyy-MM-dd'
     * @return a map of {@link String}-formatted date (in the form 'yyyy-MM-dd') to {@link DailySummary}
     * for all days in the specified month
     */
    static public Vector<DailySummary> getSortedDailySummariesForMonth(final String fromS, final String toS) {
        final Vector<DailySummary> dailySummaries = new Vector<>();
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterFrom = new Query.FilterPredicate(PROPERTY_DATE, Query.FilterOperator.GREATER_THAN_OR_EQUAL, fromS);
        final Query.Filter filterTo = new Query.FilterPredicate(PROPERTY_DATE, Query.FilterOperator.LESS_THAN_OR_EQUAL, toS);
        final Query.CompositeFilter filterFromTo = Query.CompositeFilterOperator.and(filterFrom, filterTo);
        final Query query = new Query(KIND).setFilter(filterFromTo).addSort(PROPERTY_DATE, Query.SortDirection.ASCENDING);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        // assert exactly one (or none) is found
        for (final Entity entity : preparedQuery.asIterable()) {
            final DailySummary dailySummary = getFromEntity(entity);
            dailySummaries.add(dailySummary);
        }
        return dailySummaries;
    }

    static public DailySummary getFromEntity(final Entity entity) {
        return new DailySummary(
                KeyFactory.keyToString(entity.getKey()),
                ((Text) entity.getProperty(PROPERTY_JSON)).getValue(),
                (String) entity.getProperty(PROPERTY_DATE));
    }
}