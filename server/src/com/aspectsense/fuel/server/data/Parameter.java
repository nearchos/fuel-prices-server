package com.aspectsense.fuel.server.data;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.Serializable;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         26/12/2015
 *         22:59
 */
public class Parameter implements Serializable {

    private final String uuid;
    private final String name;
    private final String value;

    public Parameter(final String uuid, final String name, final String value) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public String getParameterName() {
        return name;
    }

    public String getParameterValue() {
        return value;
    }

}