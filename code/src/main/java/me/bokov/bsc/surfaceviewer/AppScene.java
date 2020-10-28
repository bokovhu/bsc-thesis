package me.bokov.bsc.surfaceviewer;

import me.bokov.bsc.surfaceviewer.render.Lighting;
import me.bokov.bsc.surfaceviewer.sdf.CPUContext;
import me.bokov.bsc.surfaceviewer.sdf.Evaluatable;
import me.bokov.bsc.surfaceviewer.sdf.GPUContext;
import me.bokov.bsc.surfaceviewer.sdf.threed.GPUEvaluationContext;
import org.joml.Vector3f;

public final class AppScene {

    private final Evaluatable<Float, CPUContext, GPUContext> sdfGenerator;
    private final Lighting lighting;

    public AppScene(
            Evaluatable<Float, CPUContext, GPUContext> sdfGenerator,
            Lighting lighting
    ) {
        this.sdfGenerator = sdfGenerator;
        this.lighting = lighting;
    }

    public Evaluatable<Float, CPUContext, GPUContext> sdf() {
        return sdfGenerator;
    }

    public Lighting lighting() {
        return lighting;
    }

}
