package me.bokov.bsc.surfaceviewer.util;

import org.joml.Vector3f;

public final class MathUtil {

    private static final float EPSILON = 0.0001f;



    private static final Vector3f tmp1 = new Vector3f();
    private static final Vector3f tmp2 = new Vector3f();


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

    public static float interpolate(
            float a, float av,
            float b, float bv,
            float reference
    ) {

        float alpha = Math.abs((reference - av) / (Math.max(av, bv) - Math.min(av, bv)));
        return a + alpha * (b - a);
    }

}
