package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpTranslateTo implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final Vector3f position;
    private final Evaluatable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpP = new Vector3f();

    public OpTranslateTo(
            Vector3f position,
            Evaluatable<Float, CPUContext, GPUContext> generator
    ) {
        this.position = position;
        this.generator = generator;
    }


    @Override
    public Float evaluate(CPUContext c) {
        return generator.cpu().evaluate(
                c.transform(
                        tmpP.set(c.getPoint()).sub(position)
                )
        );
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {

        final GPUContext generatorContext = context.branch("0")
                .transform("Translated");
        final List<GLSLStatement> generated = generator.gpu()
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                var(
                        "vec3", generatorContext.getPointVariable(),
                        opMinus(ref(context.getPointVariable()), vec3(position))
                )
        );
        result.addAll(generated);
        result.add(
                resultVar(context, ref(generatorContext.getResult()))
        );

        return result;
    }
}
