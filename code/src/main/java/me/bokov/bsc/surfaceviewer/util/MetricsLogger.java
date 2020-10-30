package me.bokov.bsc.surfaceviewer.util;

import java.util.*;

public class MetricsLogger {

    public static void logMetrics(
            String header,
            Map<String, Object> metrics
    ) {

        System.out.println("* " + header + " *");
        for (var e : metrics.entrySet()) {
            System.out.println("    " + e.getKey() + " --> " + e.getValue());
        }

    }

}
