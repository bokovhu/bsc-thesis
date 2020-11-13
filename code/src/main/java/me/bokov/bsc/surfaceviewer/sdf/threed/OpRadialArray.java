package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpRadialArray implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final int count;
    private final float fromAngleDeg, toAngleDeg;
    private final Vector3f axis;
    private final Evaluable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpTranslate = new Vector3f();
    private final Quaternionf tmpQ = new Quaternionf();

    public OpRadialArray(
            int count,
            float fromAngleDeg,
            float toAngleDeg,
            Vector3f axis, Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        this.count = count;
        this.fromAngleDeg = fromAngleDeg;
        this.toAngleDeg = toAngleDeg;
        this.axis = axis;
        this.generator = generator;
    }

    @Override
    public Float evaluate(CPUContext context) {

        float angleStep = (toAngleDeg - fromAngleDeg) / (float) count;

        boolean didEvaluate = false;
        float result = 0.0f;

        for (int i = 0; i < count; i++) {

            float angleDeg = fromAngleDeg + (float) i * angleStep;

            tmpQ.identity()
                    .fromAxisAngleDeg(axis.normalize(), angleDeg);

            tmpTranslate.set(context.getPoint())
                    .rotate(tmpQ);

            final float generated = generator.cpu().evaluate(context.transform(tmpTranslate));

            result = !didEvaluate
                    ? generated
                    : Math.min(result, generated);
            didEvaluate = true;

        }

        return result;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {
        List<GLSLStatement> generated = new ArrayList<>();
        List<GLSLStatement> result = new ArrayList<>();
        List<GPUContext> contexts = new ArrayList<>();

        float angleStep = (toAngleDeg - fromAngleDeg) / (float) count;

        for (int i = 0; i < count; i++) {
            float angleDeg = fromAngleDeg + (float) i * angleStep;

            final var generatorContext = context.branch("I" + i);
            contexts.add(generatorContext);

            final var rotate = new OpRotate(
                    new Quaternionf().fromAxisAngleDeg(axis.normalize(), angleDeg),
                    generator
            );

            generated.addAll(rotate.evaluate(generatorContext));

        }
        result.addAll(generated);
        result.add(
                resultVar(
                        context,
                        min(contexts.stream().map(c -> ref(c.getResult())).collect(Collectors.toList()))
                )
        );
        return result;
    }
}
