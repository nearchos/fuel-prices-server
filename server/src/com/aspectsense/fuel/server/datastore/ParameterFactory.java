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

import com.aspectsense.fuel.server.admin.AdminSyncServlet;
import com.aspectsense.fuel.server.data.Parameter;
import com.google.appengine.api.datastore.*;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         01/01/2016
 *         16:29
 */
public class ParameterFactory {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    public static final String KIND = "Parameter";

    public static final String PROPERTY_NAME    = "name";
    public static final String PROPERTY_VALUE   = "value";

    static public boolean isMagic(final String code) {
        final Parameter parameter = getParameterByName(AdminSyncServlet.PARAMETER_MAGIC);
        return parameter != null && parameter.getParameterValue().equals(code);
    }

    static public Vector<Parameter> getAllParameters() {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_NAME);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Parameter> parameters = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            parameters.add(getFromEntity(entity));
        }

        return parameters;
    }

    static public Parameter getParameterByName(final String name) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_NAME, Query.FilterOperator.EQUAL, name);
        query.setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Vector<Parameter> parameters = new Vector<>();
        for(final Entity entity : preparedQuery.asIterable()) {
            parameters.add(getFromEntity(entity));
        }

        if(parameters.size() < 1) {
            return null;
        } else {
            return parameters.elementAt(0);
        }
    }

    static public String getParameterValueAsStringByName(final String name) {
        final Parameter parameter = getParameterByName(name);
        return parameter == null ? "" : parameter.getParameterValue();
    }

    static public Key addParameter(final String name, final String value) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity parameterEntity = new Entity(KIND);
        parameterEntity.setProperty(PROPERTY_NAME, name);
        parameterEntity.setProperty(PROPERTY_VALUE, value);

        return datastoreService.put(parameterEntity);
    }

    static public void editParameter(final String uuid, final String name, final String value) {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            final Entity parameterEntity = datastoreService.get(KeyFactory.stringToKey(uuid));
            parameterEntity.setProperty(PROPERTY_NAME, name);
            parameterEntity.setProperty(PROPERTY_VALUE, value);
            datastoreService.put(parameterEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + uuid);
        }
    }

    static public Parameter getFromEntity(final Entity entity) {
        return new Parameter(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_NAME),
                (String) entity.getProperty(PROPERTY_VALUE));
    }

}
