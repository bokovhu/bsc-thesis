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

public class SDFTorus implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final Vector2f radius;

    public SDFTorus(Vector2f radius) {
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        return List.of(
                new GLSLVariableDeclarationStatement(
                        "vec2",
                        context.getContextId() + "_Q",
                        new GLSLFunctionCallStatement(
                                "vec2",
                                List.of(
                                        new GLSLBinaryExpressionStatement(
                                                new GLSLFunctionCallStatement(
                                                        "length",
                                                        List.of(
                                                                new GLSLMemberStatement(
                                                                        List.of(
                                                                                context.getPointVariable(),
                                                                                "xz"
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new GLSLRawStatement(
                                                        String.format(Locale.ENGLISH, "%.4f",
                                                                radius.x
                                                        )),
                                                "-"
                                        ),
                                        new GLSLMemberStatement(
                                                List.of(
                                                        context.getPointVariable(),
                                                        "y"
                                                )
                                        )
                                )
                        )
                ),
                new GLSLVariableDeclarationStatement(
                        "float",
                        context.resultVariable(),
                        new GLSLBinaryExpressionStatement(
                                new GLSLFunctionCallStatement(
                                        "length",
                                        List.of(
                                                new GLSLRawStatement(context.getContextId() + "_Q")
                                        )
                                ),
                                new GLSLRawStatement(
                                        String.format(Locale.ENGLISH, "%.4f", radius.y)),
                                "-"
                        )
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
