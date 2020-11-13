package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpSymX implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final Evaluable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpP = new Vector3f();

    public OpSymX(Evaluable<Float, CPUContext, GPUContext> generator) {
        this.generator = generator;
    }

    @Override
    public Float evaluate(CPUContext context) {
        return Math.min(
                generator.cpu().evaluate(context),
                generator.cpu()
                        .evaluate(context.transform(tmpP.set(
                                -1.0f * context.getPoint().x,
                                context.getPoint().y,
                                context.getPoint().z
                        )))
        );
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        final var gen1Context = context.branch("Orig");
        final var gen2Context = context.branch("Sym").transform("FlipX");
        List<GLSLStatement> gen1Result = generator.gpu().evaluate(gen1Context);
        List<GLSLStatement> gen2Result = generator.gpu().evaluate(gen2Context);
        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                var(
                        "vec3",
                        gen2Context.getPointVariable(),
                        vec3(
                                opMul(literal(-1.0f), ref(context.getPointVariable(), "x")),
                                ref(context.getPointVariable(), "y"),
                                ref(context.getPointVariable(), "z")
                        )
                )
        );
        result.addAll(gen1Result);
        result.addAll(gen2Result);
        result.add(
                resultVar(context, min(ref(gen1Context.getResult()), ref(gen2Context.getResult())))
        );

        return result;
    }
}
