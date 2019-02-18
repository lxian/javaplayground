package com.playground.javacc.modelgen.compiler;

public class Utils {
    public static String capatilize(String s) {
        return String.valueOf(Character.toUpperCase(s.charAt(0))) + s.substring(1);
    }

    public static String getterName(String name) {
        return "get" + capatilize(name);
    }

    public static String setterName(String name) {
        return "set" + capatilize(name);
    }
}
