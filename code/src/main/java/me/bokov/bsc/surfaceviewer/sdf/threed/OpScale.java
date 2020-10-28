package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opDiv;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMul;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;

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

public class OpScale implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final float scale;
    private final Evaluatable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpP = new Vector3f();

    public OpScale(float scale, Evaluatable<Float, CPUContext, GPUContext> generator
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
