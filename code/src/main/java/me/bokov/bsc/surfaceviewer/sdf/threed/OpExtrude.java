package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpExtrude implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final float depth;
    private final Evaluable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpP = new Vector3f();
    private final Vector2f tmpW = new Vector2f();

    public OpExtrude(
            float depth,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        this.depth = depth;
        this.generator = generator;
    }

    @Override
    public Float evaluate(CPUContext context) {
        float d = generator.cpu().evaluate(
                context.transform(tmpP.set(context.getPoint().x, context.getPoint().y, 0.0f))
        );
        tmpW.set(
                d,
                Math.abs(context.getPoint().z) - depth
        );
        return Math.min(0.0f, Math.max(tmpW.x, tmpW.y)) + Vector2f.length(
                Math.max(tmpW.x, 0.0f),
                Math.max(tmpW.y, 0.0f)
        );
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        final var generatorContext = context.branch("2D").transform("T");
        List<GLSLStatement> result = new ArrayList<>();
        result.add(
                var(
                        "vec3",
                        generatorContext.getPointVariable(),
                        vec3(ref(context.getPointVariable(), "x"), ref(context.getPointVariable(), "y"), literal(0.0f))
                )
        );
        result.addAll(generator.gpu().evaluate(generatorContext));
        result.add(
                var("vec2", context.getContextId() + "_w", vec2(
                        ref(generatorContext.getResult()),
                        opMinus(abs(ref(context.getPointVariable(), "z")), literal(depth))
                ))
        );
        result.add(
                resultVar(
                        context,
                        opPlus(
                                min(
                                        literal(0.0f),
                                        max(
                                                ref(context.getContextId() + "_w", "x"),
                                                ref(context.getContextId() + "_w", "y")
                                        )
                                ),
                                length(
                                        max(
                                                vec2(0.0f, 0.0f),
                                                ref(context.getContextId() + "_w")
                                        )
                                )
                        )
                )
        );
        return result;
    }
}
