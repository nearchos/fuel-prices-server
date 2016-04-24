package com.aspectsense.fuel.server.data;

/**
 * @author Nearchos
 */
public class City {

    public static final City NICOSIA    = new City("Λευκωσία",      "Nicosia",      35.16932f,  33.36014f);
    public static final City LIMASSOL   = new City("Λεμεσός",       "Limassol",     34.679038f, 33.044171f);
    public static final City LARNACA    = new City("Λάρνακα",       "Larnaca",      34.9177f,   33.6319f);
    public static final City PAPHOS     = new City("Πάφος",         "Paphos",       34.75572f,  32.41542f);
    public static final City FAMAGUSTA  = new City("Αμμόχωστος",    "Famagusta",    35.1174f,   33.941f);

    public static final City [] ALL_CITIES = {NICOSIA, LIMASSOL, LARNACA, PAPHOS, FAMAGUSTA};

    private String nameEl;
    private String nameEn;
    private float lat;
    private float lng;

    private City(final String nameEl, final String nameEn, final float lat, final float lng) {
        this.nameEl = nameEl;
        this.nameEn = nameEn;
        this.lat = lat;
        this.lng = lng;
    }

    public String getNameEl() {
        return nameEl;
    }

    public String getNameEn() {
        return nameEn;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public static City decode(final String cityAsString) {
        if("Λευκωσία".equalsIgnoreCase(cityAsString) || "Λευκωσια".equalsIgnoreCase(cityAsString)) {
            return NICOSIA;
        } if("Λεμεσός".equalsIgnoreCase(cityAsString) || "Λεμεσος".equalsIgnoreCase(cityAsString)) {
            return LIMASSOL;
        } if("Λάρνακα".equalsIgnoreCase(cityAsString) || "Λαρνακα".equalsIgnoreCase(cityAsString)) {
            return LARNACA;
        } if("Πάφος".equalsIgnoreCase(cityAsString) || "Παφος".equalsIgnoreCase(cityAsString)) {
            return PAPHOS;
        } if("Αμμόχωστος".equalsIgnoreCase(cityAsString) || "Αμμοχωστος".equalsIgnoreCase(cityAsString)) {
            return FAMAGUSTA;
        } else {
            throw new RuntimeException("Unknown city: " + cityAsString);
        }
    }
}