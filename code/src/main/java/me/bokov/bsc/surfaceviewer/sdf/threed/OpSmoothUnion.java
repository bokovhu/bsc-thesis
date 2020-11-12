package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import me.bokov.bsc.surfaceviewer.util.MathUtil;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpSmoothUnion implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    protected final Evaluable<Float, CPUContext, GPUContext> a;
    protected final Evaluable<Float, CPUContext, GPUContext> b;
    private final float k;

    public OpSmoothUnion(
            Evaluable<Float, CPUContext, GPUContext> a,
            Evaluable<Float, CPUContext, GPUContext> b,
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

    @Override
    public Float evaluate(CPUContext c) {
        final float v1 = a.cpu().evaluate(c);
        final float v2 = b.cpu().evaluate(c);

        float h = MathUtil.clamp(
                0.5f + 0.5f * (v2 - v1) / k,
                0.0f, 1.0f
        );
        return MathUtil.mix(v2, v1, h);
    }

}
