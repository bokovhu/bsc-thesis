package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opDiv;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMul;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;

import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Vector3f;

public class OpScale implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    private final float scale;
    private final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator;

    public OpScale(float scale, Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator
    ) {
        this.scale = scale;
        this.generator = generator;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        final ExpressionEvaluationContext generatorContext = context.branch("0")
                .transform("Scaled");
        final List<GLSLStatement> generated = generator.gpu()
                .evaluate(generatorContext);

        final List<GLSLStatement> result = new ArrayList<>();
        result.add(
                var(
                        "vec3", generatorContext.getPointVariable(),
                        opDiv(ref(context.getPointVariable()), literal(scale))
                )
        );
        result.addAll(generated);
        result.add(
                resultVar(context, opMul(ref(generatorContext.getResult()), literal(scale)))
        );

        return result;
    }

    @Override
    public float getAt(float x, float y, float z) {
        return generator.cpu().evaluate(new Vector3f(x, y, z).div(scale)) * scale;
    }

    @Override
    public String getKind() {
        return "SDFScale";
    }
}
