package com.aspectsense.fuel.server.data;

import java.io.Serializable;

/**
 * @author Nearchos Paspallis
 * 03-Feb-16.
 */
public class Stations implements Serializable {

    private final String uuid;
    private final String json;
    private final long lastUpdated;

    public Stations(String uuid, String json, long lastUpdated) {
        this.uuid = uuid;
        this.json = json;
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public String getJson() {
        return json;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}