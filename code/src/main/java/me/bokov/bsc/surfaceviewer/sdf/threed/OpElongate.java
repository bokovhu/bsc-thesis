package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import me.bokov.bsc.surfaceviewer.util.MathUtil;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpElongate implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final Vector3f axis;
    private final Evaluable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpQ = new Vector3f();

    public OpElongate(
            Vector3f axis,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        this.axis = axis;
        this.generator = generator;
    }

    @Override
    public Float evaluate(CPUContext context) {
        tmpQ.set(context.getPoint())
                .sub(
                        MathUtil.clamp(context.getPoint().x, -axis.x, axis.x),
                        MathUtil.clamp(context.getPoint().y, -axis.y, axis.y),
                        MathUtil.clamp(context.getPoint().z, -axis.z, axis.z)
                );
        return generator.cpu().evaluate(context.transform(tmpQ));
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        List<GLSLStatement> result = new ArrayList<>();
        final var generatorContext = context.transform("Elongated");
        result.add(var(
                "vec3",
                generatorContext.getPointVariable(),
                opMinus(
                        ref(context.getPointVariable()),
                        clamp(ref(context.getPointVariable()), opMul(vec3(axis), literal(-1.0f)), vec3(axis))
                )
        ));
        result.addAll(generator.gpu().evaluate(generatorContext));
        result.add(resultVar(context, ref(generatorContext.getResult())));
        return result;
    }
}
