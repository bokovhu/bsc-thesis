package me.bokov.bsc.surfaceviewer.sdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionCallStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLRawStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Vector3f;

public class SDFInifiniteRepetition implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final Vector3f period;
    private final PerPointSDFGenerator generator;

    public SDFInifiniteRepetition(Vector3f period,
            PerPointSDFGenerator generator
    ) {
        this.period = period;
        this.generator = generator;
    }

    @Override
    public float getAt(float x, float y, float z) {
        return this.generator.getAt(
                ((x + 0.5f * period.x) % period.x) - 0.5f * period.x,
                ((y + 0.5f * period.y) % period.y) - 0.5f * period.y,
                ((z + 0.5f * period.z) % period.z) - 0.5f * period.z
        );
    }

    @Override
    public String getKind() {
        return "SDFInfiniteRepetition";
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {

        final ExpressionEvaluationContext generatorContext = new ExpressionEvaluationContext()
                .setParentStatement(context.getParentStatement())
                .setContextId(context.getContextId() + "_0")
                .setPointVariable(context.getContextId() + "_InfinitelyRepeated");
        final List<GLSLStatement> generated = ((GLSLDistanceExpression) generator)
                .evaluate(generatorContext);

        final String c = String
                .format(Locale.ENGLISH, "vec3(%.4f, %.4f, %.4f)", period.x, period.y, period.z);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                new GLSLVariableDeclarationStatement(
                        "vec3",
                        generatorContext.getPointVariable(),
                        new GLSLBinaryExpressionStatement(
                                new GLSLFunctionCallStatement(
                                        "mod",
                                        List.of(
                                                new GLSLBinaryExpressionStatement(
                                                        new GLSLRawStatement(
                                                                context.getPointVariable()),
                                                        new GLSLBinaryExpressionStatement(
                                                                new GLSLRawStatement("0.5"),
                                                                new GLSLRawStatement(c),
                                                                "*"
                                                        ),
                                                        "+"
                                                ),
                                                new GLSLRawStatement(c)
                                        )
                                ),
                                new GLSLBinaryExpressionStatement(
                                        new GLSLRawStatement("0.5"),
                                        new GLSLRawStatement(c),
                                        "*"
                                ),
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
