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

package com.aspectsense.fuel.server.sync;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         18/12/2015
 *         12:19
 */
public class Util {

    public static final Logger log = Logger.getLogger("cyprusfuelguide");

    static Vector<PetroleumPriceDetail> parseXmlPollResponse(final String xml, final String fuelType) {

        Document document = parseXml(xml);

        final Vector<PetroleumPriceDetail> petroleumPriceDetails = new Vector<>();

        if(document != null) {
            final Node petroleumTypeNode = document.getElementsByTagName("PetroleumType").item(0);
            if(petroleumTypeNode == null) {
                log.warning("Parsed XML is not valid: " + xml);
                return petroleumPriceDetails;
            }
            NodeList nodeList = document.getElementsByTagName("PetroleumPriceDetails1");

            for (int i = 0; i < nodeList.getLength(); i++) {
                String fuelCompanyCode = null;
                String fuelCompanyName = null;
                String stationCode = null;
                String stationName = null;
                String stationTelNo = null;
                String stationCity = null;
                String stationDistrict = null;
                String stationAddress = null;
                String stationLatitude = null;
                String stationLongitude = null;
                String priceModificationDate = null;
                String fuelPrice = null;
                boolean isOffline = true;

                final Node node = nodeList.item(i);
                final NodeList children = node.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    final Node child = children.item(j);
                    final String name = child.getNodeName();
                    if ("FuelCompanyCode".equals(name)) {
                        fuelCompanyCode = child.getTextContent();
                    } else if ("fuel_company_name".equals(name)) {
                        fuelCompanyName = child.getTextContent();
                    } else if ("station_code".equals(name)) {
                        stationCode = child.getTextContent();
                    } else if ("station_name".equals(name)) {
                        stationName = child.getTextContent();
                    } else if ("station_tel_no".equals(name)) {
                        stationTelNo = child.getTextContent();
                    } else if ("station_city".equals(name)) {
                        stationCity = child.getTextContent();
                    } else if ("station_district".equals(name)) {
                        stationDistrict = child.getTextContent();
                    } else if ("station_address1".equals(name)) {
                        stationAddress = child.getTextContent();
                        if(stationAddress.contains("τηλ:")) {
                            stationAddress = stationAddress.substring(0, stationAddress.indexOf("τηλ:"));
                        }
                        stationAddress = stationAddress.trim();
                    } else if ("price_modification_date".equals(name)) {
                        priceModificationDate = child.getTextContent();
                    } else if ("map_coordinates".equals(name)) {
                        final String mapCoordinates = child.getTextContent();
                        if(mapCoordinates.contains("N")) {
                            String latS = mapCoordinates.substring(0, mapCoordinates.indexOf("N"));
                            String lngS = mapCoordinates.substring(mapCoordinates.indexOf("N")+ 2, mapCoordinates.indexOf("E"));
                            stationLatitude = convertToLatitudeOrLongitude(latS);
                            stationLongitude = convertToLatitudeOrLongitude(lngS);
                        } else {
                            stationLatitude = mapCoordinates.substring(0, mapCoordinates.indexOf(","));
                            stationLongitude = mapCoordinates.substring(mapCoordinates.indexOf(",") + 1);
                        }
                    } else if ("Fuel_Price".equals(name)) {
                        fuelPrice = child.getTextContent();
                    }

                }
                final PetroleumPriceDetail petroleumPriceDetail = new PetroleumPriceDetail(
                        fuelCompanyCode, fuelCompanyName, stationCode, stationName, stationTelNo, stationCity,
                        stationDistrict, stationAddress, stationLatitude, stationLongitude, priceModificationDate,
                        fuelType, fuelPrice, isOffline);

                petroleumPriceDetails.add(petroleumPriceDetail);
            }
        }

        return petroleumPriceDetails;
    }

    private static Document parseXml(String xml) {
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final StringReader reader = new StringReader(xml);
            final InputSource inputSource = new InputSource(reader);
            return documentBuilder.parse(inputSource);
        } catch (IOException ioe) {
            log.severe("I/O Error while parsing XML: " + ioe.getMessage());
        } catch (ParserConfigurationException pce) {
            log.severe("Parser Error while parsing XML: " + pce.getMessage());
        } catch (SAXException saxe) {
            log.severe("SAX Error while parsing XML: " + saxe.getMessage());
        }

        return null;
    }

    public static String convertToLatitudeOrLongitude(final String str) {
        final String degrees = str.substring(0, str.indexOf("°")).trim();
        final int d = Integer.parseInt(degrees);
        final String minutes = str.substring(str.indexOf("°") + 1, str.indexOf("'")).trim();
        int m = Integer.parseInt(minutes);
        final String seconds = str.substring(str.indexOf("'") + 1, str.indexOf("\"")).trim();
        float s = Float.parseFloat(seconds);

        return String.format("%9.6f", d + m / 60d + s / 3600d);
    }

//    public static int updateDatastore(final Vector<PetroleumPriceDetail> petroleumPriceDetails, final String fuelType, final boolean syncStations) {
//
//        final Map<String, Station> stationsByStationCode = StationFactory.getAllStationCodesToStations(0);
//        int numOfChanges = 0;
//
//        // update the data for offline stations
//        final long updateTimestamp = System.currentTimeMillis();
//
//        // sync stations, if needed (as indicated by syncStations boolean value)
//        if(syncStations) {
//            for(final PetroleumPriceDetail petroleumPriceDetail : petroleumPriceDetails) {
//                final String stationCode = petroleumPriceDetail.getStationCode();
//
//                final Station station = stationsByStationCode.get(stationCode);
//                if (station == null) { // new station added
//                    StationFactory.addStation(petroleumPriceDetail.getFuelCompanyCode(),
//                            petroleumPriceDetail.getFuelCompanyName(),
//                            petroleumPriceDetail.getStationCode(),
//                            petroleumPriceDetail.getStationName(),
//                            petroleumPriceDetail.getStationTelNo(),
//                            petroleumPriceDetail.getStationCity(),
//                            petroleumPriceDetail.getStationDistrict(),
//                            petroleumPriceDetail.getStationAddress(),
//                            petroleumPriceDetail.getStationLatitude(),
//                            petroleumPriceDetail.getStationLongitude(),
//                            updateTimestamp
//                    );
//                    numOfChanges++;
//                } else if (petroleumPriceDetail.hasChanges(station)) { // existing station was edited
//                    // update datastore entry of the station
//                    StationFactory.editStation(station.getUuid(),
//                            petroleumPriceDetail.getFuelCompanyCode(),
//                            petroleumPriceDetail.getFuelCompanyName(),
//                            petroleumPriceDetail.getStationCode(),
//                            petroleumPriceDetail.getStationName(),
//                            petroleumPriceDetail.getStationTelNo(),
//                            petroleumPriceDetail.getStationCity(),
//                            petroleumPriceDetail.getStationDistrict(),
//                            petroleumPriceDetail.getStationAddress(),
//                            petroleumPriceDetail.getStationLatitude(),
//                            petroleumPriceDetail.getStationLongitude(),
//                            updateTimestamp
//                    );
//                    numOfChanges++;
//                }
//            }
//        }
//
//        // sync prices
////        final Prices prices =
//        PricesFactory.addPrices(petroleumPriceDetails, fuelType);
//
//        OfflinesFactory.addOfflines(petroleumPriceDetails);
////        OfflineFactory.updateOfflines(petroleumPriceDetails, updateTimestamp);
//
//        return numOfChanges;
//    }
}