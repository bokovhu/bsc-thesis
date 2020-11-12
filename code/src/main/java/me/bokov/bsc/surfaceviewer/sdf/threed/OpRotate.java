package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpRotate implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final Quaternionf orientation;
    private final Evaluable<Float, CPUContext, GPUContext> generator;
    private final Vector3f tmp = new Vector3f();


    public OpRotate(
            Quaternionf orientation,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        this.orientation = new Quaternionf(orientation);
        this.generator = generator;
    }

    @Override
    public Float evaluate(CPUContext c) {
        final Vector3f p = c.getPoint();
        orientation.transform(p, tmp);
        return generator.cpu().evaluate(c.transform(tmp));
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        final GPUContext generatorContext = context.branch("0")
                .transform("Rotated");
        final List<GLSLStatement> generated = generator.gpu()
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        final GLSLVariableDeclarationStatement q = var(
                "vec4", context.getContextId() + "_Q", vec4(orientation));
        result.add(q);
        result.add(
                var("vec3", generatorContext.getPointVariable(), opPlus(
                        ref(context.getPointVariable()),
                        opMul(
                                literal(2.0f),
                                cross(
                                        ref(q.name(), "xyz"),
                                        opPlus(
                                                cross(
                                                        ref(q.name(), "xyz"),
                                                        ref(context.getPointVariable())
                                                ),
                                                opMul(
                                                        ref(q.name(), "w"),
                                                        ref(context.getPointVariable())
                                                )
                                        )
                                )
                        )
                ))
        );
        result.addAll(generated);
        result.add(
                resultVar(context, ref(generatorContext.getResult()))
        );

        return result;
    }
}
