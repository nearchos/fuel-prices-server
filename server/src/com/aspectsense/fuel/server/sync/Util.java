package com.aspectsense.fuel.server.sync;

import java.io.InputStream;
import java.util.Scanner;

/**
 * fuel-prices-server
 *
 * @author Nearchos Paspallis
 *         18/12/2015
 *         12:19
 */
public class Util {

    static String convertStreamToString(InputStream inputStream)
    {
        final Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() + "\n" : "";
    }
}
