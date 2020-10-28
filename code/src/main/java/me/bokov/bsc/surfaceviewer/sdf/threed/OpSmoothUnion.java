package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.clamp;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.mix;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opDiv;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMul;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opPlus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.paren;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;

import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Vector3f;

public class OpSmoothUnion implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    protected final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> a;
    protected final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> b;
    private final float k;

    public OpSmoothUnion(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> a,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> b,
            float k
    ) {
        this.a = a;
        this.b = b;
        this.k = k;
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

        final GLSLVariableDeclarationStatement h = var(
                "float", context.getContextId() + "_H",
                clamp(
                        opPlus(
                                literal(0.5f),
                                opMul(
                                        literal(0.5f),
                                        opDiv(
                                                paren(
                                                        opMinus(
                                                                ref(generatorBContext
                                                                        .getResult()),
                                                                ref(generatorAContext
                                                                        .getResult())
                                                        )
                                                ),
                                                literal(k)
                                        )
                                )
                        ),
                        literal(0.0f), literal(1.0f)
                )
        );

        result.add(h);
        result.add(
                resultVar(
                        context,
                        opMinus(
                                mix(
                                        ref(generatorBContext
                                                .getResult()),
                                        ref(generatorAContext
                                                .getResult()),
                                        ref(h.name())
                                ),
                                opMul(
                                        literal(k),
                                        opMul(
                                                ref(h.name()),
                                                paren(
                                                        opMinus(literal(1.0f), ref(h.name()))
                                                )
                                        )
                                )
                        )
                )
        );

        return result;
    }

    private float _clamp(float v, float a, float b) {
        return v < a ? a : v > b ? b : v;
    }

    private float _mix(float a, float b, float v) {
        return a * (1.0f - v) + b * v;
    }

    @Override
    public float getAt(float x, float y, float z) {
        final float v1 = a.cpu().evaluate(new Vector3f(x, y, z));
        final float v2 = b.cpu().evaluate(new Vector3f(x, y, z));

        float h = _clamp(
                0.5f + 0.5f * (v2 - v1) / k,
                0.0f, 1.0f
        );
        return _mix(v2, v1, h);
    }

    @Override
    public String getKind() {
        return "SDFSmoothUnion";
    }
}
