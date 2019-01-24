package com.alibaba.metrics.os.utils;

public class FormatUtils {


    public static float parseFloat(String usage) {
        float result = 0;
        try {
            result = Float.parseFloat(usage);
        } catch (NumberFormatException e) {
            // ignore
        }
        return result;
    }

    /**
     * 0.2535 -> 0.25
     */
    public static float formatFloat(float data) {
        return (float) (Math.round(data * 100) / 100.0);
    }

    /**
     * 0.2535 -> 0.25
     */
    public static float formatFloat(String usage) {
        return (float) (Math.round(Float.parseFloat(usage) * 100) / 100.0);
    }
}
