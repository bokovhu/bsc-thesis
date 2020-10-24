package me.bokov.bsc.surfaceviewer.sdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionCallStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLMemberStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLRawStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SDFRotate implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final Quaternionf orientation;
    private final PerPointSDFGenerator generator;
    private final Vector3f tmp = new Vector3f();


    public SDFRotate(Quaternionf orientation,
            PerPointSDFGenerator generator
    ) {
        this.orientation = orientation;
        this.generator = generator;
    }

    @Override
    public float getAt(float x, float y, float z) {
        orientation.transform(x, y, z, tmp);
        return generator.getAt(tmp.x, tmp.y, tmp.z);
    }

    @Override
    public String getKind() {
        return "SDFRotate";
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        final ExpressionEvaluationContext generatorContext = new ExpressionEvaluationContext()
                .setParentStatement(context.getParentStatement())
                .setContextId(context.getContextId() + "_0")
                .setPointVariable(context.getContextId() + "_Rotated");
        final List<GLSLStatement> generated = ((GLSLDistanceExpression) generator)
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                new GLSLVariableDeclarationStatement(
                        "vec4",
                        context.getContextId() + "_Q",
                        new GLSLRawStatement(
                                String.format(Locale.ENGLISH, "vec4(%.4f, %.4f, %.4f, %.4f)",
                                        orientation.x, orientation.y, orientation.z, orientation.w
                                ))
                )
        );
        result.add(
                new GLSLVariableDeclarationStatement(
                        "vec3",
                        context.getContextId() + "_Rotated",
                        new GLSLBinaryExpressionStatement(
                                new GLSLRawStatement(context.getPointVariable()),
                                new GLSLBinaryExpressionStatement(
                                        new GLSLRawStatement("2.0"),
                                        new GLSLFunctionCallStatement(
                                                "cross",
                                                List.of(
                                                        new GLSLMemberStatement(
                                                                List.of(
                                                                        context.getContextId()
                                                                                + "_Q",
                                                                        "xyz"
                                                                )
                                                        ),
                                                        new GLSLBinaryExpressionStatement(
                                                                new GLSLFunctionCallStatement(
                                                                        "cross",
                                                                        List.of(
                                                                                new GLSLMemberStatement(
                                                                                        List.of(
                                                                                                context.getContextId()
                                                                                                        + "_Q",
                                                                                                "xyz"
                                                                                        )
                                                                                ),
                                                                                new GLSLRawStatement(
                                                                                        context.getPointVariable())
                                                                        )
                                                                ),
                                                                new GLSLBinaryExpressionStatement(
                                                                        new GLSLMemberStatement(
                                                                                List.of(
                                                                                        context.getContextId()
                                                                                                + "_Q",
                                                                                        "w"
                                                                                )),
                                                                        new GLSLRawStatement(
                                                                                context.getPointVariable()),
                                                                        "*"
                                                                ),
                                                                "+"
                                                        )
                                                )
                                        ),
                                        "*"
                                ),
                                "+"
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
