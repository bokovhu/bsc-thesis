package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMinus;
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

public class OpTranslateTo implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    private final Vector3f position;
    private final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator;

    public OpTranslateTo(Vector3f position, Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator) {
        this.position = position;
        this.generator = generator;
    }


    @Override
    public float getAt(float x, float y, float z) {
        return generator.cpu().evaluate(
                new Vector3f(x - position.x, y - position.y, z - position.z)
        );
    }

    @Override
    public String getKind() {
        return "SDFTranslateTo";
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context) {

        final ExpressionEvaluationContext generatorContext = context.branch("0")
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
