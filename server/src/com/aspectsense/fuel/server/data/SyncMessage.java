package com.aspectsense.fuel.server.data;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by Nearchos on 07-Feb-16.
 */
public class SyncMessage implements Serializable {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    private final String uuid;
    private final String json;
    private final long lastUpdated;

    public SyncMessage(String uuid, String json, long lastUpdated) {
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
