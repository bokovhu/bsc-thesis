package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.length;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;

import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Vector3f;

public class Sphere implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    private final float radius;

    public Sphere(float radius) {
        this.radius = radius;
    }

    @Override
    public float getAt(float x, float y, float z) {
        return Vector3f.length(x, y, z) - radius;
    }

    @Override
    public String getKind() {
        return "SDFSphere";
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
