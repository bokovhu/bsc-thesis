package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.mod;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMul;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opPlus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.vec3;

import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Vector3f;

public class OpInifiniteRepetition implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    private final Vector3f period;
    private final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator;

    public OpInifiniteRepetition(
            Vector3f period,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator
    ) {
        this.period = period;
        this.generator = generator;
    }

    @Override
    public float getAt(float x, float y, float z) {
        return this.generator.cpu().evaluate(
                new Vector3f(
                        ((x + 0.5f * period.x) % period.x) - 0.5f * period.x,
                        ((y + 0.5f * period.y) % period.y) - 0.5f * period.y,
                        ((z + 0.5f * period.z) % period.z) - 0.5f * period.z
                )
        );
    }

    @Override
    public String getKind() {
        return "SDFInfiniteRepetition";
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {

        final ExpressionEvaluationContext generatorContext = context.branch("0")
                .transform("InfinitelyRepeated");
        final List<GLSLStatement> generated = generator.gpu()
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                var(
                        "vec3",
                        generatorContext.getPointVariable(),
                        opMinus(
                                mod(
                                        opPlus(
                                                ref(context.getPointVariable()),
                                                opMul(
                                                        literal(0.5f),
                                                        vec3(period)
                                                )
                                        ),
                                        vec3(period)
                                ),
                                opMul(literal(0.5f), vec3(period))
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
