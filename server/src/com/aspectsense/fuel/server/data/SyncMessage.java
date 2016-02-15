package com.aspectsense.fuel.server.data;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis
 * 07-Feb-16
 */
public class SyncMessage implements Serializable {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    private final String uuid;
    private final String json;
    private final long numOfChanges;
    private final long lastUpdated;

    public SyncMessage(String uuid, String json, long numOfChanges, long lastUpdated) {
        this.uuid = uuid;
        this.json = json;
        this.numOfChanges = numOfChanges;
        this.lastUpdated = lastUpdated;
    }

    public String getUuid() {
        return uuid;
    }

    public String getJson() {
        return json;
    }

    public long getNumOfChanges() {
        return numOfChanges;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
