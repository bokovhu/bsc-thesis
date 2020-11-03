package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class SimpleNoise implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private static final String GLSL_FN_NAME = "CSG_Noise_Simple3";

    private final float bias;
    private final float scale;
    private final float offset;
    private final float power;

    public SimpleNoise(float bias, float scale, float offset, float power) {
        this.bias = bias;
        this.scale = scale;
        this.offset = offset;
        this.power = power;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                resultVar(
                        context,
                        pow(
                                opPlus(
                                        literal(offset),
                                        opMul(
                                                literal(scale),
                                                paren(opPlus(
                                                        literal(bias),
                                                        fn(
                                                                GLSL_FN_NAME,
                                                                ref(context.getPointVariable())
                                                        )
                                                ))
                                        )
                                ),
                                literal(power)
                        )
                )
        );
    }

    // TODO: Make CPU implementation
    @Override
    public Float evaluate(CPUContext c) {
        return 0.0f;
    }

}
