package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpTransform implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>, Serializable {

    private final Matrix4f transformation;
    private final Evaluatable<Float, CPUContext, GPUContext> generator;
    private final Matrix4f tInv;

    private final Vector4f tmpP4 = new Vector4f();
    private final Vector3f tmpP = new Vector3f();

    public OpTransform(
            Matrix4f transformation,
            Evaluatable<Float, CPUContext, GPUContext> generator
    ) {
        this.transformation = transformation;
        this.generator = generator;
        this.tInv = new Matrix4f(this.transformation)
                .invert();
    }

    @Override
    public Float evaluate(CPUContext context) {
        tmpP4.set(context.getPoint(), 1.0f);
        tInv.transform(tmpP4);
        tmpP.set(tmpP4.x, tmpP4.y, tmpP4.z);

        return generator.cpu()
                .evaluate(context.transform(tmpP));
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {

        final GPUContext generatorContext = context.branch("0")
                .transform("Translated");
        final List<GLSLStatement> generated = generator.gpu()
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                var(
                        "mat4", context.getContextId() + "_Transformation",
                        mat4(tInv)
                )
        );
        result.add(
                var(
                        "vec4", context.getContextId() + "_Transformed4",
                        paren(
                                opMul(
                                        ref(context.getContextId() + "_Transformation"),
                                        vec4(
                                                ref(context.getPointVariable(), "x"),
                                                ref(context.getPointVariable(), "y"),
                                                ref(context.getPointVariable(), "z"),
                                                literal(1.0f)
                                        )
                                )
                        )
                )
        );
        result.add(
                var(
                        "vec3", generatorContext.getPointVariable(),
                        ref(
                                context.getContextId() + "_Transformed4",
                                "xyz"
                        )
                )
        );
        result.addAll(generated);
        result.add(
                resultVar(context, ref(generatorContext.getResult()))
        );

        return result;
    }

}
