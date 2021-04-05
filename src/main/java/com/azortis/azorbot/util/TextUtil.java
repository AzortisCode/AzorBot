package com.azortis.azorbot.util;

public class TextUtil {
    /**
     * Half a blank tab. Not removed by string strips
     */
    public static final String tab = " ";
    /**
     * Blank character. Not removed by string strips
     */
    public static final String bnk = "\u200b";
    /**
     * Capitalize the first letter of
     * @param str this string and
     * @return the capitalized string
     */
    public static String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
