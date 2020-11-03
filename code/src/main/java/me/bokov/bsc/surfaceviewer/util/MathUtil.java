package me.bokov.bsc.surfaceviewer.util;

public final class MathUtil {

    private MathUtil() {}

    public static float threshold(float v, float th, float out1, float out2) {
        return v <= th ? out1 : out2;
    }

    public static float clamp(float v, float a, float b) {
        return v < a ? a : v > b ? b : v;
    }

    public static float mix(float a, float b, float v) {
        return a * (1.0f - v) + b * v;
    }
}
