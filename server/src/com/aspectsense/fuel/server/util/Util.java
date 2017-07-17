package com.aspectsense.fuel.server.util;

import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Nearchos Paspallis
 * 19-May-17
 */
public class Util {

    public static String convertStreamToString(InputStream inputStream) {
        final Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() + "\n" : "";
    }
}
