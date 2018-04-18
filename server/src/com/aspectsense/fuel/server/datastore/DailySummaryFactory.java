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

import com.aspectsense.fuel.server.data.DailySummaryEntity;
import com.google.appengine.api.datastore.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         18/03/2017
 */
public class DailySummaryFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "DailySummaryEntity";

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
     * Returns the specified {@link DailySummaryEntity}.
     * @param dateS the date in the form of 2017-03-18 for which the {@link DailySummaryEntity} is to be retrieved.
     * @return the specified {@link DailySummaryEntity}, or null if that could not be found
     */
    static public DailySummaryEntity getDailySummary(final String dateS) {
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

    static public DailySummaryEntity getFromEntity(final Entity entity) {
        return new DailySummaryEntity(
                KeyFactory.keyToString(entity.getKey()),
                ((Text) entity.getProperty(PROPERTY_JSON)).getValue(),
                (String) entity.getProperty(PROPERTY_DATE));
    }
}