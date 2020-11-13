package me.bokov.bsc.surfaceviewer.sdf.twod;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector2f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class Disk2D implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final float radius;

    public Disk2D(float radius) {
        this.radius = radius;
    }

    @Override
    public Float evaluate(CPUContext context) {
        return Vector2f.length(context.getPoint().x, context.getPoint().y) - radius;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                resultVar(
                        context,
                        opMinus(
                                length(ref(context.getPointVariable(), "xy")),
                                literal(radius)
                        )
                )
        );
    }
}
