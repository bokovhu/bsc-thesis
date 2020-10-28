package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.render.Lighting;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.threed.ExpressionEvaluationContext;
import org.joml.Vector3f;

public final class AppScene {

    private final Evaluatable<Float, Vector3f, ExpressionEvaluationContext> sdfGenerator;
    private final Lighting lighting;

    public AppScene(
            Evaluatable<Float, Vector3f, ExpressionEvaluationContext> sdfGenerator,
            Lighting lighting
    ) {
        this.sdfGenerator = sdfGenerator;
        this.lighting = lighting;
    }

    public Evaluatable<Float, Vector3f, ExpressionEvaluationContext> sdf() {
        return sdfGenerator;
    }

    public Lighting lighting() {
        return lighting;
    }

}
