package com.main.java.task.Utils.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean IsNullOrEmpty(String val) {
        return ((val == null) || (val.trim().length() <= 0) || "null".equalsIgnoreCase(val.trim()));
    }

    public static boolean IsNullOrEmpty(Collection<?> s) {
        return ((s == null) || (s.size() <= 0));
    }

    public static boolean IsNullOrEmpty(Map<?, ?> s) {
        return ((s == null) || (s.size() <= 0));
    }

    public static boolean IsNullOrEmpty(Object[] s) {
        return ((s == null) || (s.length <= 0));
    }

    public static int getInteger(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (Exception t) {
            return defaultValue;
        }
    }

    public static String getJsonString(Object data) {
        Gson gson = new Gson();
        return gson.toJson(data);
    }

    public static JsonElement getJsonObject(Object data) {
        Gson gson = new Gson();
        return gson.toJsonTree(data);
    }

    public static String getLikeSearchString(String input, boolean isNumOnly) {
        if (!IsNullOrEmpty(input)) {
            String res = input.trim().replaceAll(Pattern.quote("*"), "%");
            if (isNumOnly) {
                res = res.replaceAll("[^0-9%]", "");
            }
            if (!IsNullOrEmpty(res) && !res.contains("%")) {
                res = "%" + res + "%";
            }
            return res;
        }
        return input;
    }

    public static String getHostOrIp() {
        String res = "anon";
        try {
            InetAddress address = InetAddress.getLocalHost();
            String hName = address.getHostName();
            if (IsNullOrEmpty(hName)) {
                hName = address.getHostAddress();
                if (!IsNullOrEmpty(hName))
                    res = hName;
            } else {
                res = hName;
            }
        } catch (Exception e) {

        }
        return res;
    }

    public static String trimLast(String val, String delim) {
        if (!IsNullOrEmpty(val)) {
            if (val.endsWith(delim)) {
                return val.substring(0, val.length() - delim.length());
            }
        }
        return val;
    }

    public static String leftPad(String sourceString, int length, char padChar) {
        if (IsNullOrEmpty(sourceString))
            sourceString = "";
        if (length > 0) {
            while (sourceString.length() < length) {
                sourceString = padChar + sourceString;
            }
        }
        return sourceString;
    }
}
