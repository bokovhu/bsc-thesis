package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.vec3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.CPUEvaluator;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.GPUEvaluator;
import org.joml.Vector3f;

public class OpTranslateTo implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext>,
        Serializable {

    private final Vector3f position;
    private final Evaluatable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpP = new Vector3f();

    public OpTranslateTo(Vector3f position,
            Evaluatable<Float, CPUContext, GPUContext> generator
    ) {
        this.position = position;
        this.generator = generator;
    }


    @Override
    public Float evaluate(CPUContext c) {
        return generator.cpu().evaluate(
                c.transform(
                    tmpP.set(c.getPoint()).sub(position)
                )
        );
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
                        "vec3", generatorContext.getPointVariable(),
                        opMinus(ref(context.getPointVariable()), vec3(position))
                )
        );
        result.addAll(generated);
        result.add(
                resultVar(context, ref(generatorContext.getResult()))
        );

        return result;
    }
}
