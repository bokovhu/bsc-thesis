package me.bokov.bsc.surfaceviewer.sdf;

import java.util.List;
import java.util.Locale;
import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionCallStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLMemberStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLRawStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Vector2f;

public class SDFInfiniteCylinder implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final Vector2f xzOffset;
    private final float radius;

    public SDFInfiniteCylinder(Vector2f xzOffset, float radius) {
        this.xzOffset = xzOffset;
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        return List.of(
                new GLSLVariableDeclarationStatement(
                        "float",
                        context.resultVariable(),
                        new GLSLBinaryExpressionStatement(
                                new GLSLFunctionCallStatement(
                                        "length",
                                        List.of(
                                                new GLSLBinaryExpressionStatement(
                                                        new GLSLMemberStatement(
                                                                List.of(
                                                                        context.getPointVariable(),
                                                                        "xz"
                                                                )
                                                        ),
                                                        new GLSLRawStatement(
                                                                String.format(Locale.ENGLISH,
                                                                        "vec2(%.4f, %.4f)",
                                                                        xzOffset.x, xzOffset.y
                                                                )),
                                                        "-"
                                                )
                                        )
                                ),
                                new GLSLRawStatement(String.format(Locale.ENGLISH, "%.4f", radius)),
                                "-"
                        )
                )
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
