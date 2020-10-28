package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.abs;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.length;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.max;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.min;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opPlus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.vec2;

import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Vector2f;

public class CappedCylinder implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    private final float height, radius;

    public CappedCylinder(float height, float radius) {
        this.height = height;
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        final GLSLVariableDeclarationStatement d = var(
                "vec2", context.getContextId() + "_D", opMinus(
                        abs(vec2(
                                length(ref(context.getPointVariable(), "xz")),
                                ref(context.getPointVariable(), "y")
                        )),
                        vec2(radius, height)
                ));
        return List.of(
                d,
                resultVar(
                        context,
                        opPlus(
                                min(max(ref(d.name(), "x"), ref(d.name(), "y")), literal(0.0f)),
                                length(max(ref(d.name()), literal(0.0f)))
                        )
                )
        );
    }

    @Override
    public float getAt(float x, float y, float z) {
        Vector2f d = new Vector2f(
                Vector2f.length(x, z),
                y
        ).absolute().sub(radius, height);
        return (float) Math.min(0.0f, (float) Math.max(d.x, d.y)) + d.max(new Vector2f(0f, 0f))
                .length();
    }

    @Override
    public String getKind() {
        return "SDFCappedCylinder";
    }
}
