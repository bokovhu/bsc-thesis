package me.bokov.bsc.surfaceviewer.util;

import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import org.joml.Vector3f;

public final class MathUtil {

    private static final float EPSILON = 0.0001f;


    private static final Vector3f dxPlus = new Vector3f();
    private static final Vector3f dxMinus = new Vector3f();
    private static final Vector3f dyPlus = new Vector3f();
    private static final Vector3f dyMinus = new Vector3f();
    private static final Vector3f dzPlus = new Vector3f();
    private static final Vector3f dzMinus = new Vector3f();

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

    public static Vector3f sdfNormal(
            CPUEvaluator<Float, CPUContext> generator,
            CPUContext context,
            Vector3f dest
    ) {

        final var p = context.getPoint();

        if (dest == null) {
            dest = new Vector3f();
        }

        dest.set(
                generator.evaluate(context.transform(dxPlus.set(p).add(EPSILON, 0f, 0f))) - generator
                        .evaluate(context.transform(dxMinus.set(p).add(-EPSILON, 0f, 0f))),
                generator.evaluate(context.transform(dyPlus.set(p).add(0f, EPSILON, 0f))) - generator
                        .evaluate(context.transform(dyMinus.set(p).add(0f, -EPSILON, 0f))),
                generator.evaluate(context.transform(dzPlus.set(p).add(0f, 0f, EPSILON))) - generator
                        .evaluate(context.transform(dzMinus.set(p).add(0f, 0f, -EPSILON)))
        );

        return dest;

    }

}
