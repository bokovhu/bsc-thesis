package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.cross;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opMul;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opPlus;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.vec4;

import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLVariableDeclarationStatement;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OpRotate implements PerPointSDFGenerator3D, GLSLDistanceExpression3D {

    private final Quaternionf orientation;
    private final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator;
    private final Vector3f tmp = new Vector3f();


    public OpRotate(
            Quaternionf orientation,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator
    ) {
        this.orientation = orientation;
        this.generator = generator;
    }

    @Override
    public float getAt(float x, float y, float z) {
        orientation.transform(x, y, z, tmp);
        return generator.cpu().evaluate(tmp);
    }

    @Override
    public String getKind() {
        return "SDFRotate";
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {
        final ExpressionEvaluationContext generatorContext = context.branch("0")
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
