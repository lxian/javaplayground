package com.lxian.playground.json.mapper.field;

class Utils {

    public static String lowerCase(String s, int idx) {
        return s.substring(0, idx-1) + s.substring(idx, idx+1).toLowerCase() + s.substring(idx+1, s.length());
    }

    public static String upperCase(String s, int idx) {
        return s.substring(0, idx-1) + s.substring(idx, idx+1).toUpperCase() + s.substring(idx+1, s.length());
    }

    public static String capitalize(String s) {
        return upperCase(s, 0);
    }
}
