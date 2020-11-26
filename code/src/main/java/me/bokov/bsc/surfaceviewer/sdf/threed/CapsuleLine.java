package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import me.bokov.bsc.surfaceviewer.util.MathUtil;
import org.joml.Vector3f;

import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class CapsuleLine implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final Vector3f a, b;
    private final float radius;

    private final Vector3f tmpPA = new Vector3f(), tmpBA = new Vector3f(), tmp = new Vector3f();

    public CapsuleLine(Vector3f a, Vector3f b, float radius) {
        this.a = a;
        this.b = b;
        this.radius = radius;
    }

    @Override
    public Float evaluate(CPUContext context) {
        tmpPA.set(context.getPoint()).sub(a);
        tmpBA.set(b).sub(a);
        float h = MathUtil.clamp(
                tmp.set(tmpPA).dot(tmpBA) / (tmp.set(tmpBA).dot(tmpBA)), 0.0f, 1.0f
        );
        return tmp.set(tmpPA).sub(tmpBA.mul(h)).length() - radius;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                var("vec3", context.getContextId() + "_PA", opMinus(ref(context.getPointVariable()), vec3(a))),
                var("vec3", context.getContextId() + "_BA", opMinus(vec3(b), vec3(a))),
                var("float", context.getContextId() + "_h", clamp(
                        opDiv(
                                dot(ref(context.getContextId() + "_PA"), ref(context.getContextId() + "_BA")),
                                dot(ref(context.getContextId() + "_BA"), ref(context.getContextId() + "_BA"))
                        ),
                        literal(0.0f), literal(1.0f)
                )),
                resultVar(
                        context,
                        opMinus(length(opMinus(
                                ref(context.getContextId() + "_PA"),
                                opMul(ref(context.getContextId() + "_BA"), ref(context.getContextId() + "_h"))
                        )), literal(radius))
                )
        );
    }
}
