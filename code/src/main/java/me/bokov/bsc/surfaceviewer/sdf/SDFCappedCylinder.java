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

public class SDFCappedCylinder implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final float height, radius;

    public SDFCappedCylinder(float height, float radius) {
        this.height = height;
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        return List.of(
                new GLSLVariableDeclarationStatement(
                        "vec2",
                        context.getContextId() + "_D",
                        new GLSLBinaryExpressionStatement(
                                new GLSLFunctionCallStatement(
                                        "abs",
                                        List.of(
                                                new GLSLFunctionCallStatement(
                                                        "vec2",
                                                        List.of(
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
                                                                new GLSLMemberStatement(List.of(
                                                                        context.getPointVariable(),
                                                                        "y"
                                                                ))
                                                        )
                                                )
                                        )
                                ),
                                new GLSLFunctionCallStatement(
                                        "vec2",
                                        List.of(
                                                new GLSLRawStatement(
                                                        String.format(Locale.ENGLISH, "%.4f",
                                                                radius
                                                        )),
                                                new GLSLRawStatement(
                                                        String.format(Locale.ENGLISH, "%.4f",
                                                                height
                                                        ))
                                        )
                                ),
                                "-"
                        )
                ),
                new GLSLVariableDeclarationStatement(
                        "float",
                        context.resultVariable(),
                        new GLSLBinaryExpressionStatement(
                                new GLSLFunctionCallStatement(
                                        "min",
                                        List.of(
                                                new GLSLFunctionCallStatement(
                                                        "max",
                                                        List.of(
                                                                new GLSLMemberStatement(List.of(
                                                                        context.getContextId()
                                                                                + "_D",
                                                                        "x"
                                                                )),
                                                                new GLSLMemberStatement(List.of(
                                                                        context.getContextId()
                                                                                + "_D",
                                                                        "y"
                                                                ))
                                                        )
                                                ),
                                                new GLSLRawStatement("0.0")
                                        )
                                ),
                                new GLSLFunctionCallStatement(
                                        "length",
                                        List.of(
                                                new GLSLFunctionCallStatement(
                                                        "max",
                                                        List.of(
                                                                new GLSLRawStatement(
                                                                        context.getContextId()
                                                                                + "_D"),
                                                                new GLSLRawStatement("0.0")
                                                        )
                                                )
                                        )
                                ),
                                "+"
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
