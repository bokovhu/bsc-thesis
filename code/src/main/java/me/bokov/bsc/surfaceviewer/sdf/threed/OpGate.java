package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLIfStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpGate implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final Evaluable<Float, CPUContext, GPUContext> boundary;
    private final float threshold;
    private final Evaluable<Float, CPUContext, GPUContext> generator;

    public OpGate(
            Evaluable<Float, CPUContext, GPUContext> boundary,
            Evaluable<Float, CPUContext, GPUContext> generator,
            float threshold
    ) {
        this.boundary = boundary;
        this.generator = generator;
        this.threshold = threshold;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {

        GPUContext boundaryVolumeContext = context.branch("BoundingVolume");
        GPUContext generatorContext = context.branch("Generator");
        List<GLSLStatement> generatorStatements = new ArrayList<>(
                generator.gpu().evaluate(generatorContext)
        );
        generatorStatements.add(
                new GLSLBinaryExpressionStatement(
                        ref(context.getResult()),
                        ref(generatorContext.getResult()),
                        "="
                )
        );
        List<GLSLStatement> boundaryVolumeStatement = new ArrayList<>(
                boundary.gpu().evaluate(boundaryVolumeContext)
        );

        List<GLSLStatement> result = new ArrayList<>();
        result.addAll(boundaryVolumeStatement);
        result.add(
                var(
                        "float", context.getContextId() + "_BoundaryValue",
                        ref(boundaryVolumeContext.getResult())
                )
        );
        result.add(
                resultVar(
                        context,
                        ref(boundaryVolumeContext.getResult())
                )
        );
        result.add(
                new GLSLIfStatement(
                        cmpLt(ref(boundaryVolumeContext.getResult()), literal(threshold)),
                        generatorStatements,
                        block(
                                opAssign(ref(context.getResult()), ref(boundaryVolumeContext.getResult()))
                        )
                )
        );

        return result;
    }

    @Override
    public Float evaluate(CPUContext c) {

        float boundaryValue = boundary.cpu().evaluate(c);
        if (boundaryValue > threshold) {
            return boundaryValue;
        }

        return generator.cpu().evaluate(c);
    }

}
