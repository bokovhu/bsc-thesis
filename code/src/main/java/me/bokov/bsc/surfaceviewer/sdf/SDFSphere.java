package me.bokov.bsc.surfaceviewer.sdf;

import java.util.List;
import java.util.Locale;
import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionCallStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLRawStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Vector3f;

public class SDFSphere implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final float radius;

    public SDFSphere(float radius) {
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
                new GLSLVariableDeclarationStatement(
                        "float",
                        context.resultVariable(),
                        new GLSLBinaryExpressionStatement(
                                new GLSLFunctionCallStatement(
                                        "length",
                                        List.of(new GLSLRawStatement(context.getPointVariable()))
                                ),
                                new GLSLRawStatement(String.format(Locale.ENGLISH, "%.4f", radius)),
                                "-"
                        )
                )
        );
    }
}
