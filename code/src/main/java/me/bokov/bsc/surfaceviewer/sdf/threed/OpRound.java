package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpRound implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final float radius;
    private final Evaluable<Float, CPUContext, GPUContext> generator;

    public OpRound(
            float radius,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        this.radius = radius;
        this.generator = generator;
    }

    @Override
    public Float evaluate(CPUContext context) {
        return generator.cpu().evaluate(context) - radius;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        final var generatorContext = context.branch("0");
        List<GLSLStatement> generatorResult = generator.gpu().evaluate(generatorContext);
        List<GLSLStatement> result = new ArrayList<>();

        result.addAll(generatorResult);
        result.add(
                resultVar(
                        context,
                        opMinus(
                                ref(generatorContext.getResult()),
                                literal(radius)
                        )
                )
        );

        return result;
    }
}
