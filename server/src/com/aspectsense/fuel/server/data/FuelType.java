package com.aspectsense.fuel.server.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nearchos
 * 03-Feb-16.
 */
public enum FuelType {

    UNLEADED_95(1, "unleaded 95"),
    UNLEADED_98(2, "unleaded 98"),
    DIESEL(3, "diesel"),
    HEATING(4, "heating"),
    KEROSENE(5, "kerosene");

    public static final FuelType [] ALL_FUEL_TYPES = new FuelType[] {
            UNLEADED_95,
            UNLEADED_98,
            DIESEL,
            HEATING,
            KEROSENE
    };

    private final int code;
    private final String name;

    FuelType(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getCodeAsString() {
        return Integer.toString(code);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FuelType{code=" + code + ", name='" + name + "\'}";
    }

    public static Map<Integer,FuelType> getCodeToFuelTypeMap() {
        final Map<Integer,FuelType> codeToFuelTypeMap = new HashMap<>();
        for(final FuelType fuelType : ALL_FUEL_TYPES) {
            codeToFuelTypeMap.put(fuelType.code, fuelType);
        }
        return codeToFuelTypeMap;
    }
}