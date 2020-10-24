package me.bokov.bsc.surfaceviewer.sdf;

import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLFunctionCallStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLRawStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;

public class SDFOpUnion implements PerPointSDFGenerator, GLSLDistanceExpression {

    protected final PerPointSDFGenerator a;
    protected final PerPointSDFGenerator b;

    public SDFOpUnion(PerPointSDFGenerator a, PerPointSDFGenerator b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public float getAt(float x, float y, float z) {
        return (float) Math.min(a.getAt(x, y, z), b.getAt(x, y, z));
    }

    @Override
    public String getKind() {
        return "SDFOpUnion";
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {

        final ExpressionEvaluationContext generatorAContext = new ExpressionEvaluationContext()
                .setParentStatement(context.getParentStatement())
                .setContextId(context.getContextId() + "_0")
                .setPointVariable(context.getPointVariable());
        final ExpressionEvaluationContext generatorBContext = new ExpressionEvaluationContext()
                .setParentStatement(context.getParentStatement())
                .setContextId(context.getContextId() + "_1")
                .setPointVariable(context.getPointVariable());

        final List<GLSLStatement> generatedA = ((GLSLDistanceExpression) a)
                .evaluate(generatorAContext);
        final List<GLSLStatement> generatedB = ((GLSLDistanceExpression) b)
                .evaluate(generatorBContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.addAll(generatedA);
        result.addAll(generatedB);

        result.add(
                new GLSLVariableDeclarationStatement(
                        "float",
                        context.resultVariable(),
                        new GLSLFunctionCallStatement(
                                "min",
                                List.of(
                                        new GLSLRawStatement(generatorAContext.resultVariable()),
                                        new GLSLRawStatement(generatorBContext.resultVariable())
                                )
                        )
                )
        );

        return result;
    }
}
