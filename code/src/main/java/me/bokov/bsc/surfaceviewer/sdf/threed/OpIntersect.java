package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.max;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import org.joml.Vector3f;

public class OpIntersect implements CPUEvaluator<Float, Vector3f>, GLSLDistanceExpression3D,
        Serializable {

    protected final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> a;
    protected final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> b;

    public OpIntersect(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> a,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> b
    ) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Float evaluate(Vector3f p) {
        return Math.max(
                a.cpu().evaluate(p),
                b.cpu().evaluate(p)
        );
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
                        ref(generatorAContext.getResult()),
                        ref(generatorBContext.getResult())
                ))
        );

        return result;
    }
}
