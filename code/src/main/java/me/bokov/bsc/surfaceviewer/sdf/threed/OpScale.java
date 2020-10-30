package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpScale implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final float scale;
    private final Evaluatable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpP = new Vector3f();

    public OpScale(
            float scale, Evaluatable<Float, CPUContext, GPUContext> generator
    ) {
        this.scale = scale;
        this.generator = generator;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        final GPUContext generatorContext = context.branch("0")
                .transform("Scaled");
        final List<GLSLStatement> generated = generator.gpu()
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                var(
                        "vec3", generatorContext.getPointVariable(),
                        opDiv(ref(context.getPointVariable()), literal(scale))
                )
        );
        result.addAll(generated);
        result.add(
                resultVar(context, opMul(ref(generatorContext.getResult()), literal(scale)))
        );

        return result;
    }

    @Override
    public Float evaluate(CPUContext c) {
        return generator.cpu().evaluate(c.transform(tmpP.set(c.getPoint()).div(scale))) * scale;
    }

}
