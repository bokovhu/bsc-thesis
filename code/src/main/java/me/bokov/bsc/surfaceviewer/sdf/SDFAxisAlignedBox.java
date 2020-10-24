package me.bokov.bsc.surfaceviewer.sdf;

import java.util.List;
import java.util.Locale;
import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionCallStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLMemberStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLRawStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import org.joml.Vector3f;

public class SDFAxisAlignedBox implements PerPointSDFGenerator, GLSLDistanceExpression {

    private final Vector3f dimensions;

    private final Vector3f tempQ = new Vector3f();
    private final Vector3f tempLen = new Vector3f();
    private final Vector3f v3Zero = new Vector3f(0f, 0f, 0f);

    public SDFAxisAlignedBox(Vector3f dims) {
        this.dimensions = dims;
    }

    @Override
    public List<GLSLStatement> evaluate(
            ExpressionEvaluationContext context
    ) {
        return List.of(
                new GLSLVariableDeclarationStatement(
                        "vec3",
                        context.getContextId() + "_q",
                        new GLSLBinaryExpressionStatement(
                                new GLSLFunctionCallStatement(
                                        "abs",
                                        List.of(
                                                new GLSLMemberStatement(
                                                        List.of(context.getPointVariable())
                                                )
                                        )
                                ),
                                new GLSLRawStatement(
                                        String.format(Locale.ENGLISH, "vec3(%.4f, %.4f, %.4f)",
                                                dimensions.x, dimensions.y, dimensions.z
                                        )),
                                "-"
                        )
                ),
                new GLSLVariableDeclarationStatement(
                        "float",
                        context.resultVariable(),
                        new GLSLBinaryExpressionStatement(
                                new GLSLFunctionCallStatement(
                                        "length",
                                        List.of(
                                                new GLSLFunctionCallStatement(
                                                        "max",
                                                        List.of(
                                                                new GLSLMemberStatement(
                                                                        List.of(context
                                                                                .getContextId()
                                                                                + "_q")),
                                                                new GLSLRawStatement("0.0")
                                                        )
                                                )
                                        )
                                ),
                                new GLSLFunctionCallStatement(
                                        "min",
                                        List.of(
                                                new GLSLFunctionCallStatement(
                                                        "max",
                                                        List.of(
                                                                new GLSLMemberStatement(
                                                                        List.of(context
                                                                                .getContextId()
                                                                                + "_q", "x")),
                                                                new GLSLFunctionCallStatement(
                                                                        "max",
                                                                        List.of(
                                                                                new GLSLMemberStatement(
                                                                                        List.of(
                                                                                                context.getContextId()
                                                                                                        + "_q",
                                                                                                "y"
                                                                                        )),
                                                                                new GLSLMemberStatement(
                                                                                        List.of(
                                                                                                context.getContextId()
                                                                                                        + "_q",
                                                                                                "z"
                                                                                        ))
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new GLSLRawStatement("0.0")
                                        )
                                ),
                                "+"
                        )
                )
        );
    }

    @Override
    public float getAt(float x, float y, float z) {
        tempQ.set(x, y, z).absolute()
                .sub(dimensions);

        float part1 = tempLen.set(tempQ).max(v3Zero).length();
        float part2 = (float) Math.min(Math.max(tempQ.x, Math.max(tempQ.y, tempQ.z)), 0.0f);

        return part1 + part2;
    }

    @Override
    public String getKind() {
        return null;
    }
}
