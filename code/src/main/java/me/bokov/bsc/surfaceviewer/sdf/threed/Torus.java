package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.length;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.vec2;

import java.io.Serializable;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Torus implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final Vector2f radius;

    private final Vector2f tmpQ = new Vector2f();

    public Torus(Vector2f radius) {
        this.radius = radius;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        final GLSLVariableDeclarationStatement q = var(
                "vec2", context.getContextId() + "_Q",
                vec2(
                        opMinus(length(ref(context.getPointVariable(), "xz")), literal(radius.x)),
                        ref(context.getPointVariable(), "y")
                )
        );
        return List.of(
                q,
                resultVar(
                        context,
                        opMinus(length(ref(q.name())), literal(radius.y))
                )
        );
    }

    @Override
    public Float evaluate(CPUContext ctx) {
        final Vector3f p = ctx.getPoint();
        tmpQ.set(
                Vector2f.length(p.x, p.z) - radius.x,
                p.y
        );
        return tmpQ.length() - radius.y;
    }

}
