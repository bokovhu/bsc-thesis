package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.length;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;

import java.io.Serializable;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import org.joml.Vector3f;

public class Sphere implements CPUEvaluator<Float, Vector3f>, GLSLDistanceExpression3D,
        Serializable {

    private final float radius;

    public Sphere(float radius) {
        this.radius = radius;
    }

    @Override
    public Float evaluate(Vector3f p) {
        return p.length() - radius;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        return List.of(
                resultVar(
                        context, opMinus(length(ref(context.getPointVariable())), literal(radius)))
        );
    }
}
