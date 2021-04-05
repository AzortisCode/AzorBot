package com.azortis.azorbot.util;

public class TextUtil {
    /**
     * Capitalize the first letter of
     * @param str this string and
     * @return the capitalized string
     */
    public static String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
