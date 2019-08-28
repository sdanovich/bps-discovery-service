package com.mastercard.labs.bps.discovery.util;

import org.apache.commons.codec.binary.Base32;

public class StringUtils {

    private static final Base32 BASE_32 = new Base32();

    public static String encode(String string) {

        return new String(BASE_32.encode(string.getBytes()));

    }


    public static String decode(String word) {
        return new String(BASE_32.decode(word.getBytes()));
    }
}
