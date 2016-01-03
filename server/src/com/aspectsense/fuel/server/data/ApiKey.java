package com.aspectsense.fuel.server.data;

import java.io.Serializable;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         22/12/2015
 *         21:14
 */
public class ApiKey implements Serializable {

    private final String uuid;
    private final String emailRequester;
    private final String note;
    private final long timeRequested;
    private final boolean isActive;
    private final String apiKeyCode;

    public ApiKey(final String uuid, final String emailRequester, final String note, final long timeRequested, final boolean isActive, final String apiKeyCode)
    {
        this.uuid = uuid;
        this.emailRequester = emailRequester;
        this.note = note;
        this.timeRequested = timeRequested;
        this.isActive = isActive;
        this.apiKeyCode = apiKeyCode;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEmailRequester() {
        return emailRequester;
    }

    public String getNote() { return note; }

    public long getTimeRequested() {
        return timeRequested;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getApiKeyCode() {
        return apiKeyCode;
    }
}