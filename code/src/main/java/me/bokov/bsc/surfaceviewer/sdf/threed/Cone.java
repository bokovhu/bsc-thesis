package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class Cone implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>, Serializable {

    private static final String GLSL_FN_NAME = "CSG_DistanceToCone";

    private final float angle;
    private final float height;
    private final Vector2f C;
    private final Vector2f Q;

    public Cone(float angle, float height) {
        this.angle = angle;
        this.height = height;
        C = new Vector2f(
                (float) Math.sin(angle),
                (float) Math.cos(angle)
        );
        Q = new Vector2f(C.x / C.y, -1.0f).mul(height);
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                fn(GLSL_FN_NAME, ref(context.getPointVariable()), literal(angle), literal(height))
        );
    }

    // TODO: Move to utility class
    private float clamp(float v, float min, float max) {
        return v < min ? min : (v > max ? max : v);
    }

    @Override
    public Float evaluate(CPUContext c) {
        final Vector3f p = c.getPoint();
        Vector2f w = new Vector2f(
                Vector2f.length(p.x, p.z),
                p.y
        );
        Vector2f a = new Vector2f(w)
                .sub(
                        new Vector2f(Q)
                                .mul(
                                        clamp(
                                                new Vector2f(w).dot(Q) / new Vector2f(Q).dot(Q),
                                                0.0f, 1.0f
                                        )
                                )
                );
        Vector2f b = new Vector2f(w)
                .sub(
                        new Vector2f(Q)
                                .mul(
                                        new Vector2f(
                                                clamp(w.x / Q.x, 0.0f, 1.0f),
                                                1.0f
                                        )
                                )
                );
        float k = Math.signum(Q.y);
        float d = Math.min(a.dot(a), b.dot(b));
        float s = Math.max(k * (w.x * Q.y - w.y * Q.x), k * (w.y - Q.y));

        return (float) Math.sqrt(d) * Math.signum(s);
    }

}
