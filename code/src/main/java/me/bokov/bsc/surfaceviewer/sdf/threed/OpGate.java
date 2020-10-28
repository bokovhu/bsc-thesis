package me.bokov.bsc.surfaceviewer.sdf.threed;

import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.block;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.cmpLt;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.literal;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.opAssign;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.ref;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.resultVar;
import static me.bokov.bsc.surfaceviewer.glsl.GLSLPoet.var;

import java.util.ArrayList;
import java.util.List;
import me.bokov.bsc.surfaceviewer.glsl.GLSLBinaryExpressionStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLIfStatement;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GLSLDistanceExpression3D;
import me.bokov.bsc.surfaceviewer.sdf.PerPointSDFGenerator3D;
import org.joml.Vector3f;

public class OpGate implements GLSLDistanceExpression3D, PerPointSDFGenerator3D {

    private final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> boundary;
    private final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator;

    public OpGate(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> boundary,
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> generator
    ) {
        this.boundary = boundary;
        this.generator = generator;
    }

    @Override
    public List<GLSLStatement> evaluate(ExpressionEvaluationContext context
    ) {

        ExpressionEvaluationContext boundaryVolumeContext = context.branch("BoundingVolume");
        ExpressionEvaluationContext generatorContext = context.branch("Generator");
        List<GLSLStatement> generatorStatements = new ArrayList<>(
                boundary.gpu().evaluate(generatorContext)
        );
        generatorStatements.add(
                new GLSLBinaryExpressionStatement(
                        ref(context.getResult()),
                        ref(generatorContext.getResult()),
                        "="
                )
        );
        List<GLSLStatement> boundaryVolumeStatement = new ArrayList<>(
                boundary.gpu().evaluate(boundaryVolumeContext)
        );

        List<GLSLStatement> result = new ArrayList<>();
        result.addAll(boundaryVolumeStatement);
        result.add(
                var(
                        "float", context.getContextId() + "_BoundaryValue",
                        ref(boundaryVolumeContext.getResult())
                )
        );
        result.add(
                resultVar(
                        context,
                        ref(boundaryVolumeContext.getResult())
                )
        );
        result.add(
                new GLSLIfStatement(
                        cmpLt(ref(boundaryVolumeContext.getResult()), literal(0.0f)),
                        generatorStatements,
                        block(
                                opAssign(ref(context.getResult()), literal(1f))
                        )
                )
        );

        return result;
    }

    @Override
    public float getAt(float x, float y, float z) {

        float boundaryValue = boundary.cpu().evaluate(new Vector3f(x, y, z));
        if (boundaryValue > 0.0f) {
            return boundaryValue;
        }

        return generator.cpu().evaluate(new Vector3f(x, y, z));
    }

    @Override
    public String getKind() {
        return "SDFGate";
    }
}
