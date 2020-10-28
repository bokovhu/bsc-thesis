package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.length;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
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

public class Torus implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    private final Vector2f radius;

    public Torus(Vector2f radius) {
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        final GLSLVariableDeclarationStatement q = var(
                "vec2", context.getContextId() + "_Q",
                vec2(
                        opMinus(length(ref(context.getPointVariable(), "xz")), literal(radius.x)),
                        ref(context.getPointVariable(), "y")
                )
        );
        return List.of(
                q,
                resultVar(
                        context,
                        opMinus(length(ref(q.name())), literal(radius.y))
                )
        );
    }

    @Override
    public float getAt(float x, float y, float z) {
        Vector2f q = new Vector2f(
                Vector2f.length(x, z) - radius.x,
                y
        );
        return q.length() - radius.y;
    }

    @Override
    public String getKind() {
        return "SDFTorus";
    }
}
