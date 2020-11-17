package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class Plane implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final Vector3f normal;
    private final float h;

    private final Vector3f tmp = new Vector3f();

    public Plane(Vector3f normal, float h) {
        this.normal = normal;
        this.h = h;
    }

    @Override
    public Float evaluate(CPUContext context) {
        return tmp.set(context.getPoint())
                .dot(normal) + h;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                resultVar(
                        context,
                        opPlus(
                                dot(ref(context.getPointVariable()), vec3(normal)),
                                literal(h)
                        )
                )
        );
    }
}
