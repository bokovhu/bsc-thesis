package me.bokov.bsc.surfaceviewer.sdf.threed;

import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.*;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.*;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.*;

public class OpArray implements CPUEvaluator<Float, CPUContext>, GPUEvaluator<GPUContext> {

    private final int countX, countY, countZ;
    private final Vector3f offset;
    private final Evaluable<Float, CPUContext, GPUContext> generator;

    private final Vector3f tmpP = new Vector3f();

    public OpArray(
            int countX,
            int countY,
            int countZ,
            Vector3f offset,
            Evaluable<Float, CPUContext, GPUContext> generator
    ) {
        this.countX = countX;
        this.countY = countY;
        this.countZ = countZ;
        this.offset = offset;
        this.generator = generator;
    }

    @Override
    public Float evaluate(CPUContext context) {

        boolean didEvaluate = false;
        float result = 1.0f;

        for (int z = 0; z < countZ; z++) {
            for (int y = 0; y < countY; y++) {
                for (int x = 0; x < countX; x++) {

                    float generated = generator.cpu().evaluate(
                            context.transform(
                                    tmpP.set(
                                            context.getPoint().x + x * offset.x,
                                            context.getPoint().y + y * offset.y,
                                            context.getPoint().z + z * offset.z
                                    )
                            )
                    );

                    result = !didEvaluate
                            ? generated
                            : Math.min(result, generated);
                    didEvaluate = true;

                }
            }
        }

        return result;
    }

    @Override
    public List<GLSLStatement> evaluate(GPUContext context) {

        List<GLSLStatement> generated = new ArrayList<>();
        List<GLSLStatement> result = new ArrayList<>();
        List<GPUContext> contexts = new ArrayList<>();

        for (int z = 0; z < countZ; z++) {
            for (int y = 0; y < countY; y++) {
                for (int x = 0; x < countX; x++) {

                    final var generatorContext = context.branch("Z" + z + "Y" + y + "X" + x)
                            .transform("Off");
                    contexts.add(generatorContext);
                    final var pointVariable = var("vec3", generatorContext.getPointVariable(), vec3(
                            opPlus(ref(context.getPointVariable(), "x"), opMul(literal((float) x), literal(offset.x))),
                            opPlus(ref(context.getPointVariable(), "y"), opMul(literal((float) y), literal(offset.y))),
                            opPlus(ref(context.getPointVariable(), "z"), opMul(literal((float) z), literal(offset.z)))
                    ));

                    result.add(pointVariable);
                    generated.addAll(generator.gpu().evaluate(generatorContext));

                }
            }
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
