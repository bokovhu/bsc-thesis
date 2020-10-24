package me.bokov.bsc.surfaceviewer.sdf;

import java.util.List;
import java.util.Locale;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionCallStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLRawStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Vector2f;

public class SDFCone implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final float angle;
    private final float height;
    private Vector2f C;
    private Vector2f Q;

    public SDFCone(float angle, float height) {
        this.angle = angle;
        this.height = height;
        C = new Vector2f(
                (float) Math.sin(angle),
                (float) Math.cos(angle)
        );
        Q = new Vector2f(C.x / C.y, -1.0f).mul(height);
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        return List.of(
                new GLSLVariableDeclarationStatement(
                        "float",
                        context.resultVariable(),
                        new GLSLFunctionCallStatement(
                                "CSG_DistanceToCone",
                                List.of(
                                        new GLSLRawStatement(context.getPointVariable()),
                                        new GLSLRawStatement(
                                                String.format(Locale.ENGLISH, "%.4f", angle)),
                                        new GLSLRawStatement(
                                                String.format(Locale.ENGLISH, "%.4f", height))
                                )
                        )
                )
        );
    }

    private float clamp(float v, float min, float max) {
        return v < min ? min : (v > max ? max : v);
    }

    @Override
    public float getAt(float x, float y, float z) {
        Vector2f w = new Vector2f(
                Vector2f.length(x, z),
                y
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

    @Override
    public String getKind() {
        return "SDFCone";
    }
}
