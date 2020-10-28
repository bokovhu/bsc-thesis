package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.min;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector3f;

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
