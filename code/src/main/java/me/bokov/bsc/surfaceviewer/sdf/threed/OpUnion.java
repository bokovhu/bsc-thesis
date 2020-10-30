package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpUnion implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    protected final Evaluatable<Float, CPUContext, GPUContext> a;
    protected final Evaluatable<Float, CPUContext, GPUContext> b;

    public OpUnion(
            Evaluatable<Float, CPUContext, GPUContext> a,
            Evaluatable<Float, CPUContext, GPUContext> b
    ) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Float evaluate(CPUContext c) {
        return Math.min(a.cpu().evaluate(c), b.cpu().evaluate(c));
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

        result.add(
                resultVar(context, min(
                        ref(generatorAContext.getResult()),
                        ref(generatorBContext.getResult())
                ))
        );

        return result;
    }
}
