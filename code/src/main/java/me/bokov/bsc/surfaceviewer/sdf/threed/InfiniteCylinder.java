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

public class InfiniteCylinder implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final Vector2f xzOffset;
    private final float radius;

    public InfiniteCylinder(Vector2f xzOffset, float radius) {
        this.xzOffset = xzOffset;
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                resultVar(
                        context, opMinus(
                                length(opMinus(
                                        ref(context.getPointVariable(), "xz"), vec2(xzOffset))),
                                literal(radius)
                        ))
        );
    }

    @Override
    public Float evaluate(CPUContext c) {
        final Vector3f p = c.getPoint();
        return Vector2f.length(p.x - xzOffset.x, p.z - xzOffset.y) - radius;
    }

}
