package com.aspectsense.fuel.server.data;

import java.io.Serializable;

public class DailyStatistics implements Serializable {

    private final String uuid;
    private final String json; // JSON-formatted message having the daily statistics for the given date
    private final String date; // e.g. 2017-03-17

    public DailyStatistics(final String uuid, final String json, final String date) {
        this.uuid = uuid;
        this.json = json;
        this.date = date;
    }

    public String getUuid() {
        return uuid;
    }

    public String getJson() {
        return json;
    }

    public String getDate() {
        return date;
    }
}