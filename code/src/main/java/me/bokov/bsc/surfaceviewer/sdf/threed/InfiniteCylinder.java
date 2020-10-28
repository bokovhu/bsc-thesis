package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.length;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.vec2;

import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Vector2f;

public class InfiniteCylinder implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    private final Vector2f xzOffset;
    private final float radius;

    public InfiniteCylinder(Vector2f xzOffset, float radius) {
        this.xzOffset = xzOffset;
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
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
    public float getAt(float x, float y, float z) {
        return Vector2f.length(x - xzOffset.x, z - xzOffset.y) - radius;
    }

    @Override
    public String getKind() {
        return "SDFInfiniteCylinder";
    }
}
