package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.max;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMul;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;

import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Vector3f;

public class OpSubtract implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    protected final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> a;
    protected final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> b;

    public OpSubtract(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> a,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> b
    ) {
        this.a = a;
        this.b = b;
    }

    @Override
    public float getAt(float x, float y, float z) {
        return (float) Math.max(-1.0f * a.cpu().evaluate(new Vector3f(x, y, z)), b.cpu().evaluate(new Vector3f(x, y, z)));
    }

    @Override
    public String getKind() {
        return "SDFOpSubtract";
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        final ExpressionEvaluationContext generatorAContext = context.branch("A");
        final ExpressionEvaluationContext generatorBContext = context.branch("B");

        final List<GLSLStatement> generatedA = a.gpu()
                .evaluate(generatorAContext);
        final List<GLSLStatement> generatedB = b.gpu()
                .evaluate(generatorBContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.addAll(generatedA);
        result.addAll(generatedB);

        result.add(
                resultVar(context, max(
                        opMul(literal(-1.0f), ref(generatorAContext.getResult())),
                        ref(generatorBContext.getResult())
                ))
        );

        return result;
    }
}
