package com.library.util;

import java.util.Collection;

/** Converts Java lists into JavaScript array text for Chart.js on the dashboard. */
public class ChartUtil {
    public static String labels(Collection<String> items) {
        StringBuilder sb = new StringBuilder("[");
        for (String s : items) {
            if (sb.length() > 1) sb.append(",");
            sb.append("\"").append(s.replace("\\", "\\\\").replace("\"", "\\\"").replace("<", "\\u003c")).append("\"");
        }
        return sb.append("]").toString();
    }
    public static String numbers(Collection<Integer> items) {
        StringBuilder sb = new StringBuilder("[");
        for (Integer n : items) {
            if (sb.length() > 1) sb.append(",");
            sb.append(n);
        }
        return sb.append("]").toString();
    }
}
