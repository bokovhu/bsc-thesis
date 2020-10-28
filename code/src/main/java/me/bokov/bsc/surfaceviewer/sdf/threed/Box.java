package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.abs;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.length;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.max;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.min;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opPlus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.vec3;

import java.io.Serializable;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import org.joml.Vector3f;

public class Box implements CPUEvaluator<Float, Vector3f>, GLSLDistanceExpression3D, Serializable {

    private final Vector3f dimensions;

    private final Vector3f tempQ = new Vector3f();
    private final Vector3f tempLen = new Vector3f();
    private final Vector3f v3Zero = new Vector3f(0f, 0f, 0f);

    public Box(Vector3f dims) {
        this.dimensions = dims;
    }

    @Override
    public List<GLSLStatement> evaluate(
            ExpressionEvaluationContext context
    ) {
        final GLSLVariableDeclarationStatement q = var(
                "vec3", context.getContextId() + "_q",
                opMinus(abs(ref(context.getPointVariable())), vec3(dimensions))
        );
        return List.of(
                q,
                resultVar(context, opPlus(
                        length(max(ref(q.name()), literal(0.0f))),
                        min(
                                max(
                                        max(ref(q.name(), "x"), ref(q.name(), "y")),
                                        ref(q.name(), "z")
                                ), literal(0.0f))
                ))
        );
    }

    @Override
    public Float evaluate(Vector3f p) {
        tempQ.set(p).absolute()
                .sub(dimensions);

        float part1 = tempLen.set(tempQ).max(v3Zero).length();
        float part2 = Math.min(Math.max(tempQ.x, Math.max(tempQ.y, tempQ.z)), 0.0f);

        return part1 + part2;
    }

}
