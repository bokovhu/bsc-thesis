package me.bokov.bsc.surfaceviewer.sdf.twod;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector2f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class Box2D implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final Vector2f bounds;

    private final Vector2f tmpD = new Vector2f();

    public Box2D(Vector2f bounds) {
        this.bounds = bounds;
    }

    @Override
    public Float evaluate(CPUContext context) {
        tmpD.set(context.getPoint().x, context.getPoint().y).absolute()
                .sub(bounds);
        return Vector2f.length(
                Math.max(tmpD.x, 0.0f),
                Math.max(tmpD.y, 0.0f)
        ) + Math.min(0.0f, Math.max(tmpD.x, tmpD.y));
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                var(
                        "vec2",
                        context.getContextId() + "_d",
                        opMinus(abs(ref(context.getPointVariable(), "xy")), vec2(bounds))
                ),
                resultVar(
                        context,
                        opPlus(
                                length(max(ref(context.getContextId() + "_d"), vec2(0.0f, 0.0f))),
                                min(
                                        max(
                                                ref(context.getContextId() + "_d", "x"),
                                                ref(context.getContextId() + "_d", "y")
                                        ),
                                        literal(0.0f)
                                )
                        )
                )
        );
    }
}
