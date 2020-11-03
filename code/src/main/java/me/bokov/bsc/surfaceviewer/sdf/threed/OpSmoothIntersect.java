package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpSmoothIntersect implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final Evaluatable<Float, CPUContext, GPUContext> a;
    private final Evaluatable<Float, CPUContext, GPUContext> b;
    private final float k;

    public OpSmoothIntersect(
            Evaluatable<Float, CPUContext, GPUContext> a,
            Evaluatable<Float, CPUContext, GPUContext> b,
            float k
    ) {
        this.a = a;
        this.b = b;
        this.k = k;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        final GPUContext generatorAContext = context.branch("A");
        final GPUContext generatorBContext = context.branch("B");

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
                        opMinus(
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
                        opPlus(
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

    // TODO: Should be moved to a utility class
    private float _clamp(float v, float a, float b) {
        return v < a ? a : v > b ? b : v;
    }

    // TODO: Should be moved to a utility class
    private float _mix(float a, float b, float v) {
        return a * (1.0f - v) + b * v;
    }

    @Override
    public Float evaluate(CPUContext p) {
        final float v1 = a.cpu().evaluate(p);
        final float v2 = b.cpu().evaluate(p);

        float h = _clamp(
                0.5f - 0.5f * (v2 - v1) / k,
                0.0f, 1.0f
        );
        return _mix(v2, v1, h);
    }

}
