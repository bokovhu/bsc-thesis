package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class CappedCylinder implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final float height, radius;

    public CappedCylinder(float height, float radius) {
        this.height = height;
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        final GLSLVariableDeclarationStatement d = var(
                "vec2", context.getContextId() + "_D", opMinus(
                        abs(vec2(
                                length(ref(context.getPointVariable(), "xz")),
                                ref(context.getPointVariable(), "y")
                        )),
                        vec2(radius, height)
                ));
        return List.of(
                d,
                resultVar(
                        context,
                        opPlus(
                                min(max(ref(d.name(), "x"), ref(d.name(), "y")), literal(0.0f)),
                                length(max(ref(d.name()), literal(0.0f)))
                        )
                )
        );
    }

    @Override
    public Float evaluate(CPUContext c) {
        final Vector3f p = c.getPoint();
        Vector2f d = new Vector2f(
                Vector2f.length(p.x, p.z),
                p.y
        ).absolute().sub(radius, height);
        return Math.min(0.0f, Math.max(d.x, d.y)) + d.max(new Vector2f(0f, 0f))
                .length();
    }

}
