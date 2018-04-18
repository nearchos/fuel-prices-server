package com.aspectsense.fuel.server.model;

import java.util.Map;

public class FixrIoMessage {
    public String date; // e.g. "2018-04-17"
    public String base; // e.g. "EUR"
    public Map<String,Double> rates; // e.g. {"GBP":0.8628,"USD":1.2357}

    public FixrIoMessage() {
    }

    public FixrIoMessage(String date, String base, Map<String, Double> rates) {
        this.date = date;
        this.base = base;
        this.rates = rates;
    }

    public String getDate() {
        return date;
    }

    public String getBase() {
        return base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public Double getRate(final String currency) {
        return rates.get(currency);
    }
}