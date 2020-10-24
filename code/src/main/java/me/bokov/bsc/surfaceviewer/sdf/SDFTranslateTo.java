package me.bokov.bsc.surfaceviewer.sdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLRawStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Vector3f;

public class SDFTranslateTo implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final Vector3f position;
    private final PerPointSDFGenerator generator;

    public SDFTranslateTo(Vector3f position, PerPointSDFGenerator generator) {
        this.position = position;
        this.generator = generator;
    }


    @Override
    public float getAt(float x, float y, float z) {
        return generator.getAt(x - position.x, y - position.y, z - position.z);
    }

    @Override
    public String getKind() {
        return "SDFTranslateTo";
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context) {

        final ExpressionEvaluationContext generatorContext = new ExpressionEvaluationContext()
                .setParentStatement(context.getParentStatement())
                .setContextId(context.getContextId() + "_0")
                .setPointVariable(context.getContextId() + "_Translated");
        final List<GLSLStatement> generated = ((GLSLDistanceExpression) generator)
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                new GLSLVariableDeclarationStatement(
                        "vec3",
                        context.getContextId() + "_Translated",
                        new GLSLBinaryExpressionStatement(
                                new GLSLRawStatement(context.getPointVariable()),
                                new GLSLRawStatement(
                                        String.format(Locale.ENGLISH, "vec3(%.4f, %.4f, %.4f)",
                                                position.x, position.y, position.z
                                        )),
                                "-"
                        )
                )
        );
        result.addAll(generated);
        result.add(
                new GLSLVariableDeclarationStatement(
                        "float",
                        context.resultVariable(),
                        new GLSLRawStatement(generatorContext.resultVariable())
                )
        );

        return result;
    }
}
