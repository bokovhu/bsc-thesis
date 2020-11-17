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

public class CappedCone implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final Vector3f a;
    private final float aRadius;
    private final Vector3f b;
    private final float bRadius;

    private final Vector3f tmp1 = new Vector3f();
    private final Vector3f tmp2 = new Vector3f();

    public CappedCone(Vector3f a, float aRadius, Vector3f b, float bRadius) {
        this.a = a;
        this.aRadius = aRadius;
        this.b = b;
        this.bRadius = bRadius;
    }

    @Override
    public Float evaluate(CPUContext context) {

        float rba = bRadius - aRadius;
        tmp1.set(b).sub(a);
        float baba = tmp1.dot(tmp1.x, tmp1.y, tmp1.z);
        tmp1.set(context.getPoint()).sub(a);
        float papa = tmp1.dot(tmp1.x, tmp1.y, tmp1.z);
        float paba = tmp1.dot(tmp2.set(b).sub(a)) / baba;

        float x = (float) Math.sqrt(papa - paba * paba * baba);
        float cax = Math.max(0.0f, x - ((paba < 0.5f) ? aRadius : bRadius));
        float cay = Math.abs(paba - 0.5f) - 0.5f;
        float k = rba * rba + baba;
        float f = MathUtil.clamp(
                (rba * (x - aRadius) + paba * baba) / k,
                0f, 1f
        );
        float cbx = x - aRadius - f * rba;
        float cby = paba - f;
        float s = (cbx < 0.0f && cay < 0.0f) ? -1.0f : 1.0f;
        return s * (float) Math.sqrt(
                Math.min(
                        cax * cax + cay * cay * baba,
                        cbx * cbx + cby * cby * baba
                )
        );

    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        return List.of(
                resultVar(
                        context,
                        fn(
                                "CSG_DistanceToCappedCone",
                                ref(context.getPointVariable()),
                                vec3(a), vec3(b), literal(aRadius), literal(bRadius)
                        )
                )
        );
    }
}
