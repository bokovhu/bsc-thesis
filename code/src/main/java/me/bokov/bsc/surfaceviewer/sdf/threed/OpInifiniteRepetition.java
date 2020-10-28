package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.mod;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMul;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opPlus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.vec3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector3f;

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
