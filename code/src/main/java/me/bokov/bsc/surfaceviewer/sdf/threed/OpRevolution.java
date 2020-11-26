package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpRevolution implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final Evaluable<Float, CPUContext, GPUContext> generator;
    private final float o;

    private final Vector2f tmp = new Vector2f();

    public OpRevolution(Evaluable<Float, CPUContext, GPUContext> generator, float o) {
        this.generator = generator;
        this.o = o;
    }

    @Override
    public Float evaluate(CPUContext context) {
        tmp.set(
                Vector2f.length(context.getPoint().x, context.getPoint().z) - o,
                context.getPoint().y
        );
        return generator.cpu().evaluate(
                context.transform(
                        new Vector3f(tmp, 0.0f)
                )
        );
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        List<GLSLStatement> result = new ArrayList<>();
        final var generatorContext = context.transform("Revolution");

        result.add(
                var("vec3", generatorContext.getPointVariable(), vec3(
                        opMinus(
                                length(ref(context.getPointVariable(), "xz")),
                                literal(o)
                        ),
                        ref(context.getPointVariable(), "y"),
                        literal(0.0f)
                ))
        );
        result.addAll(generator.gpu().evaluate(generatorContext));
        result.add(resultVar(context, ref(generatorContext.getResult())));

        return result;
    }
}
