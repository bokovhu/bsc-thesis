package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpInifiniteRepetition implements CPUEvaluator<Float, CPUContext>,
        GPUEvaluator<GPUContext>,
        Serializable {

    private final Vector3f period;
    private final Evaluatable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpP = new Vector3f();

    public OpInifiniteRepetition(
            Vector3f period,
            Evaluatable<Float, CPUContext, GPUContext> generator
    ) {
        this.period = period;
        this.generator = generator;
    }

    @Override
    public Float evaluate(CPUContext c) {
        final Vector3f p = c.getPoint();
        return this.generator.cpu().evaluate(
                c.transform(
                        tmpP.set(
                                ((p.x + 0.5f * period.x) % period.x) - 0.5f * period.x,
                                ((p.y + 0.5f * period.y) % period.y) - 0.5f * period.y,
                                ((p.z + 0.5f * period.z) % period.z) - 0.5f * period.z
                        )
                )
        );
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {

        final GPUContext generatorContext = context.branch("0")
                .transform("InfinitelyRepeated");
        final List<GLSLStatement> generated = generator.gpu()
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                var(
                        "vec3",
                        generatorContext.getPointVariable(),
                        opMinus(
                                mod(
                                        opPlus(
                                                ref(context.getPointVariable()),
                                                opMul(
                                                        literal(0.5f),
                                                        vec3(period)
                                                )
                                        ),
                                        vec3(period)
                                ),
                                opMul(literal(0.5f), vec3(period))
                        )
                )
        );
        result.addAll(generated);
        result.add(
                resultVar(context, ref(generatorContext.getResult()))
        );

        return result;

    }
}
