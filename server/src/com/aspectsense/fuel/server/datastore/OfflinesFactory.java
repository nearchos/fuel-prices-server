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

import com.aspectsense.fuel.server.data.Offlines;
import com.aspectsense.fuel.server.json.OfflinesParser;
import com.aspectsense.fuel.server.sync.PetroleumPriceDetail;
import com.google.appengine.api.datastore.*;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         04/01/2016
 *         20:12
 */
public class OfflinesFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");
    public static final String KIND = "Offlines";

    public static final String PROPERTY_JSON            = "json";
    public static final String PROPERTY_LAST_UPDATED    = "last_updated";

    public static void addOfflines(final Vector<PetroleumPriceDetail> petroleumPriceDetails, final long lastUpdated) {
        // add the Offlines to the datastore ONLY IF non empty
        if(!petroleumPriceDetails.isEmpty()) {
            final String json = OfflinesParser.toOfflinesJson(petroleumPriceDetails);
            final Text jsonText = new Text(json);
            addOfflines(jsonText, lastUpdated);
        }
    }

    /**
     * Returns the most recent {@link Offlines} instance.
     * @return the most recent {@link Offlines} instance, or null if that could not be found
     */
    static public Offlines getLatestOfflines() {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_LAST_UPDATED, Query.SortDirection.DESCENDING);
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

    static public Key addOfflines(Text json, long lastUpdated) {

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity offlinesEntity = new Entity(KIND);

        offlinesEntity.setProperty(PROPERTY_JSON, json);
        offlinesEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

        // storing in the datastore
        return datastoreService.put(offlinesEntity);
    }

    static public Offlines getFromEntity(final Entity entity) {
        return new Offlines(
                KeyFactory.keyToString(entity.getKey()),
                ((Text) entity.getProperty(PROPERTY_JSON)).getValue(),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED));
    }
}